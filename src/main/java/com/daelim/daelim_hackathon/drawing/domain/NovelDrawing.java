package com.daelim.daelim_hackathon.drawing.domain;

import com.daelim.daelim_hackathon.novel.domain.Novel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NovelDrawing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000)
    private String uuid;

    @Column(length = 100)
    private String file_path;

    @Column(length = 100)
    private String file_name;

    @Column(length = 100)
    private String file_extension;

    @Column(name = "create_date")
    private Date newDate;

    @OneToOne
    private Novel novel;
}
