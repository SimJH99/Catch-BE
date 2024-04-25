package com.encore.thecatch.complaint.repository;


import com.encore.thecatch.common.util.AesUtil;
import com.encore.thecatch.complaint.dto.request.SearchComplaintCondition;
import com.encore.thecatch.complaint.dto.response.*;
import com.encore.thecatch.complaint.entity.Complaint;
import com.encore.thecatch.complaint.entity.QComplaint;
import com.encore.thecatch.complaint.entity.Status;
import com.encore.thecatch.user.domain.QUser;
import com.encore.thecatch.user.domain.User;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
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
                .from(complaint)
                .leftJoin(complaint.user, user)
                .where(
                        eqPostId(searchComplaintCondition.getComplaintId()),
                        eqName(searchComplaintCondition.getName()),
                        containsTitle(searchComplaintCondition.getTitle()),
                        eqStatus(searchComplaintCondition.getStatus()),
                        complaint.active.eq(true))
                .orderBy(complaint.status.asc(), complaint.createdTime.asc())
                .fetch();
    }

    public Page<MyComplaintRes> findMyComplaintList(User user, Pageable pageable) {
        List<MyComplaintRes> content = queryFactory
                .select(new QMyComplaintRes(
                        complaint.id,
                        complaint.title,
                        complaint.createdTime,
                        complaint.status))
                .from(complaint)
                .where(complaint.user.eq(user),complaint.active.eq(true))
                .orderBy(complaint.createdTime.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        JPAQuery<Complaint> countQuery = queryFactory
                .selectFrom(complaint)
                .where(
                        complaint.user.eq(user),complaint.active.eq(true)
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchCount);
    }

    public List<MyPageComplaints> myPageComplaints() {
        return queryFactory
                .select(new QMyPageComplaints(
                        complaint.id,
                        complaint.title,
                        complaint.status))
                .from(complaint)
                .where(complaint.active.eq(true))
                .orderBy(complaint.createdTime.desc())
                .limit(5)
                .fetch();
    }

    public Long countAllComplaint() {
        return queryFactory
                .select(new QCountAllComplaintRes(
                        complaint.count()))
                .from(complaint)
                .where(complaint.active.eq(true))
                .fetchCount();
    }

    public List<CountStatusComplaintRes> countStatusComplaint() {
        return queryFactory
                .select(new QCountStatusComplaintRes(
                        complaint.count()).as("count"))
                .from(complaint)
                .where(complaint.active.eq(true))
                .groupBy(complaint.status)
                .fetch();
    }

    private BooleanExpression eqPostId(Long id) {
        return id != null ? complaint.id.eq(id) : null;
    }

    private BooleanExpression eqUserId(Long id) {
        return id != null ? user.id.eq(id) : null;
    }

    private BooleanExpression eqName(String name) throws Exception {
        return hasText(name) ? user.name.contains(aesUtil.aesCBCEncode(name)) : null;
    }

    private BooleanExpression containsTitle(String title) {
        return hasText(title) ? complaint.title.contains(title) : null;
    }

    private BooleanExpression eqStatus(Status status) {
        return status != null ? complaint.status.eq(status) : null;
    }
}
