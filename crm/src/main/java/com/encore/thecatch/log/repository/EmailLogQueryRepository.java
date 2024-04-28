package com.encore.thecatch.log.repository;

import com.encore.thecatch.common.querydsl.Querydsl4RepositorySupport;
import com.encore.thecatch.log.domain.EmailLog;
import com.encore.thecatch.log.domain.QEmailLog;
import org.springframework.stereotype.Repository;

@Repository
public class EmailLogQueryRepository extends Querydsl4RepositorySupport {

    QEmailLog emailLog = QEmailLog.emailLog;

    public EmailLogQueryRepository (){
        super(EmailLog.class);
    }

    public Long totalEmail() {
        return select(
                        emailLog.count())
                .from(emailLog)
                .fetchCount();
    }
}
