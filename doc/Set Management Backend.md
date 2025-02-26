Set Management Backend

[TOC]



# User Story Related

As a teacher, 

I want to manage exams and assignments, which are called problem sets,

so that I can create, edit, delete problem sets. 

As a teacher,

I want to manage objective problems, programming problems and students in problem sets,

so that I can add or drop problems and students to or from problem sets.

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

- There should be a listing page that lists all problem sets in the database. The exams and assignments should be listed seperately.
- For editing objective problems, programming problems and students associated with the problem set, there should be a search function to assist user with fuzzy search.

# Quality Requirements

- There should be authentication when using the problem set management: no access for students, access to owned problem sets for teachers (can only access problem sets created by themselves), full access to any problem sets for administrators. "Access" means edit and delete.
- When performing a search on objective problems, programming problems and students, the search result should exclude items that have been added to the problem sets previously.
- When deleting or adding objective problems, programming problems and students, there should be a check whether the item has been deleted or added before performing the database operation to prevent duplicated operation.

# API Requirements

## 1. Create Problem Sets

- URL: `/set_manage/`
- Type: `POST`
- Bearer Authorization: `YES`
- Input Data:
  - `psName`: problem set name or title
  - `psStartTime`: problem set start date and time in a string, like `2007-12-03T10:15:30`
  - `psEndTime`: problem set end date and time in a string, like `2007-12-03T10:15:30`
  - `duration`: problem set duration for exam, unit: minute; for assignments the value is `0`
- Response:
  - `error_message`: If there is no problem with the submitted parameters and access permissions, `success` will be returned. Otherwise, error information related to the request will be returned. 
  - `problem_set_id`: The ID of the newly created problem set, coresponding to `problem_set_id` in the table `problem_set`. 
- Comment: Only teachers and administrators can create problem sets. Make sure to check the length of submitted parameters against the length of the field in the table `problem_set`. Also, you should check logical errors in the start time, end time and duration, i.e., `start_time + duration <= end_time`.

## 2. Delete Problem Sets

- URL: `/set_manage/`
- Type: `DELETE`
- Bearer Authorization: `YES`
- Input Data:
  - `problemSetId`: the ID of the problem set that you want to delete
- Response:
  - `error_message`: If there is no problem with the submitted parameters and access permissions, `success` will be returned. Otherwise, error information related to the request will be returned. 
- Comment: Only teachers and administrators can delete problem sets. Teachers can delete problem sets created by themselves. Administrators can delete problem sets created by anyone. 

## 3. Update Problem Sets

- URL: `/set_manage/`
- Type: `POST`
- Bearer Authorization: `YES`
- Input Data:
  - `problemSetId`: the ID of the problem set that you want to update
  - `psName`: the new problem set name or title
  - `psStartTime`: the new problem set start date and time in a string, like `2007-12-03T10:15:30`
  - `psEndTime`: the new problem set end date and time in a string, like `2007-12-03T10:15:30`
  - `duration`: the new problem set duration for exam, unit: minute; for assignments the value is `0`
- Response:
  - `error_message`: If there is no problem with the submitted parameters and access permissions, `success` will be returned. Otherwise, error information related to the request will be returned. 
- Comment: 
  - Only teachers and administrators can update problem sets. 
  - Teachers can update problem sets created by themselves. 
  - Administrators can update problem sets created by anyone. 
  - Unchanged parameters should also be sent as input data. Just keep them as their unchanged value. 
  - Make sure to check the length of submitted parameters against the length of the field in the table `problem_set`.
  - You should check logical errors in the start time, end time and duration, i.e., `start_time + duration <= end_time`.

## 4. Get One Problem Set (Get One)

- URL: `/set_manage/`
- Type: `GET`
- Bearer Authorization: `YES`
- Input Data:
  - `problemSetId`: the ID of the problem set that you want to get. 
