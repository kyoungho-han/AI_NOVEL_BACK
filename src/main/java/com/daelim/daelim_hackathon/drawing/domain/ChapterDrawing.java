package com.daelim.daelim_hackathon.drawing.domain;

import com.daelim.daelim_hackathon.chapter.domain.Chapter;
import com.daelim.daelim_hackathon.novel.domain.Novel;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

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

    @Column(length = 100)
    private String file_path;

    @Column(length = 100)
    private String file_name;

    @Column(length = 100)
    private String file_extension;

    @Column(name = "create_date")
    private Date newDate;

    @ManyToOne
    private Novel novel;

}
