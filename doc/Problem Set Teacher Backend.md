Problem Set Teacher Backend

[TOC]



# User Story Related

As a teacher, 

I want to access my problem set transcript,

so that I can view all student record, all problem record with student performance data, and one student answer. 

As a administrator, 

I want to access all problem set transcript,

so that I can view all student record, all problem record with student performance data, and one student answer. 

# Database Structure

For this sprint, we will need to use almost all the tables defined in the database design, as shown in the following physical design model.

![](Set%20Management%20Backend.assets/Database%20PDM.jpeg)

The only table that is not used in this sprint is `test_case` table. New tables used in this sprint are `objective_problem_answer`, `programming_answer`, `student_n_ps`, `op_n_ps`, `p_n_ps`. The explanation of all ten tables are listed as follows. Please refer to the definitions as needed.

## 1. `user`

| **Field**  | **Type**                  | **Constrain** | **Field Comments**                                           |
| ---------- | ------------------------- | ------------- | ------------------------------------------------------------ |
| user_id    | Integer                   | PK            | User entity primary key                                      |
| username   | Variable characters (100) | Not Null      | Username, usually a combination of numbers and/or letters    |
| name       | Variable characters (100) | Not Null      | User's real name                                             |
| password   | Variable characters (100) | Not Null      | Password, stored in ciphertext                               |
| permission | Integer                   | Not Null      | User permissions, 0 for students, 1 for teachers, 2 for administrators |
| photo      | Text                      |               | User profile data, stored in Base64 encoding, no more than 50KB |

## 2. `objective_problem`

| **Field**            | **Type**                    | **Constrain** | **Field Comments**                   |
| -------------------- | --------------------------- | ------------- | ------------------------------------ |
| objective_problem_id | Integer                     | PK            | Objective problem entity primary key |
| user_id              | Integer                     | FK            | Objective problem author ID          |
| op_description       | Variable characters (10000) | Not Null      | Objective problem description        |
| op_total_score       | Integer                     | Not Null      | Objective problem total score        |
| op_correct_answer    | Variable characters (1024)  | Not Null      | Objective problem correct answer     |
| op_tag               | Variable characters (100)   | Not Null      | Objective problem tag                |
| op_diffiulty         | Integer                     | Not Null      | Objective problem difficulty         |

## 3. `programming`

| **Field**       | **Type**                    | **Constrain** | **Field Comments**                                         |
| --------------- | --------------------------- | ------------- | ---------------------------------------------------------- |
| programming_id  | Integer                     | PK            | Programming problem entity primary key                     |
| user_id         | Integer                     | FK            | Programming problem author ID                              |
| p_title         | Variable characters (100)   | Not Null      | Programming problem title                                  |
| p_description   | Variable characters (10000) | Not Null      | Programming problem description                            |
| p_total_score   | Integer                     | Not Null      | Programming problem total score                            |
| time_limit      | Integer                     | Not Null      | Programming problem running time limit, unit: milliseconds |
| code_size_limit | Integer                     | Not Null      | Programming problem code size limit, unit: KB              |
| p_tag           | Variable characters (100)   | Not Null      | Programming problem tag                                    |
| p_difficulty    | Integer                     | Not Null      | Programming problem difficulty                             |
| p_judge_code    | Text                        |               | Code Completion Judge Code                                 |

## 4. `test_case`

| **Field**      | **Type**                   | **Constrain** | **Field Comments**                                    |
| -------------- | -------------------------- | ------------- | ----------------------------------------------------- |
| test_case_id   | Integer                    | PK            | Test case entity primary key                          |
| programming_id | Integer                    | FK            | The programming problem to which the test case belong |
| tc_input       | Variable characters (1024) | Not Null      | Test case input                                       |
| tc_output      | Variable characters (1024) | Not Null      | Test case expected output                             |

## 5. `problem_set`

