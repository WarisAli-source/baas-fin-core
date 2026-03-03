package com.waris.bank.ledger.grpc;

import com.waris.bank.ledger.store.LedgerRepository;
import com.waris.bank.proto.ledger.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.UUID;

@GrpcService
public class LedgerGrpcService extends LedgerServiceGrpc.LedgerServiceImplBase {

  private final LedgerRepository repo;

  public LedgerGrpcService(LedgerRepository repo) {
    this.repo = repo;
  }

  @Override
  public void getBalance(GetBalanceRequest request, StreamObserver<GetBalanceResponse> responseObserver) {
    UUID accountId = UUID.fromString(request.getAccountId());
    long bal = repo.getAvailable(accountId);

    GetBalanceResponse resp = GetBalanceResponse.newBuilder()
        .setAccountId(request.getAccountId())
        .setBalance(Money.newBuilder().setAmountPaise(bal).build())
        .build();

    responseObserver.onNext(resp);
    responseObserver.onCompleted();
  }

  @Override
  public void postTransfer(PostTransferRequest request, StreamObserver<PostTransferResponse> responseObserver) {
    String txnId = request.getTxnId();
    if (repo.txnExists(txnId)) {
      responseObserver.onNext(PostTransferResponse.newBuilder().setTxnId(txnId).setStatus("DUPLICATE").build());
      responseObserver.onCompleted();
      return;
    }

    UUID from = UUID.fromString(request.getFromAccountId());
    UUID to = UUID.fromString(request.getToAccountId());
    long amt = request.getAmount().getAmountPaise();

    long available = repo.getAvailable(from);
    if (available < amt) {
      responseObserver.onNext(PostTransferResponse.newBuilder().setTxnId(txnId).setStatus("FAILED").build());
      responseObserver.onCompleted();
      return;
    }

    repo.createTxn(txnId, null, "TRANSFER", "POSTING");
    repo.postEntry(txnId, from, "DEBIT", amt, request.getNarrative());
    repo.postEntry(txnId, to, "CREDIT", amt, request.getNarrative());
    repo.markTxn(txnId, "POSTED");

    responseObserver.onNext(PostTransferResponse.newBuilder().setTxnId(txnId).setStatus("POSTED").build());
    responseObserver.onCompleted();
  }

  @Override
  public void postAdjustment(PostAdjustmentRequest request, StreamObserver<PostAdjustmentResponse> responseObserver) {
    String txnId = request.getTxnId();
    if (repo.txnExists(txnId)) {
      responseObserver.onNext(PostAdjustmentResponse.newBuilder().setTxnId(txnId).setStatus("DUPLICATE").build());
      responseObserver.onCompleted();
      return;
    }

    UUID account = UUID.fromString(request.getAccountId());
    long amt = request.getAmount().getAmountPaise();
    String dir = request.getDirection();

    repo.createTxn(txnId, null, "ADJUSTMENT", "POSTING");
    if ("CREDIT".equalsIgnoreCase(dir)) {
      repo.postEntry(txnId, account, "CREDIT", amt, request.getReason());
    } else {
      long available = repo.getAvailable(account);
      if (available < amt) {
        repo.markTxn(txnId, "FAILED");
        responseObserver.onNext(PostAdjustmentResponse.newBuilder().setTxnId(txnId).setStatus("FAILED").build());
        responseObserver.onCompleted();
        return;
      }
      repo.postEntry(txnId, account, "DEBIT", amt, request.getReason());
    }

    repo.markTxn(txnId, "POSTED");
    responseObserver.onNext(PostAdjustmentResponse.newBuilder().setTxnId(txnId).setStatus("POSTED").build());
    responseObserver.onCompleted();
  }
}
