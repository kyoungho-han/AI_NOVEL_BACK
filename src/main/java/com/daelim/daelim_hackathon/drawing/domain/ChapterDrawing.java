package com.daelim.daelim_hackathon.drawing.domain;

import com.daelim.daelim_hackathon.chapter.domain.Chapter;
import com.daelim.daelim_hackathon.novel.domain.Novel;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChapterDrawing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String uuid;

    @ManyToOne
    private Chapter chapter;

    @ManyToOne
    private Novel novel;

}
