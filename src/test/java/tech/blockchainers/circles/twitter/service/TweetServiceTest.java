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
        // All addresses in message must be lowercase!
        // Message:
        // I AM 0x945cac6047b1f58945ed2aafa5baed96a31faa4c AND WANT 0xa485e9295ef16143891f4a0d77c060e87ea59c87 TO TRUST ME
        String signature = "0x8472a7dabc427546ada355d5cea33108fe7150958fc626ef94185623db5cd98870aeefa5e948c22ea1e5307eea77e6620244f11e4c34ebf0e3341ce3bdeeda811c";
        TweetService twitterScanner = new TweetService(new SignatureService(), null);
        ReflectionTestUtils.setField(twitterScanner, "trusthubAddress", "0xA485e9295ef16143891F4A0d77C060E87EA59C87".toLowerCase());
        ReflectionTestUtils.setField(twitterScanner, "message", "I AM #addr# AND WANT #botaddr# TO TRUST ME");
        assertTrue(twitterScanner.signatureDoesMatch("#circles_twitterhub 0x945CaC6047B1f58945ed2aafA5BaeD96A31faa4c ".toLowerCase() + signature, "0x898408eba8e4faEC1027AF9a09D304e155c9c18b"));
    }
}
