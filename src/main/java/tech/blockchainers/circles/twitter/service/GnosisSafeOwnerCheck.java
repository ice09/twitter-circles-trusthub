package tech.blockchainers.circles.twitter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.DefaultGasProvider;
import tech.blockchainers.GnosisSafe;

import java.util.List;

@Slf4j
@Service
public class GnosisSafeOwnerCheck {

    private final Web3j web3j;
    private final Credentials credentials;

    public GnosisSafeOwnerCheck(Web3j web3j, Credentials credentials) {
        this.web3j = web3j;
        this.credentials = credentials;
    }

    public String checkGnosisSafeOwner(String ethereumAddress) throws Exception {
        List owners = GnosisSafe.load(ethereumAddress, web3j, credentials, new DefaultGasProvider()).getOwners().send();
        return (String) owners.get(0);
        /*
        BigInteger trusted = hub.limits(gnosisSafe.getContractAddress(), "0x945CaC6047B1f58945ed2aafA5BaeD96A31faa4c").send();
        log.info("limit: {}", trusted.intValue());
        return true;
         */
    }
}
