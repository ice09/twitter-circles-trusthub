package tech.blockchainers.circles.twitter.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import tech.blockchainers.Hub;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class TweetServiceTest {

    @Test
    public void shouldMatchTwitterMessage() {
        assertTrue(new TweetService(null, null).isValidMessage("#twitterbot 0xaaa 0xbbb"));
        assertFalse(new TweetService(null, null).isValidMessage("#twitterbot 0xaaa 0xbbb "));
        assertTrue(new TweetService(null, null).isValidMessage("#twitterbot address: 0xaaa signature: 0xbbb"));
    }

    @Test
    public void shouldRecoverAddress() {
        //message: I AM 0xb1168e07a7a91f3ea30e6a2853b9b29433a64268 AND WANT 0xb33d8395557533c57a265c975554ecf165c42ca1 TO TRUST ME
        String signature = "0xf196c8274e560e59c8f44923dbf38e0f3a12fba84c1f55bb26bebbdf051f2dbf4bdd90d67a0f7c296a01e9bc0f110630d2d654ae467c44b418457544f8296aaf1c";
        TweetService twitterScanner = new TweetService(new SignatureService(), null);
        Hub hub = Mockito.mock(Hub.class);
        when(hub.getContractAddress()).thenReturn("0xb33d8395557533c57a265c975554ecf165c42ca1".toLowerCase());
        ReflectionTestUtils.setField(twitterScanner, "hub", hub);
        ReflectionTestUtils.setField(twitterScanner, "message", "I AM #addr# AND WANT #botaddr# TO TRUST ME");
        assertTrue(twitterScanner.signatureDoesMatch("#twitterbot 0xb1168e07a7a91f3ea30e6a2853b9b29433a64268 ".toLowerCase() + signature, "0xb1168e07a7a91f3ea30e6a2853b9b29433a64268"));
    }
}
