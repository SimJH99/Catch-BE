package com.encore.thecatch.event.repository;

import com.encore.thecatch.admin.dto.response.AdminInfoDto;
import com.encore.thecatch.admin.dto.response.AdminSearchDto;
import com.encore.thecatch.admin.dto.response.QAdminInfoDto;
import com.encore.thecatch.company.domain.Company;
import com.encore.thecatch.coupon.domain.Coupon;
import com.encore.thecatch.coupon.dto.CouponFindResDto;
import com.encore.thecatch.event.domain.Event;
import com.encore.thecatch.event.domain.QEvent;
import com.encore.thecatch.event.dto.response.EventInfoDto;
import com.encore.thecatch.event.dto.response.EventSearchDto;
import com.encore.thecatch.event.dto.response.QEventInfoDto;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
public class EventQueryRepository {

    private final JPAQueryFactory queryFactory;

    QEvent event = QEvent.event;

    public Page<EventInfoDto> findEventList(EventSearchDto eventSearchDto, Company company, Pageable pageable) throws Exception {
        List<EventInfoDto> content = queryFactory
                .select(new QEventInfoDto(
                        event.id,
                        event.name,
                        event.startDate,
                        event.endDate
                        ))
                .from(event)
                .where(
                        containsName(eventSearchDto.getName()),
                        containsStartDate(eventSearchDto.getStartDate()),
                        containsEndDate(eventSearchDto.getEndDate()),
                        event.companyId.eq(company))
                .orderBy(event.createdTime.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        JPAQuery<Event> countQuery = queryFactory
                .selectFrom(event)
                .where(
                        containsName(eventSearchDto.getName()),
                        containsStartDate(eventSearchDto.getStartDate()),
                        containsEndDate(eventSearchDto.getEndDate()),
                        event.companyId.eq(company)
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchCount);
    }

    private Predicate containsStartDate(String startDate) {
        return hasText(startDate) ? event.startDate.eq(LocalDate.from(LocalDate.parse(startDate).atStartOfDay())) : null;
    }

    private Predicate containsEndDate(String endDate) {
        return hasText(endDate) ? event.endDate.eq(LocalDate.from(LocalDate.parse(endDate).atStartOfDay())) : null;
    }

    private Predicate containsName(String name) {
        return hasText(name) ? event.name.eq(name) : null;
    }

}
