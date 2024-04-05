package com.encore.thecatch.complaint.service;

import com.encore.thecatch.common.CatchException;
import com.encore.thecatch.common.ResponseCode;
import com.encore.thecatch.common.s3.S3Service;
import com.encore.thecatch.common.util.AesUtil;
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
import java.util.List;

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

    @PreAuthorize("hasAuthority('USER')")
    public Complaint createComplaint(CreateComplaintReq createComplaintReq) {
        List<String> imgkeys = null;
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new CatchException(ResponseCode.USER_NOT_FOUND));

            Complaint newComplaint = createComplaintReq.toEntity(user);
            complaintRepository.save(newComplaint);

            if (createComplaintReq.getImages() != null  && !createComplaintReq.getImages().isEmpty()) {
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
            if(imgkeys != null && !imgkeys.isEmpty()){
                for (String keys : imgkeys){
                    s3Service.deleteFile(keys);
                }
            }
            throw new  CatchException(ResponseCode.S3_UPLOAD_ERROR);
        }
    }

    public Complaint deletePost(Long id) {
        Complaint complaint = complaintRepository.findById(id).orElseThrow(() -> new CatchException(ResponseCode.POST_NOT_FOUND));
        complaint.isDelete();
        return complaint;
    }

    @PreAuthorize("hasAuthority('USER')")
    public DetailComplaintRes detailComplaint(Long id) {
        activeComplaint(id);
        Complaint complaint = complaintRepository.findById(id).orElseThrow(() -> new CatchException(ResponseCode.POST_NOT_FOUND));
        List<Image> imgList = imageRepository.findAllByComplaintId(complaint.getId());
        List<String> imgUrls = new ArrayList<>();

        for (Image image : imgList) {
            String key = image.getKeyValue();
            String url = s3UrlUtil.setUrl();
            imgUrls.add(url + key);
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
    public Page<MyComplaintRes> myComplaintList(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new CatchException(ResponseCode.USER_NOT_FOUND));
        return complaintRepository.findAllByUserIdAndActive(user.getId(), pageable, true).map(MyComplaintRes::toDto);
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

    @PreAuthorize("hasAuthority('MARKETER')")
    public Page<ListComplaintRes> searchComplaint(SearchComplaintCondition searchComplaintCondition, Pageable pageable) throws Exception {
        List<ListComplaintRes> listComplaintRes = complaintQueryRepository.findComplaintList(searchComplaintCondition);
        List<ListComplaintRes> listComplaintRes1 = new ArrayList<>();
        for (ListComplaintRes listPost : listComplaintRes) {
            listPost = com.encore.thecatch.complaint.dto.response.ListComplaintRes.builder()
                    .complaintId(listPost.getComplaintId())
                    .name(aesUtil.aesCBCDecode(listPost.getName()))
                    .title(listPost.getTitle())
                    .status(listPost.getStatus())
                    .build();
            listComplaintRes1.add(listPost);
        }
        return new PageImpl<>(listComplaintRes1, pageable, listComplaintRes.size());
    }
}
