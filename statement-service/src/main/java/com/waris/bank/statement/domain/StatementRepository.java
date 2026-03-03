package com.waris.bank.statement.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StatementRepository extends JpaRepository<StatementLine, Long> {
  List<StatementLine> findTop200ByAccountIdOrderByCreatedAtDesc(UUID accountId);
}
