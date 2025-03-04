package com.yw.backend.service.setManage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface SetManageService {
    Map<String, String> create(String psName, LocalDateTime psStartTime, LocalDateTime psEndTime, int duration);

    Map<String, String> delete(int problemSetId);

    List<Map<String, String>> searchObjectiveProblem(int problemSetId, String opDescription, String opTag, int opDifficultyMin, int opDifficultyMax);

    Map<String, String> addObjectiveProblem(int problemSetId, int objectiveProblemId);

    Map<String, String> deleteObjectiveProblem(int problemSetId, int objectiveProblemId);

    List<Map<String, String>> getAddedObjectiveProblem(int problemSetId);
}
