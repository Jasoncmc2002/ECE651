package com.yw.backend.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ObjectiveProblem {
    @TableId(type = IdType.AUTO)
    private Integer objectiveProblemId;
    private Integer authorId;
    private String opDescription;
    private Integer opTotalScore;
    private String opCorrectAnswer;
    private String opTag;
    private Integer opDifficulty;
}
