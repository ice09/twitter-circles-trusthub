package tech.blockchainers.circles.twitter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.TestPropertySource;
import org.web3j.protocol.Web3j;
import tech.blockchainers.Hub;
import tech.blockchainers.circles.twitter.service.TwitterScanner;

import java.math.BigInteger;

@SpringBootTest
@TestPropertySource("/trusthub.properties")
@MockBean(TwitterScanner.class)
public class IntegrationTest {

    @Autowired
    private Hub hub;
    @Autowired
    private Web3j httpWeb3j;

    @Test
    public void setup() throws Exception {
        hub.trust("0xf17f52151EbEF6C7334FAD080c5704D77216b732", BigInteger.TEN).send();
        Thread.sleep(5000);
    }

}
