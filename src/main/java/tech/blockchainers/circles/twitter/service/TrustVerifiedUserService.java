package tech.blockchainers.circles.twitter.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import tech.blockchainers.GnosisSafe;
import tech.blockchainers.Hub;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;

@Slf4j
@Service
public class TrustVerifiedUserService {

    private final GnosisSafe gnosisSafe;
    private final Hub hub;
    private final Credentials credentials;

    public TrustVerifiedUserService(GnosisSafe gnosisSafe, Hub hub, Credentials credentials) {
        this.gnosisSafe = gnosisSafe;
        this.hub = hub;
        this.credentials = credentials;
    }

    public String giveTrustToEthereumAddress(String ethereumAddress) throws Exception {
        /* Create signature for trust function to pass to execTrx function on safe */

        Function functionExecTrx =
                new Function("trust",
                        Arrays.asList(new Address(ethereumAddress), new Uint256(50)),
                        Collections.emptyList());
        String txTrustData = FunctionEncoder.encode(functionExecTrx);
        byte[] txTrustBytes = Hex.decodeHex(txTrustData.substring(2));

        /* Add signature as v=1 (pre-signed) */
        byte[] r = Hex.decodeHex(credentials.getAddress().substring(2));
        byte[] v = new byte[]{(byte)1};
        byte[] txSignedTranscodedTrx = new byte[65];
        System.arraycopy(r, 0, txSignedTranscodedTrx, 12, 20);
        System.arraycopy(v, 0, txSignedTranscodedTrx, 64, 1);
        log.info(Hex.encodeHexString(txSignedTranscodedTrx));

        TransactionReceipt trxhash = gnosisSafe.execTransaction(
                hub.getContractAddress(), BigInteger.ZERO, txTrustBytes,
                BigInteger.ZERO, BigInteger.valueOf(50000), BigInteger.ZERO,
                BigInteger.ZERO,"0x0", "0x0", txSignedTranscodedTrx, BigInteger.ZERO).send();
        return trxhash.getTransactionHash();
    }
}
