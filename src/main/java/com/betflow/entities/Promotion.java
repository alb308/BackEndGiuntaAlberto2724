package com.betflow.entities;

import com.betflow.enums.PromotionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "promotions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String description;

    @Column(name = "bonus_amount", precision = 19, scale = 2)
    private BigDecimal bonusAmount;

    @Column(name = "rollover_target", precision = 19, scale = 2)
    private BigDecimal rolloverTarget;

    @Column(name = "rollover_done", precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal rolloverDone = BigDecimal.ZERO;

    @Column(name = "deadline_date")
    private LocalDate deadlineDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PromotionStatus status = PromotionStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    public BigDecimal getRolloverPercentage() {
        if (rolloverTarget == null || rolloverTarget.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.valueOf(100);
        }
        return rolloverDone.multiply(BigDecimal.valueOf(100)).divide(rolloverTarget, 2, java.math.RoundingMode.HALF_UP);
    }
}
