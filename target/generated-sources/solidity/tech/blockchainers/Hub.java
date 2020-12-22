package tech.blockchainers;

import io.reactivex.Flowable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.2.0.
 */
public class Hub extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b50610160806100206000396000f30060806040526004361061004b5763ffffffff7c01000000000000000000000000000000000000000000000000000000006000350416639951d62f8114610050578063a3eb2b4a14610083575b600080fd5b34801561005c57600080fd5b5061008173ffffffffffffffffffffffffffffffffffffffff600435166024356100c9565b005b34801561008f57600080fd5b506100b773ffffffffffffffffffffffffffffffffffffffff6004358116906024351661011a565b60408051918252519081900360200190f35b60408051828152905173ffffffffffffffffffffffffffffffffffffffff84169133917fe60c754dd8ab0b1b5fccba257d6ebcd7d09e360ab7dd7a6e58198ca1f57cdcec9181900360200190a35050565b6000602081815292815260408082209093529081522054815600a165627a7a7230582086b4042450082524f4c32016e2bc7919b22c3591030e166445627087a52221320029";

    public static final String FUNC_TRUST = "trust";

    public static final String FUNC_LIMITS = "limits";

    public static final Event SIGNUP_EVENT = new Event("Signup", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>() {}));
    ;

    public static final Event ORGANIZATIONSIGNUP_EVENT = new Event("OrganizationSignup", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}));
    ;

    public static final Event TRUST_EVENT = new Event("Trust", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event HUBTRANSFER_EVENT = new Event("HubTransfer", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    @Deprecated
    protected Hub(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Hub(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected Hub(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected Hub(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteCall<TransactionReceipt> trust(String user, BigInteger limit) {
        final Function function = new Function(
                FUNC_TRUST, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(user), 
                new org.web3j.abi.datatypes.generated.Uint256(limit)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> limits(String param0, String param1) {
        final Function function = new Function(FUNC_LIMITS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0), 
                new org.web3j.abi.datatypes.Address(param1)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public List<SignupEventResponse> getSignupEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(SIGNUP_EVENT, transactionReceipt);
        ArrayList<SignupEventResponse> responses = new ArrayList<SignupEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            SignupEventResponse typedResponse = new SignupEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.user = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.token = (String) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<SignupEventResponse> signupEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, SignupEventResponse>() {
            @Override
            public SignupEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(SIGNUP_EVENT, log);
                SignupEventResponse typedResponse = new SignupEventResponse();
                typedResponse.log = log;
                typedResponse.user = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.token = (String) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<SignupEventResponse> signupEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(SIGNUP_EVENT));
        return signupEventFlowable(filter);
    }

    public List<OrganizationSignupEventResponse> getOrganizationSignupEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(ORGANIZATIONSIGNUP_EVENT, transactionReceipt);
        ArrayList<OrganizationSignupEventResponse> responses = new ArrayList<OrganizationSignupEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            OrganizationSignupEventResponse typedResponse = new OrganizationSignupEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.organization = (String) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<OrganizationSignupEventResponse> organizationSignupEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, OrganizationSignupEventResponse>() {
            @Override
            public OrganizationSignupEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(ORGANIZATIONSIGNUP_EVENT, log);
                OrganizationSignupEventResponse typedResponse = new OrganizationSignupEventResponse();
                typedResponse.log = log;
                typedResponse.organization = (String) eventValues.getIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<OrganizationSignupEventResponse> organizationSignupEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ORGANIZATIONSIGNUP_EVENT));
        return organizationSignupEventFlowable(filter);
    }

    public List<TrustEventResponse> getTrustEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(TRUST_EVENT, transactionReceipt);
        ArrayList<TrustEventResponse> responses = new ArrayList<TrustEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TrustEventResponse typedResponse = new TrustEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.canSendTo = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.user = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.limit = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<TrustEventResponse> trustEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, TrustEventResponse>() {
            @Override
            public TrustEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(TRUST_EVENT, log);
                TrustEventResponse typedResponse = new TrustEventResponse();
                typedResponse.log = log;
                typedResponse.canSendTo = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.user = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.limit = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<TrustEventResponse> trustEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TRUST_EVENT));
        return trustEventFlowable(filter);
    }

    public List<HubTransferEventResponse> getHubTransferEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(HUBTRANSFER_EVENT, transactionReceipt);
        ArrayList<HubTransferEventResponse> responses = new ArrayList<HubTransferEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            HubTransferEventResponse typedResponse = new HubTransferEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<HubTransferEventResponse> hubTransferEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, HubTransferEventResponse>() {
            @Override
            public HubTransferEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(HUBTRANSFER_EVENT, log);
                HubTransferEventResponse typedResponse = new HubTransferEventResponse();
                typedResponse.log = log;
                typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<HubTransferEventResponse> hubTransferEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(HUBTRANSFER_EVENT));
        return hubTransferEventFlowable(filter);
    }

    @Deprecated
    public static Hub load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Hub(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Hub load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Hub(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static Hub load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new Hub(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Hub load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Hub(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<Hub> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Hub.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Hub> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Hub.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<Hub> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Hub.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Hub> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Hub.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static class SignupEventResponse {
        public Log log;

        public String user;

        public String token;
    }

    public static class OrganizationSignupEventResponse {
        public Log log;

        public String organization;
    }

    public static class TrustEventResponse {
        public Log log;

        public String canSendTo;

        public String user;

        public BigInteger limit;
    }

    public static class HubTransferEventResponse {
        public Log log;

        public String from;

        public String to;

        public BigInteger amount;
    }
}
