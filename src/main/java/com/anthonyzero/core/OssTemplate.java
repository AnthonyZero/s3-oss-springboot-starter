package com.anthonyzero.core;


import com.amazonaws.services.s3.AmazonS3;
import com.anthonyzero.spring.boot.autoconfigure.properties.OssProperties;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OssTemplate {

    private final OssProperties ossProperties;
    private final AmazonS3 amazonS3;


}
