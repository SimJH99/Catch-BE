package com.encore.thecatch.complaint.service;

import com.encore.thecatch.common.CatchException;
import com.encore.thecatch.common.ResponseCode;
import com.encore.thecatch.common.s3.S3Service;
import com.encore.thecatch.common.util.AesUtil;
import com.encore.thecatch.common.util.MaskingUtil;
import com.encore.thecatch.common.util.S3UrlUtil;
import com.encore.thecatch.complaint.dto.request.AddImageReq;
import com.encore.thecatch.complaint.dto.request.CreateComplaintReq;
import com.encore.thecatch.complaint.dto.request.SearchComplaintCondition;
import com.encore.thecatch.complaint.dto.request.UpdateComplaintReq;
import com.encore.thecatch.complaint.dto.response.*;
import com.encore.thecatch.complaint.entity.Complaint;
import com.encore.thecatch.complaint.entity.Image;
import com.encore.thecatch.complaint.repository.ComplaintQueryRepository;
import com.encore.thecatch.complaint.repository.ComplaintRepository;
import com.encore.thecatch.complaint.repository.ImageRepository;
import com.encore.thecatch.user.domain.User;
import com.encore.thecatch.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class ComplaintService {

    private final UserRepository userRepository;
    private final ComplaintRepository complaintRepository;
    private final ComplaintQueryRepository complaintQueryRepository;
    private final ImageRepository imageRepository;
    private final S3Service s3Service;
    private final S3UrlUtil s3UrlUtil;
    private final AesUtil aesUtil;
    private final MaskingUtil maskingUtil;


    @PreAuthorize("hasAuthority('USER')")
    public Complaint createComplaint(CreateComplaintReq createComplaintReq) {
        List<String> imgkeys = null;
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = userRepository.findByEmail((authentication.getName())).orElseThrow(() -> new CatchException(ResponseCode.USER_NOT_FOUND));

            Complaint newComplaint = createComplaintReq.toEntity(user);
            complaintRepository.save(newComplaint);

            if (createComplaintReq.getImages() != null && !createComplaintReq.getImages().isEmpty()) {
                imgkeys = s3Service.uploadList("complaint", createComplaintReq.getImages());
            }

            if (imgkeys != null) {
                for (String imgKey : imgkeys) {
                    Image img = new Image(imgKey, newComplaint);
                    imageRepository.save(img);
                }
            }
            return newComplaint;
        } catch (CatchException e) {
            if (imgkeys != null && !imgkeys.isEmpty()) {
                for (String keys : imgkeys) {
                    s3Service.deleteFile(keys);
                }
            }
            throw new CatchException(ResponseCode.S3_UPLOAD_ERROR);
        }
    }

    public Complaint deletePost(Long id) {
        Complaint complaint = complaintRepository.findById(id).orElseThrow(() -> new CatchException(ResponseCode.POST_NOT_FOUND));
        complaint.isDelete();
        return complaint;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','CS', 'MARKETER', 'USER')")
    public DetailComplaintRes detailComplaint(Long id) {
        activeComplaint(id);
        Complaint complaint = complaintRepository.findById(id).orElseThrow(() -> new CatchException(ResponseCode.POST_NOT_FOUND));
        List<Image> imgList = imageRepository.findAllByComplaintId(complaint.getId());
        Map<Long, String> imgUrls = new HashMap<>();


        for (Image image : imgList) {
            String key = image.getKeyValue();
            String url = s3UrlUtil.setUrl();
            Long imgId = image.getId();
            imgUrls.put(imgId, url + key);
        }

        return DetailComplaintRes.from(complaint, imgUrls);
    }

    @PreAuthorize("hasAuthority('USER')")
    public UpdateComplaintRes updateComplaint(Long id, UpdateComplaintReq updateComplaintReq) {
        Complaint complaint = complaintRepository.findById(id).orElseThrow(() -> new CatchException(ResponseCode.POST_NOT_FOUND));
        complaint.updatePost(updateComplaintReq.getTitle(), updateComplaintReq.getCategory(), updateComplaintReq.getContents());
        return UpdateComplaintRes.from(complaint);
    }


    @PreAuthorize("hasAuthority('USER')")
    public Page<MyComplaints> myComplaintList(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new CatchException(ResponseCode.USER_NOT_FOUND));
        return complaintQueryRepository.findMyComplaintList(user, pageable).map(MyComplaints::toDto);
    }

    @PreAuthorize("hasAuthority('USER')")
    public List<MyPageComplaints> myPageComplaints() {
        return complaintQueryRepository.myPageComplaints();
    }

    // 삭제된 게시글 확인
    public void activeComplaint(Long id) {
        Complaint complaint = complaintRepository.findById(id).orElseThrow(() -> new CatchException(ResponseCode.POST_NOT_FOUND));
        if (!complaint.isActive()) {
            throw new CatchException(ResponseCode.POST_NOT_ACTIVE);
        }
    }

    @PreAuthorize("hasAuthority('USER')")
    public AddImageRes addImage(Long id, AddImageReq addImageReq) {
        String imgKey = null;
        Complaint complaint = complaintRepository.findById(id).orElseThrow(() -> new CatchException(ResponseCode.POST_NOT_FOUND));
        if (addImageReq.getImage() != null) {
            imgKey = s3Service.upload("complaint", addImageReq.getImage());
            Image img = new Image(imgKey, complaint);
            imageRepository.save(img);
        }
        return AddImageRes.from(s3UrlUtil.setUrl() + imgKey);
    }

    @PreAuthorize("hasAuthority('USER')")
    public void deleteImage(Long id) {
        Image image = imageRepository.findById(id).orElseThrow(() -> new CatchException(ResponseCode.IMAGE_NOT_FOUND));
        String imageKey = image.getKeyValue();
        imageRepository.delete(image);
        s3Service.deleteFile(imageKey);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','CS','MARKETER')")
    public Page<ListComplaintRes> searchComplaint(SearchComplaintCondition searchComplaintCondition, Pageable pageable) throws Exception {
        List<ListComplaintRes> listComplaintRes = complaintQueryRepository.findComplaintList(searchComplaintCondition);
        List<ListComplaintRes> listComplaintRes1 = new ArrayList<>();
        for (ListComplaintRes ListComplaint : listComplaintRes) {
            ListComplaint = ListComplaintRes.builder()
                    .complaintId(ListComplaint.getComplaintId())
                    .name(maskingUtil.nameMasking(aesUtil.aesCBCDecode(ListComplaint.getName())))
                    .title(ListComplaint.getTitle())
                    .status(ListComplaint.getStatus())
                    .build();
            listComplaintRes1.add(ListComplaint);
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), listComplaintRes1.size());

        return new PageImpl<>(listComplaintRes1.subList(start, end), pageable, listComplaintRes.size());
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','CS','MARKETER')")
    public Long countAllComplaint() {
        return complaintQueryRepository.countAllComplaint();
    }

    public List<CountStatusComplaintRes> countStatusComplaint() {
        return complaintQueryRepository.countStatusComplaint();
    }
}
