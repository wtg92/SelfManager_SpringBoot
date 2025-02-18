package manager.service;
import manager.entity.general.FileRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.signer.Presigner;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
//import software.amazon.awssdk.services.s3.model.GeneratePresignedUrlRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.auth.StsAssumeRoleCredentialsProvider;

import java.net.URL;
import java.time.Duration;
import java.util.UUID;

@Component
public class S3Service {

    @Value("${s3.aws_access_key_id}")
    private String accessKeyID;
    @Value("${s3.aws_secret_access_key}")
    private String accessKey;

    @Value("${s3.url_duration_seconds}")
    private Integer urlDurationSeconds;

    public static final String BUCKET_NAME = "self-japan";

    final Region bucketRegion = Region.AP_NORTHEAST_3;

    private StaticCredentialsProvider getCredentials(){
        return StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKeyID, accessKey));
    }

    private S3Presigner getS3Presigner(){
        return S3Presigner.builder()
                .region(bucketRegion)
                .credentialsProvider(getCredentials())
                .build();
    }

    private S3Client getS3Client(){
        // 创建 S3Client 实例
        return S3Client.builder()
                .region(bucketRegion) // 设置区域
                .credentialsProvider(getCredentials()) // 设置凭证
                .build();
    }

    public String generateUploadURL(String fileName) {
        try (S3Presigner presigner = getS3Presigner()) {
            PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(
                    PutObjectPresignRequest.builder()
                            .signatureDuration(Duration.ofSeconds(urlDurationSeconds))
                            .putObjectRequest(PutObjectRequest.builder()
                                    .bucket(BUCKET_NAME)
                                    .key(fileName)
                                    .build())
                            .build());

            URL presignedUrl = presignedRequest.url();
            return presignedUrl.toString();
        }
    }

    public String generateGetURL(FileRecord fileRecord) {
        try (S3Presigner presigner = getS3Presigner()) {
            PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(
                    GetObjectPresignRequest.builder()
                            .signatureDuration(Duration.ofSeconds(urlDurationSeconds))
                            .getObjectRequest(GetObjectRequest.builder()
                                    .bucket(fileRecord.getBucketName())
                                    .key(fileRecord.getFileName())
                                    .build())
                            .build());

            URL presignedUrl = presignedRequest.url();
            return presignedUrl.toString();
        }
    }

    public void deleteObject(FileRecord fileRecord) {
        try (S3Client s3Client = getS3Client()) {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(fileRecord.getBucketName())
                    .key(fileRecord.getFileName())
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
        }
    }
}
