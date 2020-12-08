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
import org.web3j.protocol.core.methods.response.EthBlock;

import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.tx.gas.DefaultGasProvider;
import tech.blockchainers.Hub;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

@Component
@Slf4j
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
            EthFilter eventFilter = new EthFilter(DefaultBlockParameter.valueOf(latestBlock), DefaultBlockParameterName.LATEST, hub.getContractAddress());
            String encodedEventSignature = EventEncoder.encode(Hub.TRUST_EVENT);
            eventFilter.addSingleTopic(encodedEventSignature);
            Request<?, EthLog> resReg = httpWeb3j.ethGetLogs(eventFilter);
            List<EthLog.LogResult> regLogs = resReg.send().getLogs();
            for (int i=0; i< regLogs.size(); i++) {
                Log lastLogEntry = ((EthLog.LogObject) regLogs.get(i));
                if (lastLogEntry.getBlockNumber().compareTo(latestBlock) > 0) {
                    List<String> ethLogTopics = lastLogEntry.getTopics();
                    log.info("user | canSendTo : " + ethLogTopics.get(2) + " | " + ethLogTopics.get(1));
                    latestBlock = lastLogEntry.getBlockNumber();
                } else {
                    log.warn("Old Log Entries detected, skipping.");
                }
            }
        }
    }

}