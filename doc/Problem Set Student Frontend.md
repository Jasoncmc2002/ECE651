Problem Set Student Frontend

[TOC]



# User Story Related

As a student, 

I want to access my problem sets (assigned to me),

so that I can view problems, submit my answers and get graded feedback. 

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

- There should be a listing page that lists all problem sets assigned to the student and a list of all active problem sets.
- When viewing a problem set, there should be a navigation that helps student to jump between problems.
- There should also be a summary page for the problem set, showing that the answer status of each problem.

# Quality Requirements

- There should be an access control for students based on the current time and the start time, end time, duration of the problem set.

# API Used

## 1. Get Active Problem Set

- URL: `/problem_set/active/`

- Type: `GET`

- Bearer Authorization: `YES`

- Input Data: None

- Response: `A List of Active Problem Sets`

  ```json
  [
      {
          "duration": "0",
          "ps_name": "Assignment 1 - Basic Syntax",
          "ps_end_time": "2025-02-16T18:10",
          "problem_set_id": "2",
          "ps_author_name": "Jerry(pyxc)",
          "ps_start_time": "2025-02-15T17:47"
      },
      {
          "duration": "30",
          "ps_name": "Entry Exam",
          "ps_end_time": "2025-01-15T21:35",
          "problem_set_id": "1",
          "ps_author_name": "Jerry(pyxc)",
          "ps_start_time": "2025-01-15T19:00"
      }
  ]
  ```

  

- Comment: 

  - Criteria for Active Problem Sets: `start_time < now < end_time`, `<` here means `is before`. 
  - Also, make sure the problem sets are assigned to this student using `student_n_ps` table.

## 2. Get All Problem Set

- URL: `/problem_set/all/`

- Type: `GET`

- Bearer Authorization: `YES`

- Input Data: None

- Response: `A List of All Problem Sets`, same format as the previous one.

  ```json
  [
      {
          "duration": "0",
          "ps_name": "Assignment 1 - Basic Syntax",
          "ps_end_time": "2025-02-16T18:10",
          "problem_set_id": "2",
          "ps_author_name": "Jerry(pyxc)",
          "ps_start_time": "2025-02-15T17:47"
      },
      {
          "duration": "30",
          "ps_name": "Entry Exam",
          "ps_end_time": "2025-01-15T21:35",
          "problem_set_id": "1",
          "ps_author_name": "Jerry(pyxc)",
          "ps_start_time": "2025-01-15T19:00"
      }
  ]
  ```

  

- Comment: 
  - Make sure the problem sets are assigned to this student using `student_n_ps` table.

## 3. Get One Problem Set

- URL: `/problem_set/`
- Type: `GET`
- Bearer Authorization: `YES`
- Input Data:
  - `problemSetId`: the ID of the problem set you want to get.
- Output Data:
  - `error_message`: If there is no problem with the submitted parameters and access permissions, `success` will be returned. Otherwise, error information related to the request will be returned. 
  - `problem_set_id`: problem set id
  - `ps_name`: problem set name
  - `ps_author_id`: problem set author id
  - `ps_author_name`: problem set author name
  - `ps_start_time`: problem set start time
  - `ps_end_time`: problem set end time
  - `duration`: duration of the exam; for assignment it would be `0`
  - `first_start_time`: first start time field of the `student_n_ps` table; if the student has not start the problem set answer, this value would be an empty string `""`.
  - `ps_status`: problem set status; for the status definition, please see the notes at the bottom of this doc.
  - `ps_total_score`: sum of scores of all problems (including objective and programming) in the problem set.
  - `ps_actual_score`: sum of student actual score of all answered problems; if the problem set has not ended, i.e., `now < end_time`, this value would be a "hidden" string `--`.

- Comment: 
  - Make sure the problem set is assigned to this student using `student_n_ps` table.
  - Make sure the problem set is started by `start_time < now`. You cannot get one problem set that has not started.

## 4. Start a Problem Set

- URL: `/problem_set/start/`
- Type: `PUT`
- Bearer Authorization: `YES`
- Input Data:
  - `problemSetId`: the ID of the problem set you want to start.
- Output Data:
  - `error_message`: If there is no problem with the submitted parameters and access permissions, `success` will be returned. Otherwise, error information related to the request will be returned. 

- Comment: 
  - Exams need first start time to control the answering duration of a student. For example, if the exam is limited in 60 minutes, the system needs to know when the student first start answer the question, so that it can control the student answering time.
  - To start a problem set, basically you just update the `first_start_time` field in `student_n_ps` table. Why update? this record is created when a student is add to the problem set. At that time, `first_start_time` is `NULL` in the record. So we update this field when a student start the problem set.
  - Make sure the problem set is assigned to this student using `student_n_ps` table.
  - Make sure the problem set is started by `start_time < now`. You cannot **start answering** one problem set **that has not started**.
  - Make sure the problem set has not ended by `now < end_time`. You cannot **start answering** one problem set **that has ended.**
  - Make sure you have not previously **started answering**, by checking the `first_start_time` field in `student_n_ps` table is empty or not. You can only start answer once.