| **Field**      | **Type**                  | **Constrain** | **Field Comments**                                           |
| -------------- | ------------------------- | ------------- | ------------------------------------------------------------ |
| problem_set_id | Integer                   | PK            | Problem set entity primary key                               |
| user_id        | Integer                   | FK            | Problem set author ID                                        |
| ps_name        | Variable characters (100) | Not Null      | Problem set title (or name)                                  |
| ps_start_time  | Datetime                  | Not Null      | Problem set start date and time                              |
| ps_end_time    | Datetime                  | Not Null      | Problem set end date and time                                |
| duration       | Integer                   | Not Null      | Exam duration time, unit: minutes; for assignments the value is 0 |

## 6. `objective_problem_answer`

| **Field**                   | **Type**                   | **Constrain** | **Field Comments**                                          |
| --------------------------- | -------------------------- | ------------- | ----------------------------------------------------------- |
| objective_problem_answer_id | Integer                    | PK            | Objective problem answer entity primary key                 |
| user_id                     | Integer                    | FK            | The ID of the student who made the answer                   |
| problem_set_id              | Integer                    | FK            | The ID of the problem set to which the answer belongs       |
| objective_problem_id        | Integer                    | FK            | The ID of the objective problem to which the answer belongs |
| opa_actual_score            | Integer                    | Not Null      | Actual score earned for this objective problem answer       |
| opa_actual_answer           | Variable characters (1024) | Not Null      | The student's answer                                        |

## 7. `programming_answer`

| **Field**             | **Type**                    | **Constrain** | **Field Comments**                                           |
| --------------------- | --------------------------- | ------------- | ------------------------------------------------------------ |
| programming_answer_id | Integer                     | PK            | Programming problem answer entity primary key                |
| user_id               | Integer                     | FK            | The ID of the student who made the answer                    |
| problem_set_id        | Integer                     | FK            | The ID of the problem set to which the answer belongs        |
| programming_id        | Integer                     | FK            | The ID of the programming problem to which the answer belongs |
| pa_code               | Variable characters (16000) | Not Null      | The student's answer in programming code                     |
| pa_actual_score       | Integer                     | Not Null      | Actual score earned for this programming problem answer      |
| pass_count            | Integer                     | Not Null      | The number of test cases passed by the programming problem answer |

## 8. `student_n_ps`

This table record the relationship between students and problem sets. When a student is add to a problem set, a record is created, including the IDs of the student and the problem set. When a student is removed from a problem set, the corresponding record is removed.

| **Field**        | **Type** | **Constrain** | **Field Comments**                                           |
| ---------------- | -------- | ------------- | ------------------------------------------------------------ |
| user_id          | Integer  | PK, FK        | The ID of the student associated with the problem set        |
| problem_set_id   | Integer  | PK, FK        | The ID of the problem set to which the student belongs       |
| first_start_time | Datetime |               | The timestamp when the student first access to the problem set |

The field `first_start_time` records the time when student first access the problem set. For the exam, this is the timestamp when the exam count down begins.

## 9. `op_n_ps`

This table record the relationship between objective problems and problem sets. When an objective problem is add to a problem set, a record is created, including the IDs of the objective problem and the problem set. When an objective problem is removed from a problem set, the corresponding record is removed.

| **Field**            | **Type** | **Constrain** | **Field Comments**                                           |
| -------------------- | -------- | ------------- | ------------------------------------------------------------ |
| objective_problem_id | Integer  | PK, FK        | The ID of the objective problem associated with the problem set |
| problem_set_id       | Integer  | PK, FK        | The ID of the problem set to which the objective problem belongs |

## 10. `p_n_ps`

This table record the relationship between programming problems and problem sets. When a programming problem is add to a problem set, a record is created, including the IDs of the programming problem and the problem set. When a programming problem is removed from a problem set, the corresponding record is removed.

| **Field**      | **Type** | **Constrain** | **Field Comments**                                           |
| -------------- | -------- | ------------- | ------------------------------------------------------------ |
| programming_id | Integer  | PK, FK        | The ID of the programming problem associated with the problem set |
| problem_set_id | Integer  | PK, FK        | The ID of the problem set to which the programming problem belongs |

# Functional Requirements

- There should be lists of student, problem record in the transcript. 
- The performance data should be displayed with numbers and some visualization.
- Teacher's view on student answer sheet should have a similar look as student answer sheet.

