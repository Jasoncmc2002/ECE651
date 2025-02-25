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
| problem_set_id | Integer  | PK, FK        | 学The ID of the problem set to which the programming problem belongs |

# Functional Requirements

- There should be a listing page that lists all problem sets in the database.

# Quality Requirements

- There should be authentication when using the programming problem management: no access for students, access to owned problems for teachers (can only access problems created by themselves), full access to any problems for administrators. "Access" means edit and delete.