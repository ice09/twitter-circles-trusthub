package tech.blockchainers.circles.twitter.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.web3j.crypto.Hash;
import org.web3j.utils.Numeric;
import tech.blockchainers.circles.twitter.persistence.RegistrationRepository;
import tech.blockchainers.circles.twitter.service.dto.TweetDto;
import tech.blockchainers.circles.twitter.service.dto.UserDto;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class TweetService {

    public static final String SIGNATURE_PATTERN = "^.+(0x[0-9a-zA-Z]+) .*?(0x[0-9a-zA-Z]+)$";
    @Value("${twitter.bearer.token}")
    private String twitterBearerToken;

    @Value("${twitter.hashtag}")
    private String hashTag;

    @Value("${signature.message}")
    private String message;

    @Value("${trusthub.contract.address}")
    private String trusthubAddress;

    private final SignatureService signatureService;
    private final RegistrationRepository registrationRepository;

    public TweetService(SignatureService signatureService, RegistrationRepository registrationRepository) {
        this.signatureService = signatureService;
        this.registrationRepository = registrationRepository;
    }

    String handleTweet(String text, String userName, String signerAddress) throws IOException, URISyntaxException {
        if (!isValidMessage(text) || !signatureDoesMatch(text, signerAddress)) {
            return text;
        }
        log.info("responseUser: " + userName);
        String ethereumAddress = extractEthereumAddress(text);
        storeTwitterUserEthereumAddressLink(userName, ethereumAddress);
        return ethereumAddress;
    }

    private void storeTwitterUserEthereumAddressLink(String username, String ethereumAddress) {
        if (!registrationRepository.isTwitterIdRegistered(username) && !registrationRepository.hasEthereumAddressBeenRegisteredAlready(ethereumAddress)) {
            registrationRepository.linkTwitterIdToEthereumAddress(username, ethereumAddress);
        } else {
            String repoUsername = username;
            String repoEthereumAddress = ethereumAddress;
            if (registrationRepository.isTwitterIdRegistered(username)) {
                repoEthereumAddress = registrationRepository.getEthereumAddressForTwitterUsername(username);
            }
            if (registrationRepository.hasEthereumAddressBeenRegisteredAlready(ethereumAddress)) {
                repoUsername = registrationRepository.getTwitterAddressForEthereumAddress(ethereumAddress);
            }
            throw new IllegalArgumentException("Twitter Name " + repoUsername + " has already been registered for Ethereum address " + repoEthereumAddress);
        }
    }

    boolean signatureDoesMatch(String text, String signerAddress) {
        Pattern pattern = Pattern.compile(SIGNATURE_PATTERN);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String address = matcher.group(1);
            String signature = matcher.group(2);
            String completeMessage = this.message.replaceAll("#addr#", address).replaceAll("#botaddr#", trusthubAddress);
            String recoveredAddress = signatureService.ecrecoverAddress(
                    Hash.sha3(signatureService.createProof(completeMessage.getBytes(StandardCharsets.UTF_8))),
                    Numeric.hexStringToByteArray(signature),
                    signerAddress
            );
            if (StringUtils.isNotEmpty(recoveredAddress) && recoveredAddress.equalsIgnoreCase(signerAddress.substring(2))) {
                log.info("Recovered address {}", address);
                return true;
            }
        }
        return false;
    }

    boolean isValidMessage(String text) {
        if (text.matches(SIGNATURE_PATTERN)) {
            log.info("Received valid message.");
            return true;
        }
        return false;
    }

    String extractEthereumAddress(String twitterText) {
        Pattern pattern = Pattern.compile(SIGNATURE_PATTERN);
        Matcher matcher = pattern.matcher(twitterText);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    TweetDto getTweets() {
        HttpEntity<?> entity = prepareHeader();
        RestTemplate restTemplate = new RestTemplate();
        UriComponentsBuilder uriBuilder =
                UriComponentsBuilder.fromHttpUrl("https://api.twitter.com/2/tweets/search/recent")
                        .queryParam("query", "#" + hashTag)
                        .queryParam("expansions", "author_id");
        ResponseEntity<TweetDto> response =
                restTemplate.exchange(
                        uriBuilder.build(Collections.emptyMap()),
                        HttpMethod.GET,
                        entity, TweetDto.class);
        if (response.getBody() == null) {
            return null;
        } else if (response.getBody().getData() == null) {
            return null;
        }
        return response.getBody();
    }

    UserDto getUser(String id) throws IOException, URISyntaxException {
        HttpEntity<?> entity = prepareHeader();
        URIBuilder uriBuilder = new URIBuilder("https://api.twitter.com/2/users/" + id);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<UserDto> response =
                restTemplate.exchange(
                        uriBuilder.build(),
                        HttpMethod.GET,
                        entity, UserDto.class);
        return response.getBody();
    }

    private HttpEntity<?> prepareHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.set("Authorization", String.format("Bearer %s", twitterBearerToken));
        HttpEntity<?> entity = new HttpEntity<>(headers);
        return entity;
    }


}
