package com.encore.thecatch.admin.repository;

import com.encore.thecatch.admin.domain.QAdmin;
import com.encore.thecatch.admin.dto.response.AdminInfoDto;
import com.encore.thecatch.admin.dto.response.AdminSearchDto;
import com.encore.thecatch.admin.dto.response.QAdminInfoDto;
import com.encore.thecatch.common.CatchException;
import com.encore.thecatch.common.ResponseCode;
import com.encore.thecatch.common.dto.Role;
import com.encore.thecatch.common.querydsl.Querydsl4RepositorySupport;
import com.encore.thecatch.common.util.AesUtil;
import com.encore.thecatch.company.domain.Company;
import com.encore.thecatch.complaint.entity.Complaint;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import static org.springframework.util.StringUtils.hasText;

@Repository
public class AdminQueryRepository extends Querydsl4RepositorySupport {
    private final AesUtil aesUtil;

    QAdmin admin = QAdmin.admin;

    public AdminQueryRepository(AesUtil aesUtil) {
        super(Complaint.class);
        this.aesUtil = aesUtil;
    }

    public Page<AdminInfoDto> findAdminList(AdminSearchDto adminSearchDto, Company company, Pageable pageable) throws Exception {
        return applyPagination(
                pageable,
                query -> {
                    try {
                        return query.select(new QAdminInfoDto(
                                        admin.id,
                                        admin.name,
                                        admin.employeeNumber,
                                        admin.email,
                                        admin.role))
                                .from(admin)
                                .where(
                                        containsName(adminSearchDto.getName()),
                                        containsEmployeeNumber(adminSearchDto.getEmployeeNumber()),
                                        containsEmail(adminSearchDto.getEmail()),
                                        eqRole(adminSearchDto.getRole()),
                                        admin.company.eq(company))
                                .orderBy(admin.createdTime.desc());
                    } catch (Exception e) {
                        throw new CatchException(ResponseCode.AES_ENCODE_FAIL);
                    }
                },
                countQuery -> {
                    try {
                        return countQuery.selectFrom(admin)
                                .where(containsName(adminSearchDto.getName()),
                                        containsEmployeeNumber(adminSearchDto.getEmployeeNumber()),
                                        containsEmail(adminSearchDto.getEmail()),
                                        eqRole(adminSearchDto.getRole()),
                                        admin.company.eq(company));
                    } catch (Exception e) {
                        throw new CatchException(ResponseCode.AES_ENCODE_FAIL);
                    }
                }
        );
    }

    private BooleanExpression containsName(String name) throws Exception {
        return hasText(name) ? admin.name.eq(aesUtil.aesCBCEncode(name)) : null;
    }

    private BooleanExpression containsEmployeeNumber(String employeeNumber) throws Exception {
        return hasText(employeeNumber) ? admin.employeeNumber.eq(aesUtil.aesCBCEncode(employeeNumber)) : null;
    }

    private BooleanExpression containsEmail(String email) throws Exception {
        return hasText(email) ? admin.email.eq(aesUtil.aesCBCEncode(email)) : null;
    }

    private BooleanExpression eqRole(Role role) {
        return role != null ? admin.role.ne(Role.ADMIN).and(admin.role.eq(role)) : admin.role.ne(Role.ADMIN);
    }
}
