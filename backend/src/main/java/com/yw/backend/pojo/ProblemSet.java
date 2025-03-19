package com.yw.backend.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProblemSet {
    @TableId(type = IdType.AUTO)
    private Integer problemSetId;
    private String psName;
    private Integer psAuthorId;
    private LocalDateTime psStartTime;
    private LocalDateTime psEndTime;
    private Integer duration;
}
