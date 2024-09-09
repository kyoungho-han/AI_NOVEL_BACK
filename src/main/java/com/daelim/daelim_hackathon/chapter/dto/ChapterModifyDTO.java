package com.daelim.daelim_hackathon.chapter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterModifyDTO {
    private String chapterName;
    private String writing;
}
