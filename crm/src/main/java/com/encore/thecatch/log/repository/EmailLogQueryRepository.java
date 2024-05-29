package com.encore.thecatch.log.repository;

import com.encore.thecatch.common.querydsl.Querydsl4RepositorySupport;
import com.encore.thecatch.event.domain.Event;
import com.encore.thecatch.log.domain.EmailLog;
import com.encore.thecatch.log.domain.LogType;
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

    public Long eventSendCount(Event event) {
        return select(emailLog.count())
                .from(emailLog)
                .where(
                        emailLog.event.eq(event),
                        emailLog.type.eq(LogType.EVENT_EMAIL_SEND))
                .fetchCount();
    }

    public Long eventReceiveCount(Event event) {
        return select(emailLog.count())
                .from(emailLog)
                .where(
                        emailLog.event.eq(event),
                        emailLog.type.eq(LogType.EVENT_EMAIL_SEND),
                        emailLog.emailCheck.eq(true))
                .fetchCount();
    }
}
