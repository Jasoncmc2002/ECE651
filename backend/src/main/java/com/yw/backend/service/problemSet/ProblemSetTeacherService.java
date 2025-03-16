package com.yw.backend.service.problemSet;

import java.util.List;
import java.util.Map;

public interface ProblemSetTeacherService {
    Map<String, String> getOneProblemSetInfo(int problemSetId);

    List<Map<String, String>> getAllStudentRecord(int problemSetId);

    List<Map<String, String>> getAllObjectiveProblemRecord(int problemSetId);

}
