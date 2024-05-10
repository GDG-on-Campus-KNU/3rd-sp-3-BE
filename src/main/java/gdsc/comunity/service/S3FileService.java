package gdsc.comunity.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import gdsc.comunity.exception.CustomException;
import gdsc.comunity.exception.ErrorCode;
import java.io.File;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class S3FileService {
    private final AmazonS3 s3Client;

    private final String bucketName = "mohaji";

    public S3FileService() {
        this.s3Client = AmazonS3ClientBuilder.defaultClient();
    }

    public String uploadImage(MultipartFile image){
        String s3FileName = UUID.randomUUID().toString().substring(0, 10) + "_" + image.getOriginalFilename();
        try {
            File convertedFile = convertMultiPartFileToFile(image);
            s3Client.putObject(new PutObjectRequest(bucketName, s3FileName, convertedFile)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (Exception e) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_ERROR);
        }

        return s3Client.getUrl(bucketName, s3FileName).toString();
    }

    public String uploadFile(MultipartFile file) {
        String s3FileName = UUID.randomUUID().toString().substring(0, 10) + "_" + file.getOriginalFilename();
        try {
            File convertedFile = convertMultiPartFileToFile(file);
            s3Client.putObject(new PutObjectRequest(bucketName, s3FileName, convertedFile));
        } catch (Exception e) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_ERROR);
        }
        return s3FileName;
    }

    public File downloadFile(String s3FileName) {
        try {
            File file = File.createTempFile(s3FileName.substring(11), null);
            s3Client.getObject(new GetObjectRequest(bucketName, s3FileName), file);
            return file;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.FILE_DOWNLOAD_ERROR);
        }
    }

    private File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try {
            file.transferTo(convertedFile);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.FILE_TRANSFORM_ERROR);
        }
        return convertedFile;
    }

}
