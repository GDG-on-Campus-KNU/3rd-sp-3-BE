package gdsc.comunity.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import gdsc.comunity.exception.CustomException;
import gdsc.comunity.exception.ErrorCode;
import java.io.File;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class S3FileService {
    private final AmazonS3 s3Client;

    private final String bucketName = "mohaji";

    public S3FileService(@Value("${aws.access-key}") String accessKeyId,
        @Value("${aws.secret-key}") String secretKey) {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKeyId, secretKey);
        this.s3Client = AmazonS3ClientBuilder.standard()
            .withRegion("ap-northeast-2")
            .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
            .build();
    }

    public String uploadImage(MultipartFile image){
        String s3FileName = UUID.randomUUID().toString().substring(0, 10) + "_" + image.getOriginalFilename();
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(image.getContentType());
            metadata.setContentLength(image.getSize());
            s3Client.putObject(new PutObjectRequest(bucketName, s3FileName, image.getInputStream(), metadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (Exception e) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_ERROR);
        }

        return s3Client.getUrl(bucketName, s3FileName).toString();
    }

    public String uploadFile(MultipartFile file) {
        String s3FileName = UUID.randomUUID().toString().substring(0, 15);
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());
            s3Client.putObject(new PutObjectRequest(bucketName, s3FileName, file.getInputStream(), metadata));
        } catch (Exception e) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_ERROR);
        }
        return s3FileName;
    }

    public File downloadFile(String s3FileName) {
        try {
            File file = File.createTempFile(s3FileName, null);
            s3Client.getObject(new GetObjectRequest(bucketName, s3FileName), file);
            return file;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.FILE_DOWNLOAD_ERROR);
        }
    }
}
