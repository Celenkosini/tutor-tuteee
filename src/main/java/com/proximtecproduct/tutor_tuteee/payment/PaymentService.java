package com.proximtecproduct.tutor_tuteee.payment;

import com.proximtecproduct.tutor_tuteee.common.BusinessException;
import com.proximtecproduct.tutor_tuteee.session.SessionStatus;
import com.proximtecproduct.tutor_tuteee.session.SessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private final PaymentRepository repo;
    private final SessionRepository sessions;

    public PaymentService(PaymentRepository repo, SessionRepository sessions) {
        this.repo = repo;
        this.sessions = sessions;
    }

    @Transactional
    public Payment intent(Long sessionId, String tuteeEmail) {
        var s = sessions.findById(sessionId)
                .orElseThrow(() -> new BusinessException("Session not found"));

        if (s.getStatus() != SessionStatus.APPROVED)
            throw new BusinessException("Session must be approved");

        long price = s.getPriceCentsSnapshot();
        long fee = (long) Math.ceil(price * 0.05);

        return repo.save(Payment.builder()
                .session(s)
                .tutee(s.getTutee())
                .tutor(s.getTutor())
                .amountCents(price)
                .platformFeeCents(fee)
                .status(PaymentStatus.PENDING)
                .providerRef("mock_" + sessionId)
                .build());
    }

    @Transactional
    public Payment capture(Long paymentId) {
        var p = repo.findById(paymentId)
                .orElseThrow(() -> new BusinessException("Payment not found"));

        p.setStatus(PaymentStatus.CAPTURED);
        return repo.save(p);
    }
}
