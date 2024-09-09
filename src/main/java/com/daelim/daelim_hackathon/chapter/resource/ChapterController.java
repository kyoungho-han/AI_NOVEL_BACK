package com.daelim.daelim_hackathon.chapter.resource;

import com.daelim.daelim_hackathon.chapter.dto.ChapterModifyDTO;
import com.daelim.daelim_hackathon.chapter.dto.ChapterDTO;
import com.daelim.daelim_hackathon.chapter.dto.ChapterPageRequestDTO;
import com.daelim.daelim_hackathon.chapter.exception.LastChapterException;
import com.daelim.daelim_hackathon.chapter.service.ChapterService;
import com.daelim.daelim_hackathon.common.dto.StatusDTO;
import com.daelim.daelim_hackathon.drawing.dto.FileNameDTO;
import com.daelim.daelim_hackathon.drawing.dto.StringDTO;
import com.daelim.daelim_hackathon.drawing.service.AwsS3Service;
import com.daelim.daelim_hackathon.drawing.service.PapagoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/chapters")
public class ChapterController {
    private final ChapterService chapterService;
    private final AwsS3Service awsS3Service;
    private final PapagoService papagoService;

    /*@PostMapping(value = "/translate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getEn(@RequestBody StringDTO stringDTO) {
        try {
            return ResponseEntity.ok().body(papagoService.koToEn(stringDTO.getString()));
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }*/

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

    @PostMapping(value = "/upload/{id}")
    public ResponseEntity uploadURL(@RequestBody StringDTO stringDTO,
                                    @PathVariable("id") String id) {
        try {
            return new ResponseEntity<>(
                    StringDTO.builder().string(
                                    chapterService.uploadURL(
                                            stringDTO.getString(),
                                            Long.parseLong(id)))
                            .build(),
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
                                    chapterService.getURL(
                                            Long.parseLong(id)))
                            .build(),
                    HttpStatus.OK
            );
        } catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "/updateFile/{id}")
    public ResponseEntity updateURL(@RequestBody StringDTO stringDTO,
                                               @PathVariable("id") String id) {
        try {
            return new ResponseEntity<>(
                    StringDTO.builder()
                            .string(chapterService.updateURL(
                                    stringDTO.getString(),
                                    Long.parseLong(id)))
                            .build(),
                    HttpStatus.OK
            );
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
