package com.banking.repository;

import com.banking.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("""
            select t from Transaction t
            where t.fromAccount.customer.id = :customerId
               or t.toAccount.customer.id = :customerId
            order by t.createdAt desc
            """)
    List<Transaction> findByCustomerId(@Param("customerId") Long customerId);

    @Query("""
            select t from Transaction t
            where t.fromAccount.customer.id = :customerId
               or t.toAccount.customer.id = :customerId
            order by t.createdAt desc
            """)
    List<Transaction> findLatestByCustomerId(@Param("customerId") Long customerId);
}
