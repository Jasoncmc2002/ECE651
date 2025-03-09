package com.yw.backend.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentNPs {
    private Integer studentId;  // userId in user table
    private Integer problemSetId;
    private LocalDateTime firstStartTime;
}
