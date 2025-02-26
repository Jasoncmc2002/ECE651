package com.yw.backend.service.problemManage;

import java.util.List;
import java.util.Map;

public interface TestCaseManageService {
    Map<String, String> create(int programmingId, String tcInput, String tcOutput, boolean respId);

    Map<String, String> delete(int testCaseId);

    List<Map<String, String>> getByProgrammingId(int programmingId);
}
