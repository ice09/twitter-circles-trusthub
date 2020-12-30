package tech.blockchainers.circles.twitter.service;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import tech.blockchainers.circles.twitter.persistence.RegistrationMapRepository;
import tech.blockchainers.circles.twitter.service.dto.TweetContentDto;
import tech.blockchainers.circles.twitter.service.dto.UserContentDto;
import tech.blockchainers.circles.twitter.service.dto.UserDto;

import java.net.URI;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource("/trusthub.properties")
@Slf4j
@MockBean(TwitterScanner.class)
public class IntegrationTest {

    @Autowired
    private TweetService tweetService;

    @Autowired
    private TrustVerifiedUserService trustVerifiedUserService;

    @Autowired
    private GnosisSafeOwnerCheck gnosisSafeOwnerCheck;

    @MockBean
    private RegistrationMapRepository registrationMapRepository;

    // This is an integration tests and requires xDai at the address of the private key in the xDai Mainnet
    @Test
    public void trustWithSafeOnMainnet() throws Exception {
        // Message:
        // I AM 0xC29D7Ab348b2dA3B59eE80A8492bEDFaDf350AEF AND WANT 0xA485e9295ef16143891F4A0d77C060E87EA59C87 TO TRUST ME
        String tweetText = "#circles_trusthub 0xC29D7Ab348b2dA3B59eE80A8492bEDFaDf350AEF 0xd52661224f30fa27f622de66c9f1e6fe53d0c89248ec805fd3966d3411becf142203a10c81f7a349135706ce6c35cf12ca629fc1baf6dacc804114ec0424cfef1b";

        RestTemplate mockTemplate = Mockito.mock(RestTemplate.class);
        UserDto userDto = UserDto.builder().data(UserContentDto.builder().id("customer").build()).build();
        ResponseEntity<UserDto> resp = new ResponseEntity<UserDto>(userDto, HttpStatus.ACCEPTED);
        when(mockTemplate.exchange(any(URI.class), any(HttpMethod.class), any(HttpEntity.class), ArgumentMatchers.<Class<UserDto>>any())).thenReturn(resp);

        ReflectionTestUtils.setField(tweetService, "registrationRepository", registrationMapRepository);
        ReflectionTestUtils.setField(tweetService, "restTemplate", mockTemplate);
        //String signerAddress = gnosisSafeOwnerCheck.checkGnosisSafeOwner(tweetService.extractEthereumAddress(tweetText));
        List<TweetContentDto> tweets = Lists.newArrayList();
        tweets.add(TweetContentDto.builder().text(tweetText).author_id("customer").build());
        List<String> trusteeAddresses = tweetService.extractTrusteeAddresses(tweets);
        for (String trusteeAddress : trusteeAddresses) {
            String trxHash = trustVerifiedUserService.giveTrustToEthereumAddress(trusteeAddress);
            log.info("trusted: {}", trxHash);
        }
    }

}
