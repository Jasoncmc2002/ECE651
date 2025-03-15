package com.yw.backend.service.problemSet;

import java.util.List;
import java.util.Map;

public interface ProblemSetTeacherService {

    List<Map<String, String>> getAllProgrammingRecord(int problemSetId);

    Map<String, String> getOneStudentRecord(int problemSetId, int studentId);

    List<Map<String, String>> getOneStudentAllObjectiveProblem(int problemSetId, int studentId);
}
