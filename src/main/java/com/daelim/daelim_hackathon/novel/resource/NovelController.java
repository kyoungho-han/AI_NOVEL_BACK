package com.daelim.daelim_hackathon.novel.resource;

import com.daelim.daelim_hackathon.chapter.service.ChapterService;
import com.daelim.daelim_hackathon.common.dto.NameDTO;
import com.daelim.daelim_hackathon.drawing.dto.FileNameDTO;
import com.daelim.daelim_hackathon.drawing.dto.StringDTO;
import com.daelim.daelim_hackathon.drawing.service.AwsS3Service;
import com.daelim.daelim_hackathon.drawing.service.PapagoService;
import com.daelim.daelim_hackathon.novel.dto.*;
import com.daelim.daelim_hackathon.novel.service.NovelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/novels")
public class NovelController {
    private final NovelService novelService;
    private final AwsS3Service awsS3Service;
    private final PapagoService papagoService;
    private final ChapterService chapterService;

    @Value("UPLOAD_DIR")
    private String uploadDir;

    /**
     * 한글 영어로 번역
     * 
     * @param stringDTO
     * @return stringDTO(string = 영어로 번역된 문자열)
     */
    @PostMapping(value = "/translate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getEn(@RequestBody StringDTO stringDTO) {
        try {
            return ResponseEntity.ok().body(papagoService.koToEn(stringDTO.getString()));
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 내 소설 리스트 혹은, 내가 좋아요 누른 소설 리스트 불러오기
     *
     * @param pageRequestDTO
     * @return pageResultDTO
     */
    @GetMapping()
    public ResponseEntity getList(@ModelAttribute NovelPageRequestDTO pageRequestDTO) {
        try {
            return new ResponseEntity<>(novelService.getNovels(pageRequestDTO), HttpStatus.OK);
        }catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 내 소설 리스트 혹은, 내가 좋아요 누른 소설 리스트 불러오기
     *
     * @param pageRequestDTO
     * @return pageResultDTO
     */
    @GetMapping(value = "/search")
    public ResponseEntity searchList(@ModelAttribute SearchPageRequestDTO pageRequestDTO) {
        try {
            log.info(pageRequestDTO.getKeyword());
            return new ResponseEntity<>(novelService.searchNovels(pageRequestDTO), HttpStatus.OK);
        }catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 소설 생성하기
     *
     * @param dto
     * @return StatusDTO
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity create(@RequestBody NovelDTO dto) {
        try {
            return new ResponseEntity<>(novelService.saveNovel(dto), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 소설 불러오기
     *
     * @param id
     * @return novelDTO
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity read(@PathVariable(value = "id") String id) {
        try {
            return new ResponseEntity<>(novelService.getNovel(Long.parseLong(id)), HttpStatus.OK);
        }catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 소설 제목 수정하기
     *
     * @param id
     * @param dto
     * @return statusDTO
     */
    @PutMapping(value= "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity modify(@PathVariable(value = "id") String id, @RequestBody NovelModifyDTO dto) {
        try {
            return new ResponseEntity<>(novelService.updateNovel(Long.parseLong(id), dto), HttpStatus.OK);
        }catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 소설 삭제하기
     *
     * @param id
     * @param nameDTO
     * @return statusDTO
     */
    @DeleteMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity delete(@PathVariable(value = "id") String id, @RequestBody NameDTO nameDTO) {
        try {
            awsS3Service.deleteFile(novelService.deleteFile(Long.parseLong(id)));
            chapterService.deleteDrawingsAndChapters(Long.parseLong(id));
            return new ResponseEntity<>(novelService.deleteNovel(Long.parseLong(id), nameDTO.getName()), HttpStatus.OK);
        }catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 좋아요 누르기
     * 
     * @param id
     * @param nameDTO
     * @return StatusDTO
     */
    @PutMapping(value = "/love/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addLike(@PathVariable(value = "id") String id, @RequestBody NameDTO nameDTO) {
        try {
            return new ResponseEntity<>(novelService.love(Long.parseLong(id), nameDTO.getName()), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 그림 저장
     * 
     * 책 표지와 페이지 사이의 그림 table 을 구분 지어서 관리
     * NovelController 에서 s3 service 호출
     * @return 파일 경로에 해당하는 uuid 반환
     */
    @PostMapping(value = "/drawing/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity uploadFile(
            @RequestPart(value = "file", required = false) MultipartFile multipartFile,
            @PathVariable("id") String id
    ) {
        try {
            novelService.visible(Long.parseLong(id));
            return new ResponseEntity<>(
                    FileNameDTO.builder().fileName(awsS3Service.saveNovelDrawing(
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

    /**
     * 그림 불러오기
     *
     * @param id
     * @return filenameDTO
     */
    @GetMapping(value = "/drawing/{id}")
    public ResponseEntity getFile(@PathVariable("id") String id) {
        try {
            return new ResponseEntity<>(
                    FileNameDTO.builder().fileName(novelService.getFileName(
                            Long.parseLong(id)
                    )).build(),
                    HttpStatus.OK
            );
        } catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/upload/{id}")
    public ResponseEntity uploadURL(@RequestParam("file") MultipartFile file,
                                 @PathVariable("id") String id) {
        try {
            novelService.visible(Long.parseLong(id));
            String fileName = "novel_" + id + ".png";
            NovelDrawingDTO dto = new NovelDrawingDTO();
            dto.setCreateDate(new Date());
            dto.setFileName(fileName);
            dto.setFilePath(uploadDir + id + "/" + fileName);
            return new ResponseEntity<>(
                    StringDTO.builder().string(
                            novelService.uploadURL(dto)),
                    HttpStatus.OK
            );
        } catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/download/{id}")
    public ResponseEntity getURL(@PathVariable("id") String id) {
        try {
            return new ResponseEntity<>(
                    StringDTO.builder().string(
                                    novelService.getURL(
                                            Long.parseLong(id)))
                            .build(),
                    HttpStatus.OK
            );
        } catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