# Quality Requirements

- There should be authentication when using the problem set teacher: no access for students, access to owned problems for teachers (can only access problems created by themselves), full access to any problems for administrators. 

# API Requirements

## 1. Get One Problem Set Transcript Information

- URL: `/problem_set/teacher/one_problem_set_info/`
- Type: `GET`
- Bearer Authorization: `YES`
- Input Data:
  - `problemSetId`: problem set ID you want to get with teacher privilege

- Response: 
  - `error_message`: if there is no problem with the submitted parameters and access permissions, `success` will be returned. Otherwise, error information related to the request will be returned. 
  - `problem_set_id`: problem set ID
  - `ps_name`: problem set name
  - `ps_author_id`: problem set author ID
  - `ps_author_name`: problem set author name
  - `ps_start_time`: problem set start time, format like `2025-02-15T17:47`
  - `ps_end_time`: problem set end time, format like `2025-02-15T17:47`
  - `duration`: problem set duration; 0 for assignments, other positive integers for exams
  - `ps_status_message`: descriptive status information about this problem set; either `The problem set has not started`, `The problem set has ended`, or `The problem set has started`.
  - `ps_total_score`: problem set total score 

- Comment: 

  - Teachers cannot get transcript from problem set created by others; administrators do not have such limitation.
  - Problem status are defined in the Notes at the bottom of this doc.

## 2. Get All Student Record (List)

- URL: `/problem_set/teacher/all_student_record/`

- Type: `GET`

- Bearer Authorization: `YES`

- Input Data:

  - `problemSetId`: problem set ID you want to get with teacher privilege

- Response: `A List of All Student Record`

  ```json
  [
      { user_id: '4', name: 'bb', username: '04', permission: '0', first_start_time: '2024-12-16T17:25', ps_actual_score: '25' },
      { user_id: '5', name: 'cc', username: '05', permission: '0', first_start_time: '', ps_actual_score: '0' },
      { user_id: '6', name: 'dd', username: '06', permission: '0', first_start_time: '2024-04-06T17:25', ps_actual_score: '74' },
      { user_id: '7', name: 'ee', username: '06', permission: '0', first_start_time: '2024-04-06T17:25', ps_actual_score: '100' },
      { user_id: '8', name: 'ff', username: '06', permission: '0', first_start_time: '', ps_actual_score: '0' },
  ]
  ```

- Comment: 

  - If the student has not started answering, the `first_start_time` will be empty string `""`
  - Teachers cannot get transcript from problem set created by others; administrators do not have such limitation.

## 3. Get All Objective Problem Record (List)

- URL: `/problem_set/teacher/all_objective_problem_record/`

- Type: `GET`

- Bearer Authorization: `YES`

- Input Data:

  - `problemSetId`: problem set ID you want to get with teacher privilege

- Response: `A List of All Objective Problem Record`

  ```json
  [
      { objective_problem_id: '4', op_description: 'Question 4', op_correct_count: '2', op_answer_count: '6' },
      { objective_problem_id: '5', op_description: 'Question 5', op_correct_count: '4', op_answer_count: '5' },
      { objective_problem_id: '6', op_description: 'Question 6', op_correct_count: '0', op_answer_count: '5' },
      { objective_problem_id: '7', op_description: 'Question 7', op_correct_count: '0', op_answer_count: '0' },
  ]
  ```

- Comment: 

  - `op_answer_count` counts the number of students who answered this problem.
  - `op_correct_count` counts the number of students who answered this problem and received full marks.
  - Teachers cannot get transcript from problem set created by others; administrators do not have such limitation.

## 4. Get All Programming Problem Record (List)

- URL: `/problem_set/teacher/all_programming_record/`

- Type: `GET`

- Bearer Authorization: `YES`

- Input Data:

  - `problemSetId`: problem set ID you want to get with teacher privilege

