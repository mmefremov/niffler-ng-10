package guru.qa.niffler.test.grpc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.grpc.NifflerCurrencyServiceGrpc;
import guru.qa.niffler.grpc.NifflerUserdataServiceGrpc;
import guru.qa.niffler.jupiter.annotation.meta.GrpcTest;
import guru.qa.niffler.utils.GrpcConsoleInterceptor;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;

@GrpcTest
public abstract class BaseGrpcTest {

    protected static final Config CFG = Config.getInstance();

    private static final Channel CURRENCY_CHANNEL = ManagedChannelBuilder
            .forAddress(CFG.currencyGrpcAddress(), CFG.currencyGrpcPort())
            .intercept(new GrpcConsoleInterceptor())
            .usePlaintext()
            .build();

    private static final Channel USERDATA_CHANNEL = ManagedChannelBuilder
            .forAddress(CFG.userdataGrpcAddress(), CFG.userdataGrpcPort())
            .intercept(new GrpcConsoleInterceptor())
            .usePlaintext()
            .build();

    static final NifflerCurrencyServiceGrpc.NifflerCurrencyServiceBlockingStub CURRENCY_BLOCKING_STUB
            = NifflerCurrencyServiceGrpc.newBlockingStub(CURRENCY_CHANNEL);

    static final NifflerUserdataServiceGrpc.NifflerUserdataServiceBlockingStub USERDATA_BLOCKING_STUB
            = NifflerUserdataServiceGrpc.newBlockingStub(USERDATA_CHANNEL);
}
