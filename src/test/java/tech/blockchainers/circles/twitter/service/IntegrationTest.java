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

    @Test
    public void trustWithSafeOnMainnet() throws Exception {
        //I AM 0x945CaC6047B1f58945ed2aafA5BaeD96A31faa4c AND WANT 0xA485e9295ef16143891F4A0d77C060E87EA59C87 TO TRUST ME
        String tweetText = "#circles_twitterhub 0x945CaC6047B1f58945ed2aafA5BaeD96A31faa4c 0x1e3788e96771d41f195ca3542dba25e1fcd9c429db46ebb00ef9e440aea1821f01194d70cd55ad215cf66806495375bd350eefa5e155c5cab92425a9159cc3f91b";
        ReflectionTestUtils.setField(tweetService, "registrationRepository", registrationRepository);
        String signerAddress = gnosisSafeOwnerCheck.checkGnosisSafeOwner(tweetService.extractEthereumAddress(tweetText));
        String ethereumAddress = tweetService.handleTweet(tweetText, "user", signerAddress);
        String trxHash = trustVerifiedUserService.giveTrustToEthereumAddress(ethereumAddress);
        log.info("trusted: {}", trxHash);
        Thread.sleep(500000);
    }

}
