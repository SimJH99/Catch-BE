package com.encore.thecatch.event.repository;

import com.encore.thecatch.common.querydsl.Querydsl4RepositorySupport;
import com.encore.thecatch.company.domain.Company;
import com.encore.thecatch.event.domain.Event;
import com.encore.thecatch.event.domain.QEvent;
import com.encore.thecatch.event.dto.response.EventInfoDto;
import com.encore.thecatch.event.dto.response.EventSearchDto;
import com.encore.thecatch.event.dto.response.QEventInfoDto;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

import static org.springframework.util.StringUtils.hasText;

@Repository
public class EventQueryRepository extends Querydsl4RepositorySupport {

    QEvent event = QEvent.event;

    public EventQueryRepository() {
        super(Event.class);
    }

    public Page<EventInfoDto> findEventList(EventSearchDto eventSearchDto, Company company, Pageable pageable) {
        return applyPagination(
                pageable,
                query -> query
                        .select(
                                new QEventInfoDto(
                                        event.id,
                                        event.name,
                                        event.startDate,
                                        event.endDate))
                        .from(event)
                        .where(containsName(eventSearchDto.getName()),
                                containsStartDate(eventSearchDto.getStartDate()),
                                containsEndDate(eventSearchDto.getEndDate()),
                                event.companyId.eq(company))
                        .orderBy(event.createdTime.desc()),
                countQuery -> countQuery
                        .selectFrom(event)
                        .where(
                                containsName(eventSearchDto.getName()),
                                containsStartDate(eventSearchDto.getStartDate()),
                                containsEndDate(eventSearchDto.getEndDate()),
                                event.companyId.eq(company)));
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
