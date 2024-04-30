package com.encore.thecatch.user.repository;

import com.encore.thecatch.common.CatchException;
import com.encore.thecatch.common.ResponseCode;
import com.encore.thecatch.common.querydsl.Querydsl4RepositorySupport;
import com.encore.thecatch.common.util.AesUtil;
import com.encore.thecatch.company.domain.Company;
import com.encore.thecatch.user.domain.Gender;
import com.encore.thecatch.user.domain.Grade;
import com.encore.thecatch.user.domain.QUser;
import com.encore.thecatch.user.domain.User;
import com.encore.thecatch.user.dto.request.SignUpMonthReq;
import com.encore.thecatch.user.dto.request.UserSearchDto;
import com.encore.thecatch.user.dto.response.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.springframework.util.StringUtils.hasText;

@Repository
public class UserQueryRepository extends Querydsl4RepositorySupport {

    private final AesUtil aesUtil;

    QUser user = QUser.user;

    public UserQueryRepository(AesUtil aesUtil) {
        super(User.class);
        this.aesUtil = aesUtil;
    }

    public List<ChartGradeRes> countGrade() {
        return select(new QChartGradeRes(
                user.grade,
                user.grade.count()))
                .from(user)
                .groupBy(user.grade)
                .orderBy(user.grade.asc())
                .fetch();
    }

    public List<ChartGenderRes> countGender() {
        return select(new QChartGenderRes(
                user.gender,
                user.gender.count()))
                .from(user)
                .groupBy(user.gender)
                .orderBy(user.gender.asc())
                .fetch();
    }

    public List<ChartAgeRes> countAge() {
        return select(new QChartAgeRes(
                new CaseBuilder()
                        .when(user.birthDate.year().subtract(LocalDate.now().getYear()).abs().between(0, 9)).then("0")
                        .when(user.birthDate.year().subtract(LocalDate.now().getYear()).abs().between(10, 19)).then("10")
                        .when(user.birthDate.year().subtract(LocalDate.now().getYear()).abs().between(20, 29)).then("20")
                        .when(user.birthDate.year().subtract(LocalDate.now().getYear()).abs().between(30, 39)).then("30")
                        .when(user.birthDate.year().subtract(LocalDate.now().getYear()).abs().between(40, 49)).then("40")
                        .when(user.birthDate.year().subtract(LocalDate.now().getYear()).abs().between(50, 59)).then("50")
                        .when(user.birthDate.year().subtract(LocalDate.now().getYear()).abs().between(60, 69)).then("60")
                        .otherwise("70대 이상")
                        .as("ageGroup"),
                user.count()))
                .from(user)
                .groupBy(new CaseBuilder()
                        .when(user.birthDate.year().subtract(LocalDate.now().getYear()).abs().between(0, 9)).then("0")
                        .when(user.birthDate.year().subtract(LocalDate.now().getYear()).abs().between(10, 19)).then("10")
                        .when(user.birthDate.year().subtract(LocalDate.now().getYear()).abs().between(20, 29)).then("20")
                        .when(user.birthDate.year().subtract(LocalDate.now().getYear()).abs().between(30, 39)).then("30")
                        .when(user.birthDate.year().subtract(LocalDate.now().getYear()).abs().between(40, 49)).then("40")
                        .when(user.birthDate.year().subtract(LocalDate.now().getYear()).abs().between(50, 59)).then("50")
                        .when(user.birthDate.year().subtract(LocalDate.now().getYear()).abs().between(60, 69)).then("60")
                        .otherwise("70대 이상"))
                .fetch();
    }

    public Page<UserListRes> UserList(UserSearchDto userSearchDto, Company company, Pageable pageable) throws Exception {
        return applyPagination(
                pageable,
                query -> {
                    try {
                        return query
                                .select(
                                        new QUserListRes(
                                                user.id,
                                                user.name,
                                                user.email,
                                                user.birthDate,
                                                user.phoneNumber,
                                                user.gender,
                                                user.grade))
                                .from(user)
                                .where(containsName(userSearchDto.getName()),
                                        eqEmail(userSearchDto.getEmail()),
                                        eqBrithDate(userSearchDto.getBirthDate()),
                                        containsPhoneNumber(userSearchDto.getPhoneNumber()),
                                        eqGender(userSearchDto.getGender()),
                                        eqGrade(userSearchDto.getGrade()),
                                        user.company.eq(company));
                    } catch (Exception e) {
                        throw new CatchException(ResponseCode.AES_ENCODE_FAIL);
                    }
                },
                countQuery -> {
                    try {
                        return countQuery
                                .selectFrom(user)
                                .where(
                                        containsName(userSearchDto.getName()),
                                        eqEmail(userSearchDto.getEmail()),
                                        eqBrithDate(userSearchDto.getBirthDate()),
                                        containsPhoneNumber(userSearchDto.getPhoneNumber()),
                                        eqGender(userSearchDto.getGender()),
                                        eqGrade(userSearchDto.getGrade()),
                                        user.company.eq(company));
                    } catch (Exception e) {
                        throw new CatchException(ResponseCode.AES_ENCODE_FAIL);
                    }
                }
        );
    }

    public List<SignUpMonth> signUpMonth(SignUpMonthReq signUpMonthReq) {
        String dateString = signUpMonthReq.getMonth();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        YearMonth currentMonth = YearMonth.parse(dateString, formatter);
        LocalDateTime startOfMonth = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = currentMonth.atEndOfMonth().atTime(LocalTime.MAX);

        return select(
                new QSignUpMonth(
                        user.createdTime,
                        user.count()))
                .from(user)
                .where(
                        user.active.eq(true),
                        user.createdTime.goe(startOfMonth),
                        user.createdTime.loe(endOfMonth))
                .groupBy(user.createdTime.dayOfMonth())
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

    private BooleanExpression containsPhoneNumber(String phoneNumber) throws Exception {
        return hasText(phoneNumber) ? user.email.contains(aesUtil.aesCBCEncode(phoneNumber)) : null;
    }
}