- Response: `A List of All Programming Problem Record`

  ```json
  [
      { programming_id: '4', p_title: 'Question 4', p_correct_count: '3', p_answer_count: '7' },
      { programming_id: '5', p_title: 'Question 5', p_correct_count: '2', p_answer_count: '6' },
      { programming_id: '6', p_title: 'Question 6', p_correct_count: '0', p_answer_count: '6' },
      { programming_id: '7', p_title: 'Question 7', p_correct_count: '0', p_answer_count: '0' },
  ]
  ```

- Comment: 

  - `p_answer_count` counts the number of students who answered this problem.
  - `p_correct_count` counts the number of students who answered this problem and received full marks, i.e., passed all test cases.
  - Teachers cannot get transcript from problem set created by others; administrators do not have such limitation.

## 5. Get One Student Record

- URL: `/problem_set/teacher/one_student_record/`
- Type: `GET`
- Bearer Authorization: `YES`
- Input Data:
  - `problemSetId`: problem set ID you want to get with teacher privilege
  - `studentId`: student ID of the student from which you want to get record
- Response: 
  - `error_message`: if there is no problem with the submitted parameters and access permissions, `success` will be returned. Otherwise, error information related to the request will be returned. 
  - `problem_set_id`: problem set ID
  - `student_id`: student ID
  - `ps_name`: problem set name
  - `student_name`: student full name
  - `student_username`: student username
  - `ps_start_time`: problem set start time, format like `2025-02-15T17:47`
  - `ps_end_time`: problem set end time, format like `2025-02-15T17:47`
  - `duration`: problem set duration; 0 for assignments, other positive integers for exams
  - `ps_author_name`: problem set author name
  - `ps_total_score`: problem set total score 
  - `ps_actual_score`: student problem set actual score, display regardless of problem set status
  - `first_start_time`: student first start answering time
  - `ps_status_message`: descriptive status information about this problem set; either `The problem set has not started`, `The problem set has ended`, or `The problem set has started`.
- Comment: 

  - Teachers cannot get transcript from problem set created by others; administrators do not have such limitation.
  - Problem status are defined in the Notes at the bottom of this doc.
  - Teachers can see all actual score (including objective problem) of a student record, regardless whether the problem set has ended or not. Be advised that students can only see the actual score of their objective problems only after the problem set has ended.
  - Also, you need to check whether the student belongs to this problem set. Note that you need to check belonging relationship only when you try to get one record. If you try to get a list, you don't need to do that, because you just need to select all record with the problem set ID.

## 6. Get One Student All Objective Problem (List)

- URL: `/problem_set/teacher/one_student_all_objective_problem/`

- Type: `GET`

- Bearer Authorization: `YES`

- Input Data:

  - `problemSetId`: problem set ID you want to get with teacher privilege
  - `studentId`: student ID of the student from which you want to get record

- Response: `A List of One Student All Objective Problem Record`. This list is different from API #3 Get All Objective Problem Record which pulls the all objective problem record of a problem set instead of a student. 

  ```json
  [
      { objective_problem_id: '1', op_description: 'Question 1', op_total_score: '10', opa_actual_score: '10', opa_status: 'Answered' },
      { objective_problem_id: '2', op_description: 'Question 2', op_total_score: '8', opa_actual_score: '0', opa_status: 'Not Answered' },
      { objective_problem_id: '3', op_description: 'Question 3', op_total_score: '5', opa_actual_score: '0', opa_status: 'Not Answered' },
  ]
  ```

- Comment: 

  - `opa_actual_score` will be displayed always, regardless whether the problem set has ended or not. Be advised that students can only see the actual score of their objective problems only after the problem set has ended.
  - Teachers cannot get transcript from problem set created by others; administrators do not have such limitation.

## 7. Get One Student All Programming Problem (List)

- URL: `/problem_set/teacher/one_student_all_programming/`

- Type: `GET`

- Bearer Authorization: `YES`

- Input Data:

  - `problemSetId`: problem set ID you want to get with teacher privilege
  - `studentId`: student ID of the student from which you want to get record

