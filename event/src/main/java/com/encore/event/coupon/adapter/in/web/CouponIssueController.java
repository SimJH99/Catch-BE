package com.encore.event.coupon.adapter.in.web;


import com.encore.event.common.WebAdapter;
import com.encore.event.common.respone.ResponseDto;
import com.encore.event.coupon.application.port.in.ApplyForLimitedCouponIssueCommend;
import com.encore.event.coupon.application.port.in.ApplyForLimitedCouponIssueUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@WebAdapter
@RestController
@RequiredArgsConstructor
public class CouponIssueController {

    private final ApplyForLimitedCouponIssueUseCase applyForLimitedCouponIssueUseCase;

    @PostMapping("/coupon/limited/apply")
    public ResponseDto limitedCouponApply(@RequestBody ApplyForLimitedCouponIssueCommend commend){

        return applyForLimitedCouponIssueUseCase.applyForLimitedCouponIssue(commend);
    }


}
