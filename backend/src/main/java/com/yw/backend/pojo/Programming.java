package com.yw.backend.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Programming {
    @TableId(type = IdType.AUTO)
    private Integer programmingId;
    private String pDescription;
    private Integer pTotalScore;
    private Integer timeLimit;
    private Integer codeSizeLimit;
    private String pTag;
    private Integer pAuthorId;
    private String pTitle;
    private String pJudgeCode;
    private Integer pDifficulty;
}
