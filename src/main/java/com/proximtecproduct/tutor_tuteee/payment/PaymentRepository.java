package com.proximtecproduct.tutor_tuteee.payment;

// payment/repo/PaymentRepository.java

 import org.springframework.data.jpa.repository.JpaRepository;
 import org.springframework.data.jpa.repository.Query;

public interface PaymentRepository extends JpaRepository<Payment,Long> {

     @Query("SELECT COALESCE(SUM(p.amountCents), 0) FROM Payment p")
    long sumAmountCents();

    @Query("SELECT COALESCE(SUM(p.platformFeeCents), 0) FROM Payment p")
    long sumPlatformFeeCents();

    
}

