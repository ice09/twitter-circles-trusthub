package tech.blockchainers.circles.twitter.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import tech.blockchainers.circles.twitter.persistence.RegistrationRepository;
import tech.blockchainers.circles.twitter.scheduler.ContractMonitor;

@SpringBootTest
@TestPropertySource("/trusthub.properties")
@Slf4j
@MockBean(TwitterScanner.class)
@MockBean(ContractMonitor.class)
public class IntegrationTest {

    @Autowired
    private TweetService tweetService;

    @Autowired
    private TrustVerifiedUserService trustVerifiedUserService;

    @Autowired
    private GnosisSafeOwnerCheck gnosisSafeOwnerCheck;

    @MockBean
    private RegistrationRepository registrationRepository;

    // This is an integration tests and requires xDai at the address of the private key in the xDai Mainnet
    //@Test
    public void trustWithSafeOnMainnet() throws Exception {
        // All addresses in message must be lowercase!
        // Message:
        // I AM 0x945cac6047b1f58945ed2aafa5baed96a31faa4c AND WANT 0xa485e9295ef16143891f4a0d77c060e87ea59c87 TO TRUST ME
        String tweetText = "#circles_twitterhub 0x945CaC6047B1f58945ed2aafA5BaeD96A31faa4c 0x1bf09cf255761116cfe3f2d0e343012d9bc31670f79e48b571a5c412d17239ae7bae4a94867a9cffb3bd8f63c070b9318bbe9b65d60db08beeeab2bf1ea8f6781c"
                .toLowerCase();
        ReflectionTestUtils.setField(tweetService, "registrationRepository", registrationRepository);
        String signerAddress = gnosisSafeOwnerCheck.checkGnosisSafeOwner(tweetService.extractEthereumAddress(tweetText));
        String ethereumAddress = tweetService.handleTweet(tweetText, "user", signerAddress);
        String trxHash = trustVerifiedUserService.giveTrustToEthereumAddress(ethereumAddress);
        log.info("trusted: {}", trxHash);
        Thread.sleep(500000);
    }

}
