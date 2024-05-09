package com.encore.thecatch.event.domain;

import com.encore.thecatch.common.entity.BaseEntity;
import com.encore.thecatch.company.domain.Company;
import com.encore.thecatch.event.dto.request.EventUpdateDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Lob
    private String contents;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company companyId;

    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus;

    public void publishEvent(){
        this.eventStatus = EventStatus.PUBLISH;
    }

    public void eventUpdate(EventUpdateDto eventUpdateDto) {
        this.name = eventUpdateDto.getName();
        this.contents = eventUpdateDto.getContents();
        this.startDate = eventUpdateDto.getStartDate();
        this.endDate = eventUpdateDto.getEndDate();
    }
}
