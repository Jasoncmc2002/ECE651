package com.yw.backend.service.judge;

import java.util.Map;

public interface SpecialJudgeService {
    Map<String, String> specialJudge(String code, String testInput, int timeLimit);
}
