package com.daelim.daelim_hackathon.chapter.domain;

import com.daelim.daelim_hackathon.common.domain.BaseTimeEntity;
import com.daelim.daelim_hackathon.drawing.domain.ChapterDrawing;
import com.daelim.daelim_hackathon.novel.domain.Novel;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Chapter extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String chapterName;

    @Column(unique = true)
    private Long prevChapter;

    @Column(length = 5000)
    private String writing;

    @ManyToOne(fetch = FetchType.LAZY)
    private Novel novel;

    public void changeChapterName(String chapterName) {
        this.chapterName = chapterName;
    }
    public void changeWriting(String writing) {
        this.writing = writing;
    }

    public void changePrevChapter(Long prevChapter) {
        this.prevChapter = prevChapter;
    }
}
