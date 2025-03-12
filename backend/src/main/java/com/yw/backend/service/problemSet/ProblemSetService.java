package com.yw.backend.service.problemSet;

import java.util.List;
import java.util.Map;

public interface ProblemSetService {
    List<Map<String, String>> getAllProblemSet();
    Map<String, String> getOne(int problemSetId);
    Map<String, String> startProblemSet(int problemSetId);
    List<Map<String, String>> getActiveProblemSet();
    Map<String, String> getOneProgramming(int problemSetId, int programmingId);

    List<Map<String, String>> getAllObjectiveProblem(int problemSetId);

    List<Map<String, String>> getAllProgramming(int problemSetId);
    Map<String, String> submitProgramming(int problemSetId, int programmingId, String paCode);

    Map<String, String> getOneObjectiveProblem(int problemSetId, int objectiveProblemId);

    Map<String, String> submitObjectiveProblemAnswer(int problemSetId, int objectiveProblemId, String opaActualAnswer);
    Map<String, String> submitSpecialJudge(int problemSetId, int programmingId, String paCode, String testInput);
}