- Response:
  - `error_message`: If there is no problem with the submitted parameters and access permissions, `success` will be returned. Otherwise, error information related to the request will be returned. 
  - `problem_set_id`: the ID of the problem set that you want to get
  - `ps_name`: problem set title or name
  - `ps_author_id`: problem set author ID
  - `ps_author_name`: problem set author name
  - `ps_start_time`: problem set start time, in string, like: `2007-12-03T10:15:30`
  - `ps_end_time`: problem set end time, in string, like: `2007-12-03T10:15:30`
  - `duration`: problem set duration
- Comment: None

## 5. Get Assignment List

- URL: `/set_manage/assignment/`

- Type: `GET`

- Bearer Authorization: `YES`

- Input Data: `None`

- Response: `A List of All Assignments` like this:

  ```json
  [
      {
          "ps_name": "Midterm Exam Practice",
          "ps_end_time": "2025-02-27T20:15",
          "problem_set_id": "6",
          "ps_author_name": "Mary(123)",
          "ps_start_time": "2025-02-27T20:11"
      },
      {
          "ps_name": "Assignment 4: Functions",
          "ps_end_time": "2025-01-25T18:12",
          "problem_set_id": "5",
          "ps_author_name": "Mary(123)",
          "ps_start_time": "2025-01-18T16:06"
      },
      {
          "ps_name": "Assignment 3: Control Flow",
          "ps_end_time": "2025-01-18T16:09",
          "problem_set_id": "4",
          "ps_author_name": "Gary(pyxc)",
          "ps_start_time": "2025-01-18T16:03"
      }
  ]
  ```

- Comment: 

  - Assignments are problem sets with the `duration` value of `0`.
  - Exams are problem sets with the `duration` value `>0`.

## 6. Get Exam List

- URL: `/set_manage/exam/`

- Type: `GET`

- Bearer Authorization: `YES`

- Input Data: `None`

- Response: `A List of All Exams` like this:

  ```json
  [
      {
          "duration": "60",
          "ps_name": "Midterm Exam",
          "ps_end_time": "2025-02-27T20:20",
          "problem_set_id": "7",
          "ps_author_name": "Mary(123)",
          "ps_start_time": "2025-02-27T20:16"
      },
      {
          "duration": "30",
          "ps_name": "Entry Exam",
          "ps_end_time": "2025-01-15T21:35",
          "problem_set_id": "1",
          "ps_author_name": "Gary(pyxc)",
          "ps_start_time": "2025-01-15T19:00"
      }
  ]
  ```

- Comment: 

  - Assignments are problem sets with the `duration` value of `0`.
  - Exams are problem sets with the `duration` value `>0`.

## 7. Search Objective Problems

- URL: `/set_manage/objective_problem/search/`

- Type: `GET`

- Bearer Authorization: `YES`

- Input Data:

  - `problemSetId`: the ID of the problem set on which you try to perfom a search
  - `opDescription`: fuzzy objecitve problem description
  - `opTag`: fuzzy objective problem tag
  - `opDifficultyMin`: the lower limit of the difficulty range
  - `opDifficultyMax`: the upper limit of the difficulty range

