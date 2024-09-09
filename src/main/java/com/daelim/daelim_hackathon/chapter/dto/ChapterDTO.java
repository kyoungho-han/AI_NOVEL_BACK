package com.daelim.daelim_hackathon.chapter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterDTO {
    private Long chapterId;
    private String chapterName;
    private String writing;
    private Long prevChapterId;
    private Long novelId;
    private LocalDateTime regDate,modDate;
}
