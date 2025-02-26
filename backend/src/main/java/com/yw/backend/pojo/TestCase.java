package com.yw.backend.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestCase {
    @TableId(type = IdType.AUTO)
    private Integer testCaseId;
    private Integer programmingId;
    private String tcInput;
    private String tcOutput;
}
