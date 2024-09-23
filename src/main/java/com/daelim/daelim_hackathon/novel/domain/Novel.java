package com.daelim.daelim_hackathon.novel.domain;

import com.daelim.daelim_hackathon.author.domain.User;
import com.daelim.daelim_hackathon.common.domain.BaseTimeEntity;
import com.daelim.daelim_hackathon.common.domain.BooleanToYNConverter;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Novel extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column
    private Long love;

    @Column(length = 20, nullable = false)
    private String genre;

    @Column
    @Convert(converter = BooleanToYNConverter.class)
    private boolean isPublic;

    @ManyToOne(fetch = FetchType.LAZY)
    private User author;

    public void changeTitle(String title) {
        this.title = title;
    }

    public void changeIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public void changeLove(Long result) {
        this.love = result;
    }
}
