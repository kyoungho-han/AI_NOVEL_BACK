package com.daelim.daelim_hackathon.drawing.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.daelim.daelim_hackathon.chapter.domain.Chapter;
import com.daelim.daelim_hackathon.chapter.repo.ChapterRepository;
import com.daelim.daelim_hackathon.drawing.domain.NovelDrawing;
import com.daelim.daelim_hackathon.drawing.domain.ChapterDrawing;
import com.daelim.daelim_hackathon.drawing.repo.NovelDrawingRepository;
import com.daelim.daelim_hackathon.drawing.repo.ChapterDrawingRepository;
import com.daelim.daelim_hackathon.novel.domain.Novel;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class AwsS3Service {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;
    private final NovelDrawingRepository novelDrawingRepository;
    private final ChapterDrawingRepository chapterDrawingRepository;
    private final ChapterRepository chapterRepository;

    /**
     * Novel 전용 그림 테이블 등록
     *
     * @param id
     * @param multipartFile
     * @return fileName
     */
    public String saveNovelDrawing(Long id, MultipartFile multipartFile) {
        String fileName = uploadFile(multipartFile);
        novelDrawingRepository.save(
                NovelDrawing.builder()
                        .uuid(fileName)
                        .novel(
                                Novel.builder()
                                        .id(id)
                                        .build()
                        )
                        .build()
        );
        return fileName;
    }

    /**
     * Page 전용 그림 테이블 등록
     *
     * @param id
     * @param multipartFile
     * @return fileName
     */
    public String saveChapterDrawing(Long id, MultipartFile multipartFile) {
        String fileName = uploadFile(multipartFile);
        chapterDrawingRepository.save(
                ChapterDrawing.builder()
                        .uuid(fileName)
                        .chapter(
                                Chapter.builder()
                                        .id(id)
                                        .build()
                        )
                        .novel(
                                chapterRepository.findById(id).orElse(null).getNovel()
                        )
                        .build()
        );
        return fileName;
    }

    /**
     * 해당 multipartFile 을 S3 에 저장
     * 
     * @param multipartFile
     * @return fileName
     */
    public String uploadFile(MultipartFile multipartFile) {

        String fileName = createFileName(multipartFile.getOriginalFilename());
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());

        try(InputStream inputStream = multipartFile.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch(IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
        }

        return fileName;
    }

    public void deleteFile(String fileName) {
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
    }

    private String createFileName(String fileName) { // 먼저 파일 업로드 시, 파일명을 난수화하기 위해 random으로 돌립니다.
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    private String getFileExtension(String fileName) {
        try {
            // 확장자 구분 하는 곳
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일(" + fileName + ") 입니다.");
        }
    }
}
