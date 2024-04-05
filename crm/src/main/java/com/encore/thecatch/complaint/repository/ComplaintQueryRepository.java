package com.encore.thecatch.complaint.repository;


import com.encore.thecatch.common.util.AesUtil;
import com.encore.thecatch.complaint.dto.request.SearchComplaintCondition;
import com.encore.thecatch.complaint.dto.response.ListComplaintRes;
import com.encore.thecatch.complaint.dto.response.QListComplaintRes;
import com.encore.thecatch.complaint.entity.Active;
import com.encore.thecatch.complaint.entity.QComplaint;
import com.encore.thecatch.complaint.entity.Status;
import com.encore.thecatch.user.domain.QUser;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
public class ComplaintQueryRepository {
    private final JPAQueryFactory queryFactory;

    private final AesUtil aesUtil;

    QComplaint complaint = QComplaint.complaint;
    QUser user = QUser.user;

    public List<ListComplaintRes> findComplaintList(SearchComplaintCondition searchComplaintCondition) throws Exception {
        return queryFactory
                .select(new QListComplaintRes(
                        complaint.id.as("complaintId"),
                        user.name,
                        complaint.title,
                        complaint.status))
                .from(complaint )
                .leftJoin(complaint.user, user)
                .where(
                        eqPostId(searchComplaintCondition.getComplaintId()),
                        eqName(searchComplaintCondition.getName()),
                        eqTitle(searchComplaintCondition.getTitle()),
                        eqStatus(searchComplaintCondition.getStatus()),
                        complaint.active.eq(Active.valueOf("TRUE")))
                .fetch();
    }

    private BooleanExpression eqPostId(Long id) {
        return id != null ? complaint.id.eq(id) : null;
    }

    private BooleanExpression eqUserId(Long id) {
        return id != null ? user.id.eq(id) : null;
    }

    private BooleanExpression eqName(String name) throws Exception {
        return hasText(name) ? user.name.eq(aesUtil.aesCBCEncode(name)) : null;
    }

    private BooleanExpression eqTitle(String title) {
        return hasText(title) ? complaint.title.eq(title) : null;
    }

    private BooleanExpression eqStatus(Status status) {
        return status != null ? complaint.status.eq(status) : null;
    }

    private BooleanExpression eqActive(Active active) {
        return active != null ? complaint.active.eq(active) : null;
    }

}
