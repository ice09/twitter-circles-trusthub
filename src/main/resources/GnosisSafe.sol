contract Enum {
    enum Operation {
        Call,
        DelegateCall
    }
}

contract GnosisSafe {

    string public constant NAME = "Gnosis Safe";
    string public constant VERSION = "1.2.0";

    //keccak256(
    //    "EIP712Domain(address verifyingContract)"
    //);
    bytes32 private constant DOMAIN_SEPARATOR_TYPEHASH = 0x035aff83d86937d35b32e04f0ddc6ff469290eef2f1b692d8a815c89404d4749;

    //keccak256(
    //    "SafeTx(address to,uint256 value,bytes data,uint8 operation,uint256 safeTxGas,uint256 baseGas,uint256 gasPrice,address gasToken,address refundReceiver,uint256 nonce)"
    //);
    bytes32 private constant SAFE_TX_TYPEHASH = 0xbb8310d486368db6bd6f849402fdd73ad53d316b5a4b2644ad6efe0f941286d8;

    //keccak256(
    //    "SafeMessage(bytes message)"
    //);
    bytes32 private constant SAFE_MSG_TYPEHASH = 0x60b3cbf8b4a223d68d641b3b6ddf9a298e7f33710cf3d3a9d1146b5a6150fbca;

    event ApproveHash(
        bytes32 indexed approvedHash,
        address indexed owner
    );
    event SignMsg(
        bytes32 indexed msgHash
    );
    event ExecutionFailure(
        bytes32 txHash, uint256 payment
    );
    event ExecutionSuccess(
        bytes32 txHash, uint256 payment
    );

    uint256 public nonce;
    bytes32 public domainSeparator;

    constructor() public {
        require(domainSeparator == 0, "Domain Separator already set!");
        domainSeparator = keccak256(abi.encode(DOMAIN_SEPARATOR_TYPEHASH, this));
    }

    function getOwners() public view returns (address[] memory) {
        address[] memory array = new address[](0);
        return array;
    }

    function execTransaction(
        address to,
        uint256 value,
        bytes data,
        Enum.Operation operation,
        uint256 safeTxGas,
        uint256 baseGas,
        uint256 gasPrice,
        address gasToken,
        address refundReceiver,
        bytes signatures
    )
    external
    payable
    returns (bool success)
    {
        bytes32 txHash;
        // Use scope here to limit variable lifetime and prevent `stack too deep` errors

        bytes memory txHashData = encodeTransactionData(
            to, value, data, operation, // Transaction info
            safeTxGas, baseGas, gasPrice, gasToken, refundReceiver, // Payment info
            nonce
        );
        // Increase nonce and execute transaction.
        nonce++;
        txHash = keccak256(txHashData);
        checkSignatures(txHash, txHashData, signatures, true);
    }

    function encodeTransactionData(
        address to,
        uint256 value,
        bytes memory data,
        Enum.Operation operation,
        uint256 safeTxGas,
        uint256 baseGas,
        uint256 gasPrice,
        address gasToken,
        address refundReceiver,
        uint256 _nonce
    )
    public
    view
    returns (bytes memory)
    {
        bytes32 safeTxHash = keccak256(
            abi.encode(SAFE_TX_TYPEHASH, to, value, keccak256(data), operation, safeTxGas, baseGas, gasPrice, gasToken, refundReceiver, _nonce)
        );
        return abi.encodePacked(byte(0x19), byte(0x01), domainSeparator, safeTxHash);
    }

    function getMessageHash(
        bytes memory message
    )
    public
    view
    returns (bytes32)
    {
        bytes32 safeMessageHash = keccak256(
            abi.encode(SAFE_MSG_TYPEHASH, keccak256(message))
        );
        return keccak256(
            abi.encodePacked(byte(0x19), byte(0x01), domainSeparator, safeMessageHash)
        );
    }

    function checkSignatures(bytes32 dataHash, bytes memory data, bytes memory signatures, bool consumeHash)
    internal
    {
        address lastOwner = address(0);
        address currentOwner;
        uint8 v;
        bytes32 r;
        bytes32 s;
        uint256 i;
        (v, r, s) = signatureSplit(signatures, 0);
        // If v is 0 then it is a contract signature
        if (v > 30) {
            // To support eth_sign and similar we adjust v and hash the messageHash with the Ethereum message prefix before applying ecrecover
            currentOwner = ecrecover(keccak256(abi.encodePacked("\x19Ethereum Signed Message:\n32", dataHash)), v - 4, r, s);
        } else {
            // Use ecrecover with the messageHash for EOA signatures
            currentOwner = ecrecover(dataHash, v, r, s);
        }
        require(currentOwner > 0, "current owner not set");
    }

    function signatureSplit(bytes memory signatures, uint256 pos)
    internal
    pure
    returns (uint8 v, bytes32 r, bytes32 s)
    {
        // The signature format is a compact form of:
        //   {bytes32 r}{bytes32 s}{uint8 v}
        // Compact means, uint8 is not padded to 32 bytes.
        // solium-disable-next-line security/no-inline-assembly
        assembly {
            let signaturePos := mul(0x41, pos)
            r := mload(add(signatures, add(signaturePos, 0x20)))
            s := mload(add(signatures, add(signaturePos, 0x40)))
        // Here we are loading the last 32 bytes, including 31 bytes
        // of 's'. There is no 'mload8' to do this.
        //
        // 'byte' is not working due to the Solidity parser, so lets
        // use the second best option, 'and'
            v := and(mload(add(signatures, add(signaturePos, 0x41))), 0xff)
        }
    }

}