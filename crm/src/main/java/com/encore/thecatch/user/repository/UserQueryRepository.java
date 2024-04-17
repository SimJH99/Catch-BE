package com.encore.thecatch.user.repository;

import com.encore.thecatch.user.domain.QUser;
import com.encore.thecatch.user.dto.response.*;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserQueryRepository {
    private final JPAQueryFactory queryFactory;

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
                                        .when(user.brithDate.year().subtract(LocalDate.now().getYear()).abs().between(0,9)).then("0")
                                        .when(user.brithDate.year().subtract(LocalDate.now().getYear()).abs().between(10,19)).then("10")
                                        .when(user.brithDate.year().subtract(LocalDate.now().getYear()).abs().between(20,29)).then("20")
                                        .when(user.brithDate.year().subtract(LocalDate.now().getYear()).abs().between(30,39)).then("30")
                                        .when(user.brithDate.year().subtract(LocalDate.now().getYear()).abs().between(40,49)).then("40")
                                        .when(user.brithDate.year().subtract(LocalDate.now().getYear()).abs().between(50,59)).then("50")
                                        .when(user.brithDate.year().subtract(LocalDate.now().getYear()).abs().between(60,69)).then("60")
                                        .otherwise("70대 이상")
                                        .as("ageGroup"),
                                user.count()))
                                .from(user)
                                .groupBy(new CaseBuilder()
                                        .when(user.brithDate.year().subtract(LocalDate.now().getYear()).abs().between(0,9)).then("0")
                                        .when(user.brithDate.year().subtract(LocalDate.now().getYear()).abs().between(10,19)).then("10")
                                        .when(user.brithDate.year().subtract(LocalDate.now().getYear()).abs().between(20,29)).then("20")
                                        .when(user.brithDate.year().subtract(LocalDate.now().getYear()).abs().between(30,39)).then("30")
                                        .when(user.brithDate.year().subtract(LocalDate.now().getYear()).abs().between(40,49)).then("40")
                                        .when(user.brithDate.year().subtract(LocalDate.now().getYear()).abs().between(50,59)).then("50")
                                        .when(user.brithDate.year().subtract(LocalDate.now().getYear()).abs().between(60,69)).then("60")
                                        .otherwise("70대 이상"))
                                .fetch();
    }
}
