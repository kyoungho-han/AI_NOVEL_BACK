package com.daelim.daelim_hackathon.chapter.service;

import com.daelim.daelim_hackathon.chapter.domain.Chapter;
import com.daelim.daelim_hackathon.chapter.dto.*;
import com.daelim.daelim_hackathon.common.dto.PageResultDTO;
import com.daelim.daelim_hackathon.common.dto.StatusDTO;
import com.daelim.daelim_hackathon.novel.domain.Novel;

import java.util.List;

public interface ChapterService {
    ChapterDTO saveChapter(ChapterDTO chapterDTO);
    ChapterDTO getChapter(Long chapterId);
    PageResultDTO<NoWritingDTO, Object[]> getChapters(ChapterPageRequestDTO pageRequestDTO);
    StatusDTO deleteChapter(Long chapterId);
    StatusDTO updateChapter(Long chapterId, ChapterModifyDTO modifyDTO);
    ChapterDTO getNextChapter(Long prevId);
    String getFileName(Long chapterId);
    void deleteDrawingsAndChapters(Long novelId);
    String deleteFile(Long chapterId);


    String uploadURL(ChapterDrawingDTO dto);
    String getURL(Long chapterId);
    String updateURL(ChapterDrawingDTO dto);


    default Chapter dtoToEntity(ChapterDTO dto) {

        return Chapter.builder()
                .chapterName(dto.getChapterName())
                .novel(Novel.builder().id(dto.getNovelId()).build())
                .writing(dto.getWriting())
                .prevChapter(dto.getPrevChapterId())
                .build();

    }

    default ChapterDTO entityToDTO(Chapter chapter, Novel novel) {

        return ChapterDTO.builder()
                .chapterId(chapter.getId())
                .chapterName(chapter.getChapterName())
                .prevChapterId(chapter.getPrevChapter())
                .writing(chapter.getWriting())
                .novelId(novel.getId())
                .modDate(chapter.getModDate())
                .regDate(chapter.getRegDate())
                .build();
    }

    default NoWritingDTO entityToListDTO(Chapter chapter, Novel novel) {

        return NoWritingDTO.builder()
                .chapterId(chapter.getId())
                .chapterName(chapter.getChapterName())
                .prevChapterId(chapter.getPrevChapter())
                .novelId(novel.getId())
                .modDate(chapter.getModDate())
                .regDate(chapter.getRegDate())
                .build();
    }
}
