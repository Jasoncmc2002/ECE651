package com.yw.backend.service.problemManage;

import java.util.List;
import java.util.Map;

public interface ProgrammingManageService {
    Map<String, String> update(int programmingId, String pDescription, int pTotalScore, int timeLimit, int codeSizeLimit, String pTag, String pTitle, String pJudgeCode, int pDifficulty);
    Map<String, String> create(String pDescription, int pTotalScore, int timeLimit, int codeSizeLimit, String pTag, String pTitle, String pJudgeCode, int pDifficulty);

    List<Map<String, String>> getAll();
    Map<String, String> delete(int programmingId);

    Map<String, String> getOne(int programmingId);
}
