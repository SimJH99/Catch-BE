package com.encore.thecatch.complaint.entity;

import com.encore.thecatch.comments.entity.Comments;
import com.encore.thecatch.common.entity.BaseEntity;
import com.encore.thecatch.user.domain.User;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.joda.time.base.BaseDateTime;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Complaint extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    //PK

    @Column(nullable = false)
    private String category;

    @Column(length = 50, nullable = false)
    private String title;

    @Column(length = 3000, nullable = false)
    private String contents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Transient
    @Builder.Default
    private List<Image> imageList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "complaint", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Comments> comments = new ArrayList<>();

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.BEFORE;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;


    //글 내용 수정 메소드
    public void updatePost(String title, String category, String contents) {
        this.title = title;
        this.category = category;
        this.contents = contents;
    }

    //삭제 메소드
    public void isDelete() {
        this.active = false;
    }

    // 답변 완료 시 상태 변화
    public void isReply() {
        this.status = Status.REPLY;
    }

    //답변 삭제 시 상태 변화
    public void isBefore() {
        this.status = Status.BEFORE;
    }

}
