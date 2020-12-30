package tech.blockchainers.circles.twitter.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TweetServiceTest {
    @Autowired
    private GnosisSafeOwnerCheck gnosisSafeOwnerCheck;

    @Test
    public void shouldMatchTwitterMessage() {
        assertTrue(new TweetService(gnosisSafeOwnerCheck, null, null, new RestTemplate()).isValidMessage("#twitterbot 0xaaa 0xbbb"));
        assertFalse(new TweetService(gnosisSafeOwnerCheck, null, null, new RestTemplate()).isValidMessage("#twitterbot 0xaaa 0xbbb "));
        assertTrue(new TweetService(gnosisSafeOwnerCheck, null, null, new RestTemplate()).isValidMessage("#twitterbot address: 0xaaa signature: 0xbbb"));
    }

    @Test
    public void shouldRecoverAddress() {
        // Message:
        // I AM 0x945CaC6047B1f58945ed2aafA5BaeD96A31faa4c AND WANT 0xA485e9295ef16143891F4A0d77C060E87EA59C87 TO TRUST ME
        String signature = "0x1e3788e96771d41f195ca3542dba25e1fcd9c429db46ebb00ef9e440aea1821f01194d70cd55ad215cf66806495375bd350eefa5e155c5cab92425a9159cc3f91b";
        TweetService twitterScanner = new TweetService(gnosisSafeOwnerCheck, new SignatureService(), null, new RestTemplate());
        ReflectionTestUtils.setField(twitterScanner, "trusthubAddress", "0xA485e9295ef16143891F4A0d77C060E87EA59C87");
        ReflectionTestUtils.setField(twitterScanner, "message", "I AM #addr# AND WANT #botaddr# TO TRUST ME");
        assertTrue(twitterScanner.signatureDoesMatch("#circles_trusthub 0x945CaC6047B1f58945ed2aafA5BaeD96A31faa4c " + signature, "0x17337e1cA1fD74a1E73B7a50f6dB8f0E1C37588B"));
    }
}
