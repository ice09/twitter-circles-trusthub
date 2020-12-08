package tech.blockchainers.circles.twitter.config;

import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;
import tech.blockchainers.Hub;

import java.security.Security;
import java.util.concurrent.TimeUnit;

@Configuration
public class ApplicationConfiguration {

    @Value("${ethereum.rpc.url}")
    private String ethereumRpcUrl;

    @Value("${trusthub.private.key}")
    private String privateKey;

    @Value("${trusthub.contract.address}")
    private String contractAddress;

    @Bean
    public Web3j web3j() {
        return Web3j.build(new HttpService(ethereumRpcUrl, createOkHttpClient()));
    }

    @Bean
    public Hub createTrustHubProxy() throws Exception {
        if (StringUtils.isNotEmpty(contractAddress)) {
            return Hub.load(contractAddress.toLowerCase(), web3j(), createCredentials(), new DefaultGasProvider());
        } else {
            return Hub.deploy(web3j(), createCredentials(), new DefaultGasProvider()).send();
        }
    }

    @Bean
    public Credentials createCredentials() {
        return Credentials.create(privateKey);
    }

    private OkHttpClient createOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        configureTimeouts(builder);
        return builder.build();
    }

    private void configureTimeouts(OkHttpClient.Builder builder) {
        long tos = 8000L;
        builder.connectTimeout(tos, TimeUnit.SECONDS);
        builder.readTimeout(tos, TimeUnit.SECONDS);  // Sets the socket timeout too
        builder.writeTimeout(tos, TimeUnit.SECONDS);
    }
}
