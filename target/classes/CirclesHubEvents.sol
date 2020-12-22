pragma solidity ^0.4.23;

contract Hub {

    event Signup(address indexed user, address token);
    event OrganizationSignup(address indexed organization);
    event Trust(address indexed canSendTo, address indexed user, uint256 limit);
    event HubTransfer(address indexed from, address indexed to, uint256 amount);

    /// simutating a real "trust" call to the Hub contract
    function trust(address user, uint limit) public {
        emit Trust(msg.sender, user, limit);
    }

    mapping (address => mapping (address => uint256)) public limits;

}