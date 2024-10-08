package com.daelim.daelim_hackathon.novel.service;

import com.daelim.daelim_hackathon.author.domain.User;
import com.daelim.daelim_hackathon.novel.dto.*;
import com.daelim.daelim_hackathon.common.dto.PageResultDTO;
import com.daelim.daelim_hackathon.novel.domain.Novel;
import com.daelim.daelim_hackathon.common.dto.StatusDTO;

public interface NovelService {
    NovelDTO saveNovel(NovelDTO novelDTO);
    NovelDTO getNovel(Long novelId);
    PageResultDTO<NovelDTO, Object[]> getNovels(NovelPageRequestDTO pageRequestDTO);
    PageResultDTO<NovelDTO, Object[]> searchNovels(SearchPageRequestDTO pageRequestDTO);
    StatusDTO deleteNovel(Long novelId, String username);
    StatusDTO updateNovel(Long novelId, NovelModifyDTO modifyDTO);
    StatusDTO love(Long novelId, String username);
    String getFileName(Long novelId);
    String deleteFile(Long novelId);
    void visible(Long novelId);


    String uploadURL(NovelDrawingDTO dto);
    String getURL(Long novelId);


    default Novel dtoToEntity(NovelDTO dto, User author) {
        Novel novel = Novel.builder()
                .title(dto.getTitle())
                .author(author)
                .love(0L)
                .isPublic(dto.isPublic())
                .genre(dto.getGenre())
                .build();
        return novel;
    }

    default NovelDTO entityToDTO(Novel novel, User author) {
        NovelDTO novelDTO = NovelDTO.builder()
                .novelId(novel.getId())
                .regDate(novel.getRegDate())
                .modDate(novel.getModDate())
                .love(novel.getLove())
                .isPublic(novel.isPublic())
                .genre(novel.getGenre())
                .title(novel.getTitle())
                .name(author.getName())
                .build();
        return novelDTO;
    }
}
