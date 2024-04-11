package com.ecommerce.site.admin.constant;


public class AmazonS3Constant {

    public static final String S3_BASE_URI;

    public static final boolean S3_SERVICE_ACTIVE = false;

    static {
        String bucketName = System.getenv("AWS_BUCKET_NAME");
        String region = System.getenv("AWS_REGION");
        String pattern = "https://%s.s3.%s.amazonaws.com";

        S3_BASE_URI = bucketName == null ? "" : String.format(pattern, bucketName, region);
    }
}
