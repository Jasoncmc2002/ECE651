package com.yw.backend.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgrammingAnswer {
    @TableId(type = IdType.AUTO)
    private Integer programmingAnswerId;
    private Integer authorId;
    private Integer problemSetId;
    private Integer programmingId;
    private String paCode;
    private Integer paActualScore;
    private Integer passCount;
}
