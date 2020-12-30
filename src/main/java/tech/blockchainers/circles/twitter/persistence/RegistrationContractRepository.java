package tech.blockchainers.circles.twitter.persistence;

import org.springframework.stereotype.Service;
import tech.blockchainers.TrusthubRegistry;

import java.nio.charset.StandardCharsets;

@Service
public class RegistrationContractRepository implements IRegistrationRepository {

    private final TrusthubRegistry trusthubRegistry;

    public RegistrationContractRepository(TrusthubRegistry trusthubRegistry) {
        this.trusthubRegistry = trusthubRegistry;
    }

    @Override
    public void linkTwitterIdToEthereumAddress(String twitterId, String ethereumAddress) throws Exception {
        trusthubRegistry.register(twitterId, ethereumAddress).send();
    }

    public String getTwitterAddressForEthereumAddress(String ethereumAddress) throws Exception {
        return trusthubRegistry.addressRegistry(ethereumAddress).send().toString();
    }

    @Override
    public String getEthereumAddressForTwitterUsername(String twitterId) throws Exception {
        return trusthubRegistry.userRegistry(twitterId.getBytes(StandardCharsets.UTF_8)).send().toString();
    }

    @Override
    public boolean isTwitterIdRegistered(String twitterId) throws Exception {
        return trusthubRegistry.userRegistry(twitterId.getBytes(StandardCharsets.UTF_8)).send();
    }

    @Override
    public boolean hasEthereumAddressBeenRegisteredAlready(String ethereumAddress) throws Exception {
        return trusthubRegistry.isAddressRegistered(ethereumAddress).send();
    }
}