- Response: `A List of One Student All Programming Problem Record`. This list is different from API #4 Get All Programming Problem Record which pulls the all programming problem record of a problem set instead of a student. 

  ```json
  [
      { programming_id: '1', p_title: 'Programming 1', p_total_score: '25', pa_actual_score: '20', pa_status: 'Answered' },
      { programming_id: '2', p_title: 'Programming 2', p_total_score: '20', pa_actual_score: '10', pa_status: 'Answered' },
      { programming_id: '3', p_title: 'Programming 3', p_total_score: '15', pa_actual_score: '0', pa_status: 'Not Answered' },
  ]
  ```

- Comment: 

  - `pa_actual_score` will be displayed always, regardless whether the problem set has ended or not. This is the same behaviour as the student API; please refer to the Notes at the bottom of this doc to see the defined student API behaviour. 
  - The `pa_actual_score` is calculated by the equation:  $pa\_actual\_score = p\_total\_score \times \frac{pass\_count}{tc\_count}$
  - Teachers cannot get transcript from problem set created by others; administrators do not have such limitation.

## 8. Get One Student One Objective Problem

- URL: `/problem_set/teacher/one_student_one_objective_problem/`
- Type: `GET`
- Bearer Authorization: `YES`
- Input Data:
  - `problemSetId`: problem set ID you want to get with teacher privilege
  - `studentId`: student ID of the student from which you want to get record
  - `objectiveProblemId`: objective problem ID from which you want to get record
- Response: 
  - `error_message`: if there is no problem with the submitted parameters and access permissions, `success` will be returned. Otherwise, error information related to the request will be returned. 
  - `objective_problem_id`: objective problem ID
  - `op_description`: objective problem description
  - `op_total_score`: objective problem total score
  - `op_correct_answer`: objective problem corrent answer
  - `student_id`: student ID
  - `student_name`: student name
  - `student_username`: student username
  - `opa_actual_answer`: objective problem answer 
  - `opa_actual_score`: objective problem answer actual score, display regardless of problem set status
- Comment: 

  - Teachers cannot get transcript from problem set created by others; administrators do not have such limitation.
  - `opa_actual_score` and `op_correct_answer` will be displayed always, regardless whether the problem set has ended or not. Be advised that students can only see the actual score and correct answer of their objective problems only after the problem set has ended.
  - You need to check whether the student belongs to this problem set. 
  - You need to check whether the problem belongs to this problem set.

## 9. Get One Student One Programming Problem

- URL: `/problem_set/teacher/one_student_one_programming/`
- Type: `GET`
- Bearer Authorization: `YES`
- Input Data:
  - `problemSetId`: problem set ID you want to get with teacher privilege
  - `studentId`: student ID of the student from which you want to get record
  - `programmingId`: programming problem ID from which you want to get record
- Response: 
  - `error_message`: if there is no problem with the submitted parameters and access permissions, `success` will be returned. Otherwise, error information related to the request will be returned. 
  - `programming_id`: programming problem ID
  - `p_title`: programming problem title
  - `p_description`: programming problem description
  - `p_total_score`: programming problem total score
  - `time_limit`: programming problem time limit
  - `code_size_limit`: programming problem code size limit
  - `tc_count`: programming problem test case count
  - `student_id`: student ID
  - `student_name`: student name
  - `student_username`: student username
  - `pa_code`: student programming problem answer code
  - `pa_actual_score`: student programming problem answer actual score
  - `pass_count`: student programming problem answer test case pass count
- Comment: 

  - Teachers cannot get transcript from problem set created by others; administrators do not have such limitation.
  - `pa_actual_score` will be displayed always, regardless whether the problem set has ended or not. This is the same behaviour as the student API; please refer to the Notes at the bottom of this doc to see the defined student API behaviour. 
  - The `pa_actual_score` is calculated by the equation:  $pa\_actual\_score = p\_total\_score \times \frac{pass\_count}{tc\_count}$
  - You need to check whether the student belongs to this problem set. 
  - You need to check whether the problem belongs to this problem set.

# Notes: 题目集状态图

题目集需要根据题目集的开始时间，题目集结束时间，题目集允许的作答时间（考试时间），和现在的时间控制学生的作答行为，状态图如下图所示：

![image-20250304211118499](Problem%20Set%20Student%20Backend.assets/image-20250304211118499.png)