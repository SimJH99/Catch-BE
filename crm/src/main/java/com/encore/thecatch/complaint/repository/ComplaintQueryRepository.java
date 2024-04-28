package com.encore.thecatch.complaint.repository;


import com.encore.thecatch.common.CatchException;
import com.encore.thecatch.common.ResponseCode;
import com.encore.thecatch.common.querydsl.Querydsl4RepositorySupport;
import com.encore.thecatch.common.util.AesUtil;
import com.encore.thecatch.complaint.dto.request.SearchComplaintCondition;
import com.encore.thecatch.complaint.dto.response.CountStatusComplaintRes;
import com.encore.thecatch.complaint.dto.response.QCountStatusComplaintRes;
import com.encore.thecatch.complaint.entity.Complaint;
import com.encore.thecatch.complaint.entity.QComplaint;
import com.encore.thecatch.complaint.entity.Status;
import com.encore.thecatch.user.domain.QUser;
import com.encore.thecatch.user.domain.User;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.util.StringUtils.hasText;

@Repository
public class ComplaintQueryRepository extends Querydsl4RepositorySupport {

    private final AesUtil aesUtil;

    QComplaint complaint = QComplaint.complaint;
    QUser user = QUser.user;

    public ComplaintQueryRepository(AesUtil aesUtil) {
        super(Complaint.class);
        this.aesUtil = aesUtil;
    }


    //나의 문의글 보기
    public Page<Complaint> findMyComplaintList(User user, Pageable pageable) {
        return applyPagination(
                pageable,
                query -> query
                        .selectFrom(complaint)
                        .where(complaint.user.eq(user), complaint.active.eq(true)),
                countQuery -> countQuery
                        .select(complaint.id)
                        .from(complaint)
                        .where(complaint.user.eq(user), complaint.active.eq(true)));
    }

    //문의글 검색 기능
    public Page<Complaint> findComplaintList(SearchComplaintCondition searchComplaintCondition, Pageable pageable) {
        return applyPagination(
                pageable,
                query -> {
                    try {
                        return query
                                .selectFrom(complaint)
                                .leftJoin(complaint.user, user)
                                .where(
                                        eqPostId(searchComplaintCondition.getComplaintId()),
                                        eqName(searchComplaintCondition.getName()),
                                        containsTitle(searchComplaintCondition.getTitle()),
                                        eqStatus(searchComplaintCondition.getStatus()),
                                        complaint.active.eq(true)
                                )
                                .orderBy(complaint.status.asc(), complaint.createdTime.asc());
                    } catch (Exception e) {
                        throw new CatchException(ResponseCode.AES_ENCODE_FAIL);
                    }
                },
                countQuery -> {
                    try {
                        return countQuery
                                .selectFrom(complaint)
                                .where(
                                        eqPostId(searchComplaintCondition.getComplaintId()),
                                        eqName(searchComplaintCondition.getName()),
                                        containsTitle(searchComplaintCondition.getTitle()),
                                        eqStatus(searchComplaintCondition.getStatus()),
                                        complaint.active.eq(true));
                    } catch (Exception e) {
                        throw new CatchException(ResponseCode.AES_ENCODE_FAIL);
                    }
                }
        );
    }

    public List<Complaint> myPageComplaints() {
        return selectFrom(complaint)
                .where(complaint.active.eq(true))
                .orderBy(complaint.createdTime.desc())
                .limit(5)
                .fetch();
    }

    public Long countAllComplaint() {
        return selectFrom(complaint)
                .where(complaint.active.eq(true))
                .fetchCount();
    }


    public List<CountStatusComplaintRes> countStatusComplaint() {
        return select(new QCountStatusComplaintRes(
                complaint.status,
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
