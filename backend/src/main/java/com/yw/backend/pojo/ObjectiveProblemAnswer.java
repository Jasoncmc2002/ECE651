package com.yw.backend.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ObjectiveProblemAnswer {
    @TableId(type = IdType.AUTO)
    private Integer objectiveProblemAnswerId;
    private Integer authorId;
    private Integer objectiveProblemId;
    private Integer problemSetId;
    private Integer opaActualScore;
    private String opaActualAnswer;
}
