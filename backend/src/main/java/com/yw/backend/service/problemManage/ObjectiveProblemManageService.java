package com.yw.backend.service.problemManage;

import java.util.Map;

public interface ObjectiveProblemManageService {
    Map<String, String> create(String opDescription, int opTotalScore, String opCorrectAnswer, String opTag, int opDifficulty);

    Map<String, String> delete(int objectiveProblemId);

}
