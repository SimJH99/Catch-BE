package com.encore.thecatch.complaint.dto.response;

import com.encore.thecatch.complaint.entity.Complaint;
import com.encore.thecatch.complaint.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyPageComplaints {
    Long id;
    String title;
    Status status;

    public static MyPageComplaints toDto(final Complaint complaint) {
      return MyPageComplaints.builder()
              .id(complaint.getId())
              .title(complaint.getTitle())
              .status(complaint.getStatus())
              .build();
    }
}
