package com.daelim.daelim_hackathon.chapter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterDrawingDTO {
    private Long chapterId;
    private String uuid;
    private String filePath;
    private String fileName;
    private String fileExtension;
    private Date createDate;
}
