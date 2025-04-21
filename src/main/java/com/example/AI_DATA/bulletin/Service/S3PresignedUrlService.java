package com.example.AI_DATA.bulletin.Service;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Date;

@Service
public class S3PresignedUrlService {

    private final AmazonS3 s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public S3PresignedUrlService(
            @Value("${aws.s3.access-key}") String accessKey,
            @Value("${aws.s3.secret-key}") String secretKey,
            @Value("${aws.s3.region}") String region
    ) {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        this.s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }

    public String generatePresignedUrl(String objectKey, int expirationInMinutes) {
        Date expiration = new Date();
        expiration.setTime(System.currentTimeMillis() + expirationInMinutes * 60 * 1000);

        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, objectKey)
                .withMethod(HttpMethod.GET)
                .withExpiration(expiration);

        URL url = s3Client.generatePresignedUrl(request);
        return url.toString();
    }
}