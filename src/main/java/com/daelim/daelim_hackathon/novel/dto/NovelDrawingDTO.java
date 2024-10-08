package com.daelim.daelim_hackathon.novel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NovelDrawingDTO {
    private Long novelId;
    private String uuid;
    private String filePath;
    private String fileName;
    private String fileExtension;
    private Date createDate;
}
