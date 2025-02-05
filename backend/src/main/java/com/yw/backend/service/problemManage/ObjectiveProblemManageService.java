package com.yw.backend.service.problemManage;

import java.util.List;
import java.util.Map;

public interface ObjectiveProblemManageService {

    Map<String, String> update(int objectiveProblemId, String opDescription, int opTotalScore, String opCorrectAnswer, String opTag, int opDifficulty);


}
