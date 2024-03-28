package com.encore.thecatch.log.domain;

import com.encore.thecatch.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Log extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LogType type; // ADMIN,

    private String method;

    private String data;

    private String ip;

    private String email;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdTime;

}