## 5. Get All Objective Problems

- URL: `/problem_set/objective_problem/all/`

- Type: `GET`

- Bearer Authorization: `YES`

- Input Data:

  - `problemSetId`: the ID of the problem set

- Response: `A List of All Objective Problems in the Problem Set`, this is used for showing a summary of student's answer sheet.

  ```json
  [
      {
          "op_description": "**Fill in `T/F`**\n\nThe root node of the optimal binary searc...",
          "objective_problem_id": "3",
          "opa_actual_score": "0",
          "opa_status": "Not Answered",
          "op_total_score": "8"
      },
      {
          "op_description": "**Single-choice question**\n\nWhich of the following functions...",
          "objective_problem_id": "4",
          "opa_actual_score": "5",
          "opa_status": "Answered",
          "op_total_score": "5"
      },
      {
          "op_description": "**True or false**\n\nWhen using dynamic programming instead of...",
          "objective_problem_id": "6",
          "opa_actual_score": "5",
          "opa_status": "Answered",
          "op_total_score": "5"
      }
  ]
  ```

- Comment: 
  - Make sure the problem set is assigned to this student using `student_n_ps` table.
  - Make sure the problem set is started by `start_time < now`. You cannot **get problem list** one problem set **that has not started**.
  - Make sure you **have started answering** the problem set by checking the `first_start_time` field in `student_n_ps` table is empty or not.
  - The actual score visibility (show the score or just show `--`) is defined in the Notes at the bottom of this doc.
  - To check whether a problem is answered or not, you should look at the `objective_problem_answer` table or `programming_answer` table.

## 6. Get All Programming Problems

- URL: `/problem_set/programming/all/`

- Type: `GET`

- Bearer Authorization: `YES`

- Input Data:

  - `problemSetId`: the ID of the problem set

- Response: `A List of All Programming Problems in the Problem Set`, this is used for showing a summary of student's answer sheet.

  ```json
  [
      {
          "pa_status": "Answered",
          "programming_id": "1",
          "p_title": "L1-010 Comparison",
          "p_total_score": "20",
          "pa_actual_score": "0"
      },
      {
          "pa_status": "Answered",
          "programming_id": "2",
          "p_title": "L1-001 Hello World",
          "p_total_score": "15",
          "pa_actual_score": "15"
      },
      {
          "pa_status": "Answered",
          "programming_id": "6",
          "p_title": "Code Completion A+B",
          "p_total_score": "15",
          "pa_actual_score": "15"
      }
  ]
  ```

- Comment: 
  - Make sure the problem set is assigned to this student using `student_n_ps` table.
  - Make sure the problem set is started by `start_time < now`. You cannot **get problem list** one problem set **that has not started**.
  - Make sure you **have started answering** the problem set by checking the `first_start_time` field in `student_n_ps` table is empty or not.
  - The actual score visibility (show the score or just show `--`) is defined in the Notes at the bottom of this doc.
  - To check whether a problem is answered or not, you should look at the `objective_problem_answer` table or `programming_answer` table.

## 7. Get One Objective Problem

- URL: `/problem_set/objective_problem/one/`
- Type: `GET`
- Bearer Authorization: `YES`
- Input Data:
  - `problemSetId`: the ID of the problem set
  - `objectiveProblemId`: the ID of the objective problem
- Output Data:
  - `error_message`: If there is no problem with the submitted parameters and access permissions, `success` will be returned. Otherwise, error information related to the request will be returned. 
  - `op_description`: objective problem description
  - `op_total_score`: objective problem total score
  - `first_start_time`: problem set first start tiem for count down feature
  - `duration`: problem set duration for count down feature
  - `ps_end_time`: problem set end tiem for count down feature
  - `ps_status`: problem set status for count down feature
  - `op_correct_answer`: only returned when the problem set is ended, by `end_time < now`; otherwise return empty string `""`. You should only show the answer after the problem set is ended.
  - `opa_actual_answer`: objective problem actual answer by the student
  - `opa_actual_score`: only returned when the problem set is ended, by `end_time < now`; otherwise return "hidden" string `--`. 
- Comment:
  - Make sure the problem is added to this problem set using `op_n_ps` table or `p_n_ps` table.
  - Make sure the problem set is assigned to this student using `student_n_ps` table.
  - Make sure the problem set is started by `start_time < now`. You cannot **get a problem** on one problem set **that has not started**.
  - Make sure you **have started answering** the problem set by checking the `first_start_time` field in `student_n_ps` table is empty or not.
  - The actual score visibility (show the score or just show `--`) is defined in the Notes at the bottom of this doc.
  - To check whether a problem is answered or not, as well as retrieving the answer if answered, you should look at the `objective_problem_answer` table or `programming_answer` table.
  - This API will also **return some information about the problem set**, as it is needed when answering a single problem, identifying the problem set that the student is answering.

## 8. Submit One Objective Problem Answer

- URL: `/problem_set/objective_problem/submit/`
- Type: `POST`
- Bearer Authorization: `YES`
- Input Data:
  - `problemSetId`: the ID of the problem set
  - `objectiveProblemId`: the ID of the objective problem
  - `opaActualAnswer`: student answer for the objective problem
