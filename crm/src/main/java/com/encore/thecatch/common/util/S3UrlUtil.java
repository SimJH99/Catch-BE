package com.encore.thecatch.common.util;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Getter
@Service
public class S3UrlUtil {
    private final String region;
    private final String bucket;

    public S3UrlUtil(@Value("${cloud.aws.region.static}") String region,
                     @Value("${cloud.aws.s3.bucket}") String bucket) {
        this.region = region;
        this.bucket = bucket;
    }

    public String setUrl() {
        return "https://" + bucket + ".s3." + region + ".amazonaws.com/";
    }

}
