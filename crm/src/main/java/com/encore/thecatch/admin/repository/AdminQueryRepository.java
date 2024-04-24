package com.encore.thecatch.admin.repository;

import com.encore.thecatch.admin.domain.QAdmin;
import com.encore.thecatch.admin.dto.response.AdminInfoDto;
import com.encore.thecatch.admin.dto.response.AdminSearchDto;
import com.encore.thecatch.admin.dto.response.QAdminInfoDto;
import com.encore.thecatch.common.dto.Role;
import com.encore.thecatch.common.util.AesUtil;
import com.encore.thecatch.company.domain.Company;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
public class AdminQueryRepository {
    private final JPAQueryFactory queryFactory;
    private final AesUtil aesUtil;

    QAdmin admin = QAdmin.admin;

    public List<AdminInfoDto> findAdminList(AdminSearchDto adminSearchDto, Company company) throws Exception {
        return queryFactory
                .select(new QAdminInfoDto(
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
                .orderBy(admin.createdTime.desc())
                .fetch();
    }


    private BooleanExpression containsName(String name) throws Exception {
        return hasText(name) ? admin.name.eq(aesUtil.aesCBCEncode(name)) : null;
    }

    private BooleanExpression containsEmployeeNumber(String employeeNumber) throws Exception {
        return hasText(employeeNumber) ? admin.employeeNumber.eq(aesUtil.aesCBCEncode(employeeNumber)) : null;
    }

    private BooleanExpression containsEmail(String email) throws Exception {
        return hasText(email)? admin.email.eq(aesUtil.aesCBCEncode(email)) : null;
    }

    private BooleanExpression eqRole(Role role) {
        return role != null ? admin.role.ne(Role.ADMIN).and(admin.role.eq(role)) : admin.role.ne(Role.ADMIN);
    }
}
