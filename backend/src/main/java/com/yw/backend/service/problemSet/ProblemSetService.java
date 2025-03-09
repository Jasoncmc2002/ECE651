package com.yw.backend.service.problemSet;

import java.util.List;
import java.util.Map;

public interface ProblemSetService {

    List<Map<String, String>> getAllObjectiveProblem(int problemSetId);

    List<Map<String, String>> getAllProgramming(int problemSetId);

    Map<String, String> getOneObjectiveProblem(int problemSetId, int objectiveProblemId);

    Map<String, String> submitObjectiveProblemAnswer(int problemSetId, int objectiveProblemId, String opaActualAnswer);
}
