package tech.blockchainers.circles.twitter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECDSASignature;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Slf4j
@Service
public class SignatureService {

    public String sign(byte[] message, Credentials credentials) {
        Sign.SignatureData signature = Sign.signPrefixedMessage(Hash.sha3(message), credentials.getEcKeyPair());
        ByteBuffer sigBuffer = ByteBuffer.allocate(signature.getR().length + signature.getS().length + 1);
        sigBuffer.put(signature.getR());
        sigBuffer.put(signature.getS());
        sigBuffer.put(signature.getV());
        return Numeric.toHexString(sigBuffer.array());
    }

    public byte[] createProof(byte[] hashedTrxId) {
        byte[] ethPrefixMessage = "\u0019Ethereum Signed Message:\n".concat(String.valueOf(hashedTrxId.length)).getBytes(StandardCharsets.UTF_8);
        ByteBuffer sigBuffer = ByteBuffer.allocate(ethPrefixMessage.length + hashedTrxId.length);
        sigBuffer.put(ethPrefixMessage);
        sigBuffer.put(hashedTrxId);
        return sigBuffer.array();
    }


    public String ecrecoverAddress(byte[] proof, byte[] signature, String expectedAddress) {
        ECDSASignature esig = new ECDSASignature(Numeric.toBigInt(Arrays.copyOfRange(signature, 0, 32)), Numeric.toBigInt(Arrays.copyOfRange(signature, 32, 64)));
        BigInteger res;
        for (int i=0; i<4; i++) {
            res = Sign.recoverFromSignature(i, esig, proof);
            try {
                log.debug("Recovered Address 0x{}", Keys.getAddress(res));
                String address = Keys.getAddress(res);
                if (address.equalsIgnoreCase(expectedAddress.substring(2).toLowerCase())) {
                    return Keys.getAddress(res);
                }
            } catch (Exception ex) {
                log.error("Cannot recover address.", ex);
            }
        }
        return null;
    }

}