package com.encore.thecatch.admin.repository;

import com.encore.thecatch.admin.domain.QAdmin;
import com.encore.thecatch.admin.dto.response.AdminInfoDto;
import com.encore.thecatch.admin.dto.response.AdminSearchDto;
import com.encore.thecatch.admin.dto.response.QAdminInfoDto;
import com.encore.thecatch.common.dto.Role;
import com.encore.thecatch.common.util.AesUtil;
import com.encore.thecatch.complaint.entity.Status;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
public class AdminQueryRepository {

    QAdmin admin = QAdmin.admin;
    private final JPAQueryFactory jpaQueryFactory;
    private final AesUtil aesUtil;

    public List<AdminInfoDto> findAdminList(AdminSearchDto adminSearchDto) throws Exception {
        return jpaQueryFactory
                .select(new QAdminInfoDto(
                        admin.name,
                        admin.employeeNumber,
                        admin.email,
                        admin.role))
                .from(admin)
                .where(
                        containsName(adminSearchDto.getName()),
                        containsEmployeeNumber(adminSearchDto.getEmployeeNumber()),
                        containsEmail(adminSearchDto.getEmail()),
                        eqRole(adminSearchDto.getRole())
                ).fetch();
    }


    private BooleanExpression containsName(String name) throws Exception {
        return hasText(name) ? admin.name.contains(aesUtil.aesCBCEncode(name)) : null;
    }

    private BooleanExpression containsEmployeeNumber(String employeeNumber) throws Exception {
        return hasText(employeeNumber) ? admin.employeeNumber.contains(aesUtil.aesCBCEncode(employeeNumber)) : null;
    }

    private BooleanExpression containsEmail(String email) throws Exception {
        return hasText(email)? admin.email.contains(aesUtil.aesCBCEncode(email)) : null;
    }

    private BooleanExpression eqRole(Role role) {
        return role != null ? admin.role.ne(Role.ADMIN).and(admin.role.eq(role)) : admin.role.ne(Role.ADMIN);
    }





}
