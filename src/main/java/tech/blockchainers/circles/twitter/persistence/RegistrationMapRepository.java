package tech.blockchainers.circles.twitter.persistence;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentMap;

//@Service // deactivated for smart contract solution
public class RegistrationMapRepository implements IRegistrationRepository {
    private DB db;
    private ConcurrentMap twitterRegistrations;
    @Value("${repository.file}")
    private String file;

    @PostConstruct
    public void init() {
        db = DBMaker.fileDB(file).closeOnJvmShutdown().make();
        twitterRegistrations = db.hashMap("twitterRegistrations").createOrOpen();
    }

    public String printTwitterRegistrations() {
        ToStringBuilder tsb = new ToStringBuilder(twitterRegistrations);
        return tsb.build();
    }

    public ConcurrentMap getAllRegistrations() {
        return twitterRegistrations;
    }

    @Override
    public void linkTwitterIdToEthereumAddress(String twitterId, String ethereumAddress) {
        if (twitterRegistrations.containsKey(twitterId)) {
            throw new IllegalArgumentException("Twitter ID " + twitterId + " does already exist: " + twitterRegistrations.get(twitterId));
        }
        if (twitterRegistrations.containsValue(ethereumAddress)) {
            throw new IllegalArgumentException("Ethereum Address " + ethereumAddress + " is already associated with a Twitter ID: " + twitterRegistrations.values().stream().filter(it -> it.equals(ethereumAddress)).findAny().get());
        }
        twitterRegistrations.put(twitterId, ethereumAddress);
        db.commit();
    }

    @Override
    public String getTwitterAddressForEthereumAddress(String ethereumAddress) {
        return (String) twitterRegistrations.get(ethereumAddress);
    }

    @Override
    public String getEthereumAddressForTwitterUsername(String ethereumAddress) {
        return (String) twitterRegistrations.entrySet().stream().filter(it -> it.equals(ethereumAddress)).findAny().get();
    }

    @Override
    public boolean isTwitterIdRegistered(String twitterId) {
        return twitterRegistrations.containsKey(twitterId);
    }

    @Override
    public boolean hasEthereumAddressBeenRegisteredAlready(String ethereumAddress) {
        return twitterRegistrations.containsValue(ethereumAddress);
    }

}
