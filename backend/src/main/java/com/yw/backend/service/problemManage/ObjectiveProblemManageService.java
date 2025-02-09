package com.yw.backend.service.problemManage;

import java.util.List;
import java.util.Map;

public interface ObjectiveProblemManageService {
    List<Map<String, String>> getAll();

    Map<String, String> getOne(int objectiveProblemId);
    Map<String, String> update(int objectiveProblemId, String opDescription, int opTotalScore, String opCorrectAnswer, String opTag, int opDifficulty);


}