- Response: `A List of Searched Result` like this:

  ```json
  [
      {
          "op_description": "下图给出的网络从$s$到$t$的最大流是：\n\n![img](https://images.ptausercontent....",
          "objective_problem_id": "1",
          "op_use_count": "0",
          "op_difficulty": "5",
          "op_tag": "图论"
      },
      {
          "op_description": "下图给出的网络从$s$到$t$的最大流是：\n\n![img](https://images.ptausercontent....",
          "objective_problem_id": "2",
          "op_use_count": "1",
          "op_difficulty": "5",
          "op_tag": "图论"
      },
      {
          "op_description": "程序如下：\n\n```python\ntry:\n\tnumber = int(input(\"请输入数字：\"))\n\tprint(...",
          "objective_problem_id": "12",
          "op_use_count": "0",
          "op_difficulty": "5",
          "op_tag": "异常处理"
      }
  ]
  ```

- Comment: This search will exclude objective problems that have been added to the problem set to prevent duplicated add.

## 8. Add Objective Problems 

- URL: `/set_manage/objective_problem/`
- Type: `POST`
- Bearer Authorization: `YES`
- Input Data:
  - `problemSetId`: the ID of the problem set to which you want to add objective problems
  - `objectiveProblemId`: the ID of the objective problem that you want to add
- Response:
  - `error_message`: If there is no problem with the submitted parameters and access permissions, `success` will be returned. Otherwise, error information related to the request will be returned. 
- Comment: you should not add the objective problem if it has been added previously.

## 9. Delete Objective Problems

- URL: `/set_manage/objective_problem/`
- Type: `DELETE`
- Bearer Authorization: `YES`
- Input Data:
  - `problemSetId`: the ID of the problem set from which you want to delete objective problems
  - `objectiveProblemId`: the ID of the objective problem that you want to delete
- Response:
  - `error_message`: If there is no problem with the submitted parameters and access permissions, `success` will be returned. Otherwise, error information related to the request will be returned. 
- Comment: 
  - You should not delete the objective problem if it has been deleted previously.
  - When deleting the objective problem, you should also delete objective problem answers associated with this objective problem and this problem set.

## 10. Get Added Objective Problems (List)

- URL: `/set_manage/objective_problem/get_added/`

- Type: `GET`

- Bearer Authorization: `YES`

- Input Data:

  - `problemSetId`: the ID of the problem set from which the added objective problems you want to get

- Response: `A List of Added Objective Problems` like this:

  ```json
  [
      {
          "op_description": "**判断题**\n\n最优二叉搜索树的根结点一定存放的是搜索概率最高的那个关键字。 \n\n填写`T/F`...",
          "objective_problem_id": "3",
          "op_use_count": "2",
          "op_difficulty": "3",
          "op_tag": "数据结构"
      },
      {
          "op_description": "定义类如下：\n\n```python\nclass Hello(object):\n\tdef __init__(self,na...",
          "objective_problem_id": "17",
          "op_use_count": "1",
          "op_difficulty": "5",
          "op_tag": "类和对象"
      },
      {
          "op_description": "函数如下：\n\n```python\ndef chanageList(list):\n\tlist.append(\"end\")\n...",
          "objective_problem_id": "21",
          "op_use_count": "1",
          "op_difficulty": "5",
          "op_tag": "函数"
      }
  ]
  ```

  Comment: None

## 11. Search Programming Problems

- URL: `/set_manage/programming/search/`

- Type: `GET`

- Bearer Authorization: `YES`

- Input Data:

  - `problemSetId`: the ID of the problem set on which you try to perfom a search
  - `pTitle`: fuzzy programming problem title
  - `pTag`: fuzzy programming problem tag
  - `pDifficultyMin`: the lower limit of the difficulty range
  - `pDifficultyMax`: the upper limit of the difficulty range

- Response: `A List of Searched Result` like this:

  ```json
  [
      {
          "programming_id": "1",
          "p_title": "L1-010 比较大小",
          "p_difficulty": "5",
          "p_tag": "函数",
          "p_use_count": "2"
      },
      {
          "programming_id": "3",
          "p_title": "L1-015 跟奥巴马一起画方块",
          "p_difficulty": "5",
          "p_tag": "控制语句",
          "p_use_count": "1"
      },
      {
          "programming_id": "10",
          "p_title": "结尾0的个数",
          "p_difficulty": "5",
          "p_tag": "函数",
          "p_use_count": "0"
      }
  ]
  ```

- Comment: This search will exclude programming problems that have been added to the problem set to prevent duplicated add.

## 12. Add Programming Problems

- URL: `/set_manage/programming/`
- Type: `POST`
- Bearer Authorization: `YES`
- Input Data:
  - `problemSetId`: the ID of the problem set to which you want to add programming problems
  - `programmingId`: the ID of the programming problem that you want to add
- Response:
  - `error_message`: If there is no problem with the submitted parameters and access permissions, `success` will be returned. Otherwise, error information related to the request will be returned. 
- Comment: you should not add the programming problem if it has been added previously.

## 13. Delete Programming Problems

- URL: `/set_manage/programming/`
- Type: `DELETE`
- Bearer Authorization: `YES`
- Input Data:
  - `problemSetId`: the ID of the problem set from which you want to delete programming problems
  - `programmingId`: the ID of the programming problem that you want to delete
- Response:
  - `error_message`: If there is no problem with the submitted parameters and access permissions, `success` will be returned. Otherwise, error information related to the request will be returned. 
- Comment: 
  - You should not delete the programming problem if it has been deleted previously.
  - When deleting the programming problem, you should also delete programming problem answers associated with this programming problem and this problem set.

## 14. Get Added Programming Problems (List)

- URL: `/set_manage/programming/get_added/`

- Type: `GET`

- Bearer Authorization: `YES`

- Input Data:

  - `problemSetId`: the ID of the problem set from which the added programming problems you want to get

- Response: `A List of Added Programming Problems` like this:

  ```json
  [
      {
          "programming_id": "9",
          "p_title": "回文子串",
          "p_difficulty": "4",
          "p_tag": "控制语句",
          "p_use_count": "1"
      },
      {
          "programming_id": "12",
          "p_title": "函数题：光棍的悲伤",
          "p_difficulty": "3",
          "p_tag": "函数",
          "p_use_count": "1"
      }
  ]
  ```

  Comment: None

## 15. Search Students

- URL: `/set_manage/student/search/`

- Type: `GET`

- Bearer Authorization: `YES`

- Input Data:

  - `problemSetId`: the ID of the problem set on which you try to perfom a search
  - `username`: fuzzy student username
  - `name`: fuzzy student actual name

- Response: `A List of Searched Result` like this:

  ```json
  [
      {
          "user_id": "1",
          "name": "Ross(pyw)",
          "permission": "2",
          "username": "01"
      },
      {
          "user_id": "2",
          "name": "Gary(pyxc)",
          "permission": "1",
          "username": "02"
      },
      {
          "user_id": "7",
          "name": "Mary(123)",
          "permission": "1",
          "username": "05"
      }
  ]
  ```

- Comment: This search will exclude students that have been added to the problem set to prevent duplicated add.

## 16. Add Students (to Problem Sets)

- URL: `/set_manage/student/`
- Type: `POST`
- Bearer Authorization: `YES`
- Input Data:
  - `problemSetId`: the ID of the problem set to which you want to add students
  - `userId`: the ID of the student that you want to add
- Response:
  - `error_message`: If there is no problem with the submitted parameters and access permissions, `success` will be returned. Otherwise, error information related to the request will be returned. 
- Comment: you should not add the student if it has been added previously.

## 17. Delete Students (from Problem Sets)

- URL: `/set_manage/student/`
- Type: `DELETE`
- Bearer Authorization: `YES`
- Input Data:
  - `problemSetId`: the ID of the problem set from which you want to delete students
  - `userId`: the ID of the student that you want to delete
- Response:
  - `error_message`: If there is no problem with the submitted parameters and access permissions, `success` will be returned. Otherwise, error information related to the request will be returned. 
- Comment: 
  - You should not delete the student if it has been deleted previously.
  - When deleting the student, you should also delete **objective problem answers** associated with this student and this problem set.
  - When deleting the student, you should also delete **programming problem answers** associated with this student and this problem set.

## 18. Get Added Students (List)

- URL: `/set_manage/student/get_added/`

- Type: `GET`

- Bearer Authorization: `YES`

- Input Data:

  - `problemSetId`: the ID of the problem set from which the added students you want to get

- Response: `A List of Added Students` like this:

  ```json
  [
      {
          "user_id": "3",
          "name": "Larry",
          "permission": "0",
          "username": "03"
      },
      {
          "user_id": "8",
          "name": "Defaul User",
          "permission": "0",
          "username": "08"
      }
  ]
  ```

  Comment: None