package com.encore.thecatch.user.repository;

import com.encore.thecatch.common.util.AesUtil;
import com.encore.thecatch.company.domain.Company;
import com.encore.thecatch.user.domain.Gender;
import com.encore.thecatch.user.domain.Grade;
import com.encore.thecatch.user.domain.QUser;
import com.encore.thecatch.user.dto.request.UserSearchDto;
import com.encore.thecatch.user.dto.response.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
public class UserQueryRepository {
    private final JPAQueryFactory queryFactory;
    private final AesUtil aesUtil;

    QUser user = QUser.user;

    public List<ChartGradeRes> countGrade() {
        return queryFactory
                .select(new QChartGradeRes(
                        user.grade,
                        user.grade.count()))
                .from(user)
                .groupBy(user.grade)
                .orderBy(user.grade.asc())
                .fetch();
    }

    public List<ChartGenderRes> countGender() {
        return queryFactory
                .select(new QChartGenderRes(
                        user.gender,
                        user.gender.count()))
                .from(user)
                .groupBy(user.gender)
                .orderBy(user.gender.asc())
                .fetch();
    }

    public List<ChartAgeRes> countAge() {
        return queryFactory.select(new QChartAgeRes(
                                new CaseBuilder()
                                        .when(user.birthDate.year().subtract(LocalDate.now().getYear()).abs().between(0,9)).then("0")
                                        .when(user.birthDate.year().subtract(LocalDate.now().getYear()).abs().between(10,19)).then("10")
                                        .when(user.birthDate.year().subtract(LocalDate.now().getYear()).abs().between(20,29)).then("20")
                                        .when(user.birthDate.year().subtract(LocalDate.now().getYear()).abs().between(30,39)).then("30")
                                        .when(user.birthDate.year().subtract(LocalDate.now().getYear()).abs().between(40,49)).then("40")
                                        .when(user.birthDate.year().subtract(LocalDate.now().getYear()).abs().between(50,59)).then("50")
                                        .when(user.birthDate.year().subtract(LocalDate.now().getYear()).abs().between(60,69)).then("60")
                                        .otherwise("70대 이상")
                                        .as("ageGroup"),
                                user.count()))
                                .from(user)
                                .groupBy(new CaseBuilder()
                                        .when(user.birthDate.year().subtract(LocalDate.now().getYear()).abs().between(0,9)).then("0")
                                        .when(user.birthDate.year().subtract(LocalDate.now().getYear()).abs().between(10,19)).then("10")
                                        .when(user.birthDate.year().subtract(LocalDate.now().getYear()).abs().between(20,29)).then("20")
                                        .when(user.birthDate.year().subtract(LocalDate.now().getYear()).abs().between(30,39)).then("30")
                                        .when(user.birthDate.year().subtract(LocalDate.now().getYear()).abs().between(40,49)).then("40")
                                        .when(user.birthDate.year().subtract(LocalDate.now().getYear()).abs().between(50,59)).then("50")
                                        .when(user.birthDate.year().subtract(LocalDate.now().getYear()).abs().between(60,69)).then("60")
                                        .otherwise("70대 이상"))
                                .fetch();
    }

    public List<UserListRes> UserList(UserSearchDto userSearchDto, Company company) throws Exception {
        return queryFactory
                .select(new QUserListRes(
                        user.id,
                        user.name,
                        user.email,
                        user.birthDate,
                        user.phoneNumber,
                        user.gender,
                        user.grade))
                .from(user)
                .where(
                        containsName(userSearchDto.getName()),
                        eqEmail(userSearchDto.getEmail()),
                        eqBrithDate(userSearchDto.getBirthDate()),
                        containsPhoneNumber(userSearchDto.getPhoneNumber()),
                        eqGender(userSearchDto.getGender()),
                        eqGrade(userSearchDto.getGrade()),
                        user.company.eq(company))
                .orderBy(user.active.asc(), user.createdTime.desc(), user.name.asc())
                .fetch();
    }

    private BooleanExpression containsName(String name) throws Exception {
        return hasText(name) ? user.name.contains(aesUtil.aesCBCEncode(name)) : null;
    }
    private BooleanExpression eqEmail(String email) throws Exception {
        return hasText(email) ? user.email.eq(aesUtil.aesCBCEncode(email)) : null;
    }

    private BooleanExpression eqGender(Gender gender) {
        return gender != null ? user.gender.eq(gender) : null;
    }

    private BooleanExpression eqGrade(Grade grade) {
        return grade != null ? user.grade.eq(grade) : null;
    }

    private BooleanExpression eqBrithDate(LocalDate birthDate) {
        return birthDate != null ? user.birthDate.eq(birthDate) : null;
    }
    private BooleanExpression containsPhoneNumber (String phoneNumber) throws Exception {
        return hasText(phoneNumber) ? user.email.contains(aesUtil.aesCBCEncode(phoneNumber)) : null;
    }
}