- Output Data:
  - `error_message`: If there is no problem with the submitted parameters and access permissions, `success` will be returned. Otherwise, error information related to the request will be returned. 
- Comment:
  - Make sure the problem is added to this problem set using `op_n_ps` table or `p_n_ps` table.
  - Make sure the problem set is assigned to this student using `student_n_ps` table.
  - Please refer to the problem status note at the bottom of this doc to find rules about when can you sumbit an answer. 
  - Check the size of the answer against database definition.

## 9. Get One Programming Problem

- URL: `/problem_set/programming/one/`
- Type: `GET`
- Bearer Authorization: `YES`
- Input Data:
  - `problemSetId`: the ID of the problem set
  - `programmingId`: the ID of the programming problem
- Output Data:
  - `error_message`: If there is no problem with the submitted parameters and access permissions, `success` will be returned. Otherwise, error information related to the request will be returned. 
  - `p_title`: programming problem title
  - `p_description`: programming problem description
  - `p_total_score`: programming problem total score
  - `time_limit`: programming problem time limit
  - `code_size_limit`: programming problem code size limit
  - `tc_count`: programming problem test case count
  - `first_start_time`: problem set first start tiem for count down feature
  - `duration`: problem set duration for count down feature
  - `ps_end_time`: problem set end tiem for count down feature
  - `pa_code`: student answers for programming problem 
  - `pa_actual_score`: student answer score, shown after submission, regardless whether the problem set has ended or not.  $pa\_actual\_score = p\_total\_score \times \frac{pass\_count}{tc\_count}$
  - `pass_count`: number of test cases passed w.r.t. the `pa_code`
  - `ps_status`: problem set status for count down feature
- Comment:
  - Make sure the problem is added to this problem set using `op_n_ps` table or `p_n_ps` table.
  - Make sure the problem set is assigned to this student using `student_n_ps` table.
  - Make sure the problem set is started by `start_time < now`. You cannot **get a problem** on one problem set **that has not started**.
  - Make sure you **have started answering** the problem set by checking the `first_start_time` field in `student_n_ps` table is empty or not.
  - The actual score visibility (show the score or just show `--`) is defined in the Notes at the bottom of this doc.
  - To check whether a problem is answered or not, as well as retrieving the answer if answered, you should look at the `objective_problem_answer` table or `programming_answer` table.
  - This API will also **return some information about the problem set**, as it is needed when answering a single problem, identifying the problem set that the student is answering.

## 10. Submit One Programming Problem Answer

- URL: `/problem_set/programming/submit/`
- Type: `POST`
- Bearer Authorization: `YES`
- Input Data:
  - `problemSetId`: the ID of the problem set
  - `programmingId`: the ID of the programming problem
  - `paCode`: student answer code for the programming problem
- Output Data:
  - `error_message`: If there is no problem with the submitted parameters and access permissions, `success` will be returned. Otherwise, error information related to the request will be returned. 
  - `test_input`: test input of the test case that the code does not pass, optional, only returned when some test case does not pass.
  - `test_output`: test expected output of the test case that the code does not pass, optional, only returned when some test case does not pass.
  - `pa_actual_score`: student answer score, shown after submission, regardless whether the problem set has ended or not.  $pa\_actual\_score = p\_total\_score \times \frac{pass\_count}{tc\_count}$
  - `pass_count`: number of test cases passed w.r.t. the `pa_code`
  - `tc_count`: programming problem test case count
  - `res_message`: conclusion of student submitted code, either `Wrong Answer`, `Partially Accepted` or `Accepted` .
- Comment:
  - Make sure the problem is added to this problem set using `op_n_ps` table or `p_n_ps` table.
  - Make sure the problem set is assigned to this student using `student_n_ps` table.
  - Please refer to the problem status note at the bottom of this doc to find rules about when can you sumbit an answer. 
  - Check the size of the answer against database definition.

## 11. Submit Special Judge

- URL: `/problem_set/programming/special_judge/`
- Type: `POST`
- Bearer Authorization: `YES`
- Input Data:
  - `problemSetId`: the ID of the problem set
  - `programmingId`: the ID of the programming problem
  - `paCode`: student answer code for the programming problem
  - `testInput`: special judge test input
- Output Data:
  - `error_message`: If there is no problem with the submitted parameters and access permissions, `success` will be returned. Otherwise, error information related to the request will be returned. 
  - `test_output`: special judge test output
- Comment:
  - Make sure the problem is added to this problem set using `op_n_ps` table or `p_n_ps` table.
  - Make sure the problem set is assigned to this student using `student_n_ps` table.
  - Please refer to the problem status note at the bottom of this doc to find rules about when can you sumbit an answer. 
  - Check the size of the answer against database definition.

# Notes: 题目集状态图

题目集需要根据题目集的开始时间，题目集结束时间，题目集允许的作答时间（考试时间），和现在的时间控制学生的作答行为，状态图如下图所示：

![image-20250304211118499](Problem%20Set%20Student%20Backend.assets/image-20250304211118499.png)