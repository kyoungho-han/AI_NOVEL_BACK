package com.daelim.daelim_hackathon.chapter.resource;

import com.daelim.daelim_hackathon.chapter.dto.ChapterDrawingDTO;
import com.daelim.daelim_hackathon.chapter.dto.ChapterModifyDTO;
import com.daelim.daelim_hackathon.chapter.dto.ChapterDTO;
import com.daelim.daelim_hackathon.chapter.dto.ChapterPageRequestDTO;
import com.daelim.daelim_hackathon.chapter.exception.LastChapterException;
import com.daelim.daelim_hackathon.chapter.service.ChapterService;
import com.daelim.daelim_hackathon.common.dto.StatusDTO;
import com.daelim.daelim_hackathon.drawing.domain.ChapterDrawing;
import com.daelim.daelim_hackathon.drawing.dto.FileNameDTO;
import com.daelim.daelim_hackathon.drawing.dto.StringDTO;
import com.daelim.daelim_hackathon.drawing.service.AwsS3Service;
import com.daelim.daelim_hackathon.drawing.service.PapagoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/chapters")
public class ChapterController {
    private final ChapterService chapterService;
    private final AwsS3Service awsS3Service;
    private final PapagoService papagoService;

    @Value("${UPLOAD_CHAPTER_DIR}")
    private String uploadDir;

    @GetMapping()
    public ResponseEntity getList(@ModelAttribute ChapterPageRequestDTO pageRequestDTO) {
        try {
            return new ResponseEntity<>(chapterService.getChapters(pageRequestDTO), HttpStatus.OK);
        }catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity create(@RequestBody ChapterDTO dto) {
        try {
            return new ResponseEntity<>(chapterService.saveChapter(dto), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity read(@PathVariable(value = "id") String id) {
        try {
            return new ResponseEntity<>(chapterService.getChapter(Long.parseLong(id)), HttpStatus.OK);
        }catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/{id}/next")
    public ResponseEntity next(@PathVariable(value = "id") String id) {
        try {
            return new ResponseEntity<>(chapterService.getNextChapter(Long.parseLong(id)), HttpStatus.OK);
        }catch(LastChapterException e) {
            return new ResponseEntity<>(StatusDTO.builder().status("last page").build(), HttpStatus.OK);
        }catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value= "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity modify(@PathVariable(value = "id") String id, @RequestBody ChapterModifyDTO dto) {
        try {
            return new ResponseEntity<>(chapterService.updateChapter(Long.parseLong(id), dto), HttpStatus.OK);
        }catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity delete(@PathVariable(value = "id") String id) {
        try {
            return new ResponseEntity<>(chapterService.deleteChapter(Long.parseLong(id)), HttpStatus.OK);
        }catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/upload/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity uploadUrl(@RequestParam("file") MultipartFile file,
                                        @RequestParam("uuid") String uuid,
                                        @RequestParam("novelId") String novelId,
                                        @PathVariable("id") String id) {
        try {
            Long chapterId = Long.parseLong(id);

            // 파일 이름을 "chapter_<chapterId>.png" 형식으로 설정
            String fileName = "chapter_" + chapterId + ".png";
            ChapterDrawingDTO dto = new ChapterDrawingDTO();
            dto.setChapterId(chapterId);
            dto.setCreateDate(new Date());
            dto.setFileName(fileName);
            dto.setUuid(uuid);

            // uploadDir에 노벨 ID를 붙여 경로 설정
            Path path = Paths.get(uploadDir + "/novel_" + novelId); // 예시: 노벨 UUID를 사용하여 경로 생성
            Files.createDirectories(path);

            // 파일 경로 설정
            dto.setFilePath(path + "/" + fileName);
            log.info(dto.getFilePath());

            // 파일을 지정된 경로로 저장
            file.transferTo(new File(dto.getFilePath()));
            return new ResponseEntity<>(HttpStatus.OK);
        } catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping(value = "/download/{novelId}/{chapterId}")
    public ResponseEntity<Resource> getURL(@PathVariable("novelId") String novelId,
                                           @PathVariable("chapterId") String chapterId) {
        try {
            // 노벨 ID에 해당하는 폴더 경로 설정
            Path directoryPath = Paths.get(uploadDir + "/novel_" + novelId);

            // 폴더 내에서 chapterId를 포함한 파일을 검색 (파일명 형식: chapter_<chapterId>.png)
            Path filePath = directoryPath.resolve("chapter_" + chapterId + ".png");

            // 파일이 존재하는지 확인
            if (!Files.exists(filePath)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // 파일을 읽어 Resource로 변환
            Resource resource = new UrlResource(filePath.toUri());

            // 정상적으로 Resource를 반환
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping(value = "/updateFile/{id}")
    public ResponseEntity updateURL(@RequestParam("file") MultipartFile file,
                                    @RequestParam("uuid") String uuid,
                                    @RequestParam("novelId") String novelId,
                                    @PathVariable("id") String id) {
        try {
            Long chapterId = Long.parseLong(id);

            // 파일 이름을 "chapter_<chapterId>.png" 형식으로 설정
            String fileName = "chapter_" + chapterId + ".png";
            ChapterDrawingDTO dto = new ChapterDrawingDTO();
            dto.setChapterId(chapterId);
            dto.setCreateDate(new Date());
            dto.setFileName(fileName);
            dto.setUuid(uuid);

            // uploadDir에 노벨 ID를 붙여 경로 설정
            Path path = Paths.get(uploadDir + "/novel_" + novelId); // 예시: 노벨 UUID를 사용하여 경로 생성
            Files.createDirectories(path);

            // 파일 경로 설정
            dto.setFilePath(path + "/" + fileName);
            log.info(dto.getFilePath());

            // 파일을 지정된 경로로 저장
            file.transferTo(new File(dto.getFilePath()));
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/drawing/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity uploadFile(
            @RequestPart(value = "file", required = false) MultipartFile multipartFile,
            @PathVariable("id") String id
    ) {
        try {
            return new ResponseEntity<>(
                    FileNameDTO.builder().fileName(awsS3Service.saveChapterDrawing(
                            Long.parseLong(id),
                            multipartFile
                    )).build(),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/drawing/{id}")
    public ResponseEntity getFile(@PathVariable("id") String id) {
        try {
            return new ResponseEntity<>(
                    FileNameDTO.builder().fileName(chapterService.getFileName(
                            Long.parseLong(id)
                    )).build(),
                    HttpStatus.OK
            );
        } catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
