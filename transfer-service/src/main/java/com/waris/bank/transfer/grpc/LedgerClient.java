package com.waris.bank.transfer.grpc;

import com.waris.bank.proto.ledger.BalanceType;
import com.waris.bank.proto.ledger.GetBalanceRequest;
import com.waris.bank.proto.ledger.LedgerServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Component
public class LedgerClient {

  @GrpcClient("ledger")
  private LedgerServiceGrpc.LedgerServiceBlockingStub stub;

  public long getAvailablePaise(String accountId) {
    return stub.getBalance(GetBalanceRequest.newBuilder().setAccountId(accountId).setType(BalanceType.AVAILABLE).build())
        .getBalance().getAmountPaise();
  }
}
