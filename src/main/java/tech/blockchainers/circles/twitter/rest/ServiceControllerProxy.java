package tech.blockchainers.circles.twitter.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.utils.Numeric;
import tech.blockchainers.Hub;
import tech.blockchainers.circles.twitter.rest.dto.AccountDto;
import tech.blockchainers.circles.twitter.rest.dto.SignatureDto;
import tech.blockchainers.circles.twitter.service.SignatureService;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

@RestController
public class ServiceControllerProxy {

    private final SignatureService signatureService;
    private final Credentials serviceCredentials;

    @Value("${signature.message}")
    private String message;

    @Value("${twitter.hashtag}")
    private String hashtag;

    private final Hub hub;

    public ServiceControllerProxy(SignatureService signatureService, Credentials serviceCredentials, Hub hub) {
        this.signatureService = signatureService;
        this.serviceCredentials = serviceCredentials;
        this.hub = hub;
    }

    @PostMapping("/createAccount")
    public AccountDto createAccount() throws Exception {
        Credentials signer = create();
        return new AccountDto(Numeric.toHexStringWithPrefix(signer.getEcKeyPair().getPrivateKey()), signer.getAddress());
    }

    @GetMapping("/signMessage")
    public SignatureDto signMessage(@RequestParam String privateKey) {
        Credentials signer = Credentials.create(privateKey);
        String messageToSign = this.message.replaceAll("#addr#", signer.getAddress().toLowerCase()).replaceAll("#botaddr#", hub.getContractAddress().toLowerCase());
        String signature = signatureService.sign(messageToSign.getBytes(StandardCharsets.UTF_8), signer);
        return new SignatureDto(messageToSign, signature, "#" + hashtag + " " + signer.getAddress() + " " + signature);
    }

    public Credentials create() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        ECKeyPair keyPair = Keys.createEcKeyPair();
        BigInteger publicKey = keyPair.getPublicKey();
        BigInteger privateKey = keyPair.getPrivateKey();
        return Credentials.create(new ECKeyPair(privateKey, publicKey));
    }

}
