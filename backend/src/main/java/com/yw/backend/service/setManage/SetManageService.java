package com.yw.backend.service.setManage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface SetManageService {

    Map<String, String> getOne(int problemSetId);

    List<Map<String, String>> getAssignmentList();

    List<Map<String, String>> getExamList();

    List<Map<String, String>> searchStudent(int problemSetId, String username, String name);

    Map<String, String> addStudent(int problemSetId, int userId);

    Map<String, String> deleteStudent(int problemSetId, int userId);

    List<Map<String, String>> getAddedStudent(int problemSetId);
}
