package tech.blockchainers.circles.twitter.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.web3j.abi.EventEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.Log;
import tech.blockchainers.Hub;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

// @Component // deactivated for pub/sub twitter style
@Slf4j
// This component is not necessary for a "notify-all" (pub/sub) twitter messages
// For notification of individual users instead of "#"-tweets, this would have to listen to some
// middleware like a smart contract, eg. listening for "trust" calls on contracts or to transactions
// to EOA or smart contracts (which cost gas, so onboarding issues).
public class ContractMonitor {

    private BigInteger latestBlock;
    private final Hub hub;
    private final Web3j httpWeb3j;

    public ContractMonitor(Web3j httpWeb3j, Hub hub) throws IOException {
        this.hub = hub;
        this.httpWeb3j = httpWeb3j;
        this.latestBlock = httpWeb3j.ethBlockNumber().send().getBlockNumber();
    }

    @Scheduled(fixedDelay = 5000)
    private void addTrustFromTrustEvent() throws IOException {
        BigInteger currentBlock = httpWeb3j.ethBlockNumber().send().getBlockNumber();
        if (currentBlock.compareTo(latestBlock) > 0) {
            BigInteger index = latestBlock;
            while (currentBlock.compareTo(index) > 0) {
                EthFilter eventFilter = new EthFilter(DefaultBlockParameter.valueOf(index), DefaultBlockParameterName.LATEST, hub.getContractAddress());
                String encodedEventSignature = EventEncoder.encode(Hub.TRUST_EVENT);
                eventFilter.addSingleTopic(encodedEventSignature);
                Request<?, EthLog> resReg = httpWeb3j.ethGetLogs(eventFilter);
                List<EthLog.LogResult> regLogs = resReg.send().getLogs();
                for (int i = 0; i < regLogs.size(); i++) {
                    Log lastLogEntry = ((EthLog.LogObject) regLogs.get(i));
                    if (lastLogEntry.getBlockNumber().compareTo(index) > 0) {
                        List<String> ethLogTopics = lastLogEntry.getTopics();
                        log.info("user | canSendTo : " + ethLogTopics.get(2) + " | " + ethLogTopics.get(1));
                        latestBlock = lastLogEntry.getBlockNumber();
                    } else {
                        log.warn("Old Log Entries detected, skipping.");
                    }
                }
                index = index.add(BigInteger.ONE);
            }
            latestBlock = index;
        }
    }

}