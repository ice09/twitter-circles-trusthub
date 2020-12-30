pragma solidity ^0.4.25;

contract TrusthubRegistry {

    address public adminAddress;
    mapping(address => bool) public addressRegistry;
    mapping(bytes32 => bool) public userRegistry;

    event Registered(address indexed ethAddress);

    constructor() public {
        adminAddress = msg.sender;
    }

    function register(string user, address ethAddress) public {
        if (msg.sender != adminAddress) {
            revert("Only Admin can register Users.");
        }
        if (addressRegistry[ethAddress]) {
            revert("Address already registered.");
        }
        bytes32 hash = sha256(abi.encodePacked(user));
        if (userRegistry[hash]) {
            revert("User already registered.");
        }
        addressRegistry[ethAddress] = true;
        userRegistry[hash] = true;
        emit Registered(ethAddress);
    }

    function isAddressRegistered(address ethAddress) view public returns (bool) {
        return addressRegistry[ethAddress];
    }

    function isUserRegistered(string user) view public returns (bool) {
        return userRegistry[sha256(abi.encodePacked(user))];
    }

}
