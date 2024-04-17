package com.encore.thecatch.log.repository;

import com.encore.thecatch.log.domain.QEmailLog;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EmailLogQueryRepository {
    private final JPAQueryFactory queryFactory;
    QEmailLog emailLog = QEmailLog.emailLog;

    public Long totalEmail() {
        return queryFactory
                .select(
                        emailLog.count())
                .from(emailLog)
                .fetchCount();
    }
}
