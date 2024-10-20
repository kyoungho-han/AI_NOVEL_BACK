package com.daelim.daelim_hackathon.chapter.service;

import com.daelim.daelim_hackathon.chapter.domain.Chapter;
import com.daelim.daelim_hackathon.chapter.dto.*;
import com.daelim.daelim_hackathon.chapter.exception.ChapterException;
import com.daelim.daelim_hackathon.chapter.exception.LastChapterException;
import com.daelim.daelim_hackathon.chapter.repo.ChapterRepository;
import com.daelim.daelim_hackathon.common.dto.PageResultDTO;
import com.daelim.daelim_hackathon.common.dto.StatusDTO;
import com.daelim.daelim_hackathon.drawing.domain.ChapterDrawing;
import com.daelim.daelim_hackathon.drawing.domain.NovelDrawing;
import com.daelim.daelim_hackathon.drawing.repo.ChapterDrawingMapping;
import com.daelim.daelim_hackathon.drawing.repo.ChapterDrawingRepository;
import com.daelim.daelim_hackathon.drawing.service.AwsS3Service;
import com.daelim.daelim_hackathon.novel.domain.Novel;
import com.daelim.daelim_hackathon.novel.repo.NovelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Log4j2
@Service
@RequiredArgsConstructor
public class ChapterServiceImpl implements ChapterService{

    private final ChapterRepository chapterRepository;
    private final NovelRepository novelRepository;
    private final ChapterDrawingRepository chapterDrawingRepository;
    private final AwsS3Service awsS3Service;

    @Override
    public ChapterDTO saveChapter(ChapterDTO chapterDTO) {
        try {
            Novel novel = novelRepository.findById(chapterDTO.getNovelId()).get();
            return entityToDTO(chapterRepository.save(dtoToEntity(chapterDTO)), novel);
        } catch(Exception e) {
            throw e;
        }
    }

    @Override
    public ChapterDTO getChapter(Long chapterId) {
        Optional<Chapter> optional = chapterRepository.findById(chapterId);
        Chapter chapter;
        if (optional.isPresent()) {
            chapter = optional.get();
            return entityToDTO(optional.get(), chapter.getNovel());
        } else {
            throw new ChapterException("chapter isn't exist");
        }
    }

    @Override
    public ChapterDTO getNextChapter(Long prevId) {
        Optional<Chapter> optional = chapterRepository.findByPrevChapter(prevId);
        Chapter chapter;
        if (optional.isPresent()) {
            chapter = optional.get();
            return entityToDTO(chapter, chapter.getNovel());
        } else {
            throw new LastChapterException("next chapter isn't exist");
        }
    }

    @Override
    public PageResultDTO<NoWritingDTO, Object[]> getChapters(ChapterPageRequestDTO pageRequestDTO) {
        Function<Object[], NoWritingDTO> fn = (
                entity -> entityToListDTO(
                        (Chapter) entity[0],
                        (Novel) entity[1]
                )
        );
        Page<Object[]> result = chapterRepository.getChaptersByNovel_Id(
                pageRequestDTO.getPageable(Sort.by("id").ascending()),
                Long.parseLong(pageRequestDTO.getNovelId())
        );

        return new PageResultDTO<>(result, fn);
    }

    @Transactional
    @Override
    public StatusDTO deleteChapter(Long chapterId) {

        // 다음 챕터가 있다면 해당 챕터의 이전챕터와 이어줘야함
        Optional<Chapter> nextOptional = chapterRepository.findByPrevChapter(chapterId);
        if(nextOptional.isPresent()) {
            Chapter next = nextOptional.get();
            Long prev = chapterRepository.getReferenceById(chapterId).getPrevChapter();
            awsS3Service.deleteFile(deleteFile(chapterId));
            chapterRepository.deleteById(chapterId);
            next.changePrevChapter(prev);
            chapterRepository.save(next);
        } else {
            awsS3Service.deleteFile(deleteFile(chapterId));
            chapterRepository.deleteById(chapterId);
        }
        return StatusDTO.builder().status("success").build();
    }

    @Override
    public StatusDTO updateChapter(Long chapterId, ChapterModifyDTO modifyDTO) {
        Optional<Chapter> optional = chapterRepository.findById(chapterId);
        if(optional.isPresent()) {
            Chapter chapter = optional.get();
            chapter.changeChapterName(modifyDTO.getChapterName());
            chapter.changeWriting(modifyDTO.getWriting());
            chapterRepository.save(chapter);
            return StatusDTO.builder().status("success").build();
        } else {
            throw new ChapterException("해당 id 에 속하는 챕터가 없음");
        }
    }


    @Override
    public String getFileName(Long chapterId) {
        return chapterDrawingRepository.findByChapter_Id(chapterId).getUuid();
    }


    @Override
    public void deleteDrawingsAndChapters(Long novelId) {
        List<ChapterDrawingMapping> list = chapterDrawingRepository.findAllByNovel_Id(novelId);
        chapterDrawingRepository.deleteChapterDrawingByNovel_Id(novelId);
        chapterRepository.deleteAllByNovel_Id(novelId);
        for(int i = 0; i<list.size(); i++) {
            awsS3Service.deleteFile(list.get(i).getUuid());
        }
    }

    @Override
    public String deleteFile(Long chapterId) {
        String uuid = chapterDrawingRepository.findByChapter_Id(chapterId).getUuid();
        chapterDrawingRepository.deleteChapterDrawingByChapter_Id(chapterId);
        return uuid;
    }


    @Override
    public String uploadURL(ChapterDrawingDTO dto) {
        chapterDrawingRepository.save(ChapterDrawing.builder()
                .chapter(chapterRepository.getReferenceById(dto.getChapterId()))
                .uuid(dto.getUuid())
                .file_extension(dto.getFileExtension())
                .file_name(dto.getFileName())
                .file_path(dto.getFilePath())
                .build()
        );
        return dto.getFilePath();
    }

    @Override
    @Transactional
    public String updateURL(ChapterDrawingDTO dto) {
        ChapterDrawing existingChapterDrawing = chapterDrawingRepository.findByChapter_Id(dto.getChapterId());
        existingChapterDrawing.setUuid(dto.getUuid());
        chapterDrawingRepository.save(existingChapterDrawing);

        return dto.getUuid();
    }

    @Override
    public String getURL(Long chapterId) {
        return chapterDrawingRepository.findByChapter_Id(chapterId).getUuid();
    }
}
