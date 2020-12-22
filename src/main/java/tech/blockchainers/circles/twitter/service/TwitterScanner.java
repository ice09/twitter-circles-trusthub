package tech.blockchainers.circles.twitter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tech.blockchainers.circles.twitter.service.dto.TweetContentDto;
import tech.blockchainers.circles.twitter.service.dto.TweetDto;
import tech.blockchainers.circles.twitter.service.dto.UserDto;

@Service
@Slf4j
public class TwitterScanner {

    private final TweetService tweetService;

    private final TrustVerifiedUserService trustVerifiedUserService;

    private final GnosisSafeOwnerCheck gnosisSafeOwnerCheck;

    public TwitterScanner(TweetService tweetService, TrustVerifiedUserService trustVerifiedUserService, GnosisSafeOwnerCheck gnosisSafeOwnerCheck) {
        this.tweetService = tweetService;
        this.trustVerifiedUserService = trustVerifiedUserService;
        this.gnosisSafeOwnerCheck = gnosisSafeOwnerCheck;
    }

    @Scheduled(fixedDelay = 3000)
    public void scanTwitterForTwitterBot() throws Exception {
        TweetDto response = tweetService.getTweets();
        if (response != null) {
            log.info(response.toString());
            for (TweetContentDto tweet : response.getData()) {
                UserDto user = tweetService.getUser(tweet.getAuthor_id());
                String signerAddress = gnosisSafeOwnerCheck.checkGnosisSafeOwner(tweetService.extractEthereumAddress(tweet.getText()));
                String ethereumAddress = tweetService.handleTweet(tweet.getText(), user.getData().getUsername(), signerAddress);
                if (StringUtils.hasText(gnosisSafeOwnerCheck.checkGnosisSafeOwner(ethereumAddress))) {
                    trustVerifiedUserService.giveTrustToEthereumAddress(ethereumAddress);
                }
            }
        }
    }

}
