package com.encore.thecatch.log.service;

import com.encore.thecatch.log.dto.DayOfWeekLogin;
import com.encore.thecatch.log.dto.VisitTodayUserRes;
import com.encore.thecatch.log.repository.EmailLogQueryRepository;
import com.encore.thecatch.log.repository.UserLogQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class LogService {
    private final UserLogQueryRepository userLogQueryRepository;
    private final EmailLogQueryRepository emailLogQueryRepository;

    public Long visitTotalUser() {
        return userLogQueryRepository.visitTotalUser();
    }

    public Long visitToday() {
        return userLogQueryRepository.visitToday();
    }

    public Long visitTodayUserCount() {
        List<VisitTodayUserRes> list = userLogQueryRepository.visitTodayUser();
        return (long) list.size();
    }

    public List<DayOfWeekLogin> dayOfWeekLogin() {
        return userLogQueryRepository.dayOfWeekLogin();
    }

    public Long totalEmail() {
        return emailLogQueryRepository.totalEmail();
    }

}
