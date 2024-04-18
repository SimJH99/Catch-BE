package com.encore.thecatch.complaint.dto.response;

import com.encore.thecatch.complaint.entity.Complaint;
import com.encore.thecatch.complaint.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyComplaintRes {
    private Long id;
    private String title;
    private String createdTime;
    private Status status;

    public static MyComplaintRes toDto(final Complaint complaint) {
        return MyComplaintRes.builder()
                .id(complaint.getId())
                .title(complaint.getTitle())
                .createdTime(complaint.getCreatedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .status(complaint.getStatus())
                .build();
    }
}
