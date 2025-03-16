package com.yw.backend.service.problemSet;

import java.util.List;
import java.util.Map;

public interface ProblemSetTeacherService {

    List<Map<String, String>> getOneStudentAllProgramming(int problemSetId, int studentId);

    Map<String, String> getOneStudentOneObjectiveProblem(int problemSetId, int studentId, int objectiveProblemId);

    Map<String, String> getOneStudentOneProgramming(int problemSetId, int studentId, int programmingId);
}
