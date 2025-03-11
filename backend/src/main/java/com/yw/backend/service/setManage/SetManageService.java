package com.yw.backend.service.setManage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface SetManageService {
    Map<String, String> create(String psName, LocalDateTime psStartTime, LocalDateTime psEndTime, int duration);

    Map<String, String> delete(int problemSetId);

    List<Map<String, String>> searchObjectiveProblem(int problemSetId, String opDescription, String opTag, int opDifficultyMin, int opDifficultyMax);

    Map<String, String> addObjectiveProblem(int problemSetId, int objectiveProblemId);

    Map<String, String> deleteObjectiveProblem(int problemSetId, int objectiveProblemId);

    List<Map<String, String>> getAddedObjectiveProblem(int problemSetId);

    Map<String, String> update(int problemSetId, String psName, LocalDateTime psStartTime, LocalDateTime psEndTime, int duration);

    List<Map<String, String>> searchProgramming(int problemSetId, String pTitle, String pTag, int pDifficultyMin, int pDifficultyMax);

    Map<String, String> addProgramming(int problemSetId, int programmingId);

    Map<String, String> deleteProgramming(int problemSetId, int programmingId);

    List<Map<String, String>> getAddedProgramming(int problemSetId);

    Map<String, String> getOne(int problemSetId);

    List<Map<String, String>> getAssignmentList();

    List<Map<String, String>> getExamList();

    List<Map<String, String>> searchStudent(int problemSetId, String username, String name);

    Map<String, String> addStudent(int problemSetId, int userId);

    Map<String, String> deleteStudent(int problemSetId, int userId);

    List<Map<String, String>> getAddedStudent(int problemSetId);
}
