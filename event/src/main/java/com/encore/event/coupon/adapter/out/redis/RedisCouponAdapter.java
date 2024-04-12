package com.encore.event.coupon.adapter.out.redis;

import com.encore.event.config.RedisOperation;
import com.encore.event.config.RedisTransaction;
import com.encore.event.coupon.adapter.out.redis.operation.LimitedCouponIssueOperation;
import com.encore.event.coupon.application.port.in.ApplyForLimitedCouponIssueCommend;
import com.encore.event.coupon.application.port.out.RedisCouponOutPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class RedisCouponAdapter implements RedisCouponOutPort {


    private final RedisTransaction redisTransaction;

    private final LimitedCouponIssueOperation limitedCouponIssueOperation;
    private final RedisOperations<String,Object> redisOperations;


    @Override
    public Boolean limitedCouponIssue(ApplyForLimitedCouponIssueCommend commend) {

        List<Long> result = (List) redisTransaction.execute(redisOperations, limitedCouponIssueOperation, commend);
        Long currentCnt = result.get(0);

        Long limitCnt = commend.getLimit() - 1L;

        //넣기 전에 갯수를 세기 때문에 마지막 하나는 유효하지 않은 데이터임
        if(currentCnt <= limitCnt && result.get(1) != 0) {
            return true;
        }

        return false;
    }

}
