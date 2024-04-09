//package com.encore.thecatch.admin.repository;
//
//import com.encore.thecatch.admin.domain.QAdmin;
//import com.encore.thecatch.admin.dto.response.AdminInfoDto;
//import com.encore.thecatch.admin.dto.response.AdminSearchDto;
//import com.encore.thecatch.admin.dto.response.QAdminInfoDto;
//import com.encore.thecatch.common.dto.Role;
//import com.encore.thecatch.common.util.AesUtil;
//import com.querydsl.core.types.dsl.BooleanExpression;
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//import static org.springframework.util.StringUtils.hasText;
//
//@Repository
//@RequiredArgsConstructor
//public class AdminQueryRepository {
//
//    QAdmin admin = QAdmin.admin;
//    private final JPAQueryFactory jpaQueryFactory;
//    private final AesUtil aesUtil;
//
//    public List<AdminInfoDto> findAdminList(AdminSearchDto adminSearchDto) throws Exception {
//        return jpaQueryFactory
//                .select(new QAdminInfoDto(
//                        admin.name,
//                        admin.employeeNumber,
//                        admin.email,
//                        admin.role))
//                .from(admin)
//                .where(
//                        eqName(adminSearchDto.getName()),
//                        eqEmployeeNumber(adminSearchDto.getEmployeeNumber()),
//                        eqEmail(adminSearchDto.getEmail()),
//                        eqRole(adminSearchDto.getRole())
//                ).fetch();
//    }
//
//
//    private BooleanExpression eqName(String name) throws Exception {
//        return hasText(name) ? admin.name.eq(aesUtil.aesCBCEncode(name)) : null;
//    }
//
//    private BooleanExpression eqEmployeeNumber(String employeeNumber) throws Exception {
//        return hasText(employeeNumber) ? admin.employeeNumber.eq(aesUtil.aesCBCEncode(employeeNumber)) : null;
//    }
//
//    private BooleanExpression eqEmail(String email) throws Exception {
//        return hasText(email)? admin.email.eq(aesUtil.aesCBCEncode(email)) : null;
//    }
//
//    private BooleanExpression eqRole(Role role) {
//        return role != null ? admin.role.eq(role) : null;
//    }
//
//}
