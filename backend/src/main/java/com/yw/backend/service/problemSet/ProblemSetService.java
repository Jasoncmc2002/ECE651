package com.yw.backend.service.problemSet;

import java.util.List;
import java.util.Map;

public interface ProblemSetService {
    List<Map<String, String>> getActiveProblemSet();

    List<Map<String, String>> getAllProblemSet();

    Map<String, String> getOne(int problemSetId);

    Map<String, String> startProblemSet(int problemSetId);

}
