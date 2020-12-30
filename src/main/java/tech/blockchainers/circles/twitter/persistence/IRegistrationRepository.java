package tech.blockchainers.circles.twitter.persistence;

public interface IRegistrationRepository {

    void linkTwitterIdToEthereumAddress(String twitterId, String ethereumAddress) throws Exception;

    String getTwitterAddressForEthereumAddress(String ethereumAddress) throws Exception;

    String getEthereumAddressForTwitterUsername(String ethereumAddress) throws Exception;

    boolean isTwitterIdRegistered(String twitterId) throws Exception;

    boolean hasEthereumAddressBeenRegisteredAlready(String ethereumAddress) throws Exception;
}
