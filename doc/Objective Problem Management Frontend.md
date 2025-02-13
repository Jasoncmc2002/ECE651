Objective Problem Management

# User Story Related

As a teacher,

I want to manage objective problems,

so that I can create, edit, delete objective problems.

# Database Structure

**Table name: `objective_problem`**

| **Field**            | **Type**                    | **Constrain** | **Field Comments** |
| -------------------- | --------------------------- | ------------- | ------------------ |
| objective_problem_id | Integer                     | PK            | 客观题实体主键     |
| user_id              | Integer                     | FK            | 客观题作者ID       |
| op_description       | Variable characters (10000) | Not Null      | 客观题题干         |
| op_total_score       | Integer                     | Not Null      | 客观题总分         |
| op_correct_answer    | Variable characters (1024)  | Not Null      | 客观题答案         |
| op_tag               | Variable characters (100)   | Not Null      | 客观题标签         |
| op_diffiulty         | Integer                     | Not Null      | 客观题难度系数     |

# Functional Requirements

- There should be a listing page that lists all objective problems in the database.
- The UI should have a paging function to show the list of all objective problems.

# Quality Requirements

- There should be authentication when using the objective problem management: no access for students, access to owned problems for teachers (can only access problems created by themselves), full access to any problems for administrators. "Access" means edit and delete.
- There should be a main page for both objective and programming problems sub-pages, like a fork in the road. Sub-pages should have a similar UI design, easy to switch between them like using tabs on Chrome.

# API Used

**Note 1: 大家API测试 (Integration Test) 时请把结果截图放在issue的comment中**

**Note 2: 大家记得写一些Unit Test，放在`scr/test/`文件夹中**

**Note 3: 需要验证的API都是Bearer验证，前端Ajax请求例子如下**

![Screenshot 2025-02-06 at 11.08.23 AM](Objective%20Problem%20Management%20Backend.assets/Screenshot%202025-02-06%20at%2011.08.23%E2%80%AFAM.png)

| 请求的Key | 含义                                                         |
| --------- | ------------------------------------------------------------ |
| url       | 访问API的地址                                                |
| type      | 请求类型，可以是GET，POST，PUT，DELETE。                     |
| data      | 请求发送的数据，如果API不需要输入数据，则没有这一项          |
| headers   | 请求的附带验证信息，如果API不需要验证，则没有这一项          |
| success   | 请求成功返回的前端处理逻辑（回调函数），几乎所有的API都会返回一个`error_message`告诉前端处理请求的结果 |

## 1. Create Objective Problems

- URL: `/problem_manage/objective_problem_manage/`
- Type: `POST`
- Bearer Authorization: `YES`
- Input Data:
  - `opDescription`: objective problem description. 客观题题目描述。
  - `opTotalScore`: objective problem total score. 客观题总分。
  - `opCorrectAnswer`: objective problem correct answer. 客观题正确答案。
  - `opTag`: objective problem tag for categorization. 客观题分类标签。
  - `opDifficulty`: objective problem difficulty. 客观题难度等级。
- Response:
  - `error_message`: If there is no problem with the submitted parameters and access permissions, `success` will be returned. Otherwise, error information related to the request will be returned. 如果提交的参数和访问的权限没有问题，就会返回`success`。否则，返回和请求相关的错误信息。
  - `objective_problem_id`: The id of the newly created objective problem, coresponding to `objective_problem_id` in the table `objective_problem`. 新创建的客观题id。
- Comment: Only teachers and administrators can create objective problems. Make sure to check the length of submitted parameters against the length of the field in the table `objective_problem`. 只有教师和管理员可以创建客观题。注意检查提交的参数长度符合数据表的定义。

## 2. Delete Objective Problems

- URL: `/problem_manage/objective_problem_manage/`
- Type: `DELETE`
- Bearer Authorization: `YES`
- Input Data:
  - `objectiveProblemId`: the id of the objective problem that you want to delete. 需要删除的客观题id。
- Response:
  - `error_message`: If there is no problem with the submitted parameters and access permissions, `success` will be returned. Otherwise, error information related to the request will be returned. 如果提交的参数和访问的权限没有问题，就会返回`success`。否则，返回和请求相关的错误信息。
- Comment: Only teachers and administrators can delete objective problems. Teachers can delete objective problems created by themselves. Administrators can delete objective problems created by anyone. 只有教师和管理员可以删除客观题。教师只能删除自己创建的客观题，管理员可以删除任何人创建的客观题。

## 3. Update Objective Problems

- URL: `/problem_manage/objective_problem_manage/`
- Type: `PUT`
- Bearer Authorization: `YES`
- Input Data:
  - `objectiveProblemId`: the id of the objective problem that you want to update. 需要更新的客观题id。
  - `opDescription`: the new objective problem description. 新的客观题题目描述。
  - `opTotalScore`: the new objective problem total score. 新的客观题总分。
  - `opCorrectAnswer`: the new objective problem correct answer. 新的客观题正确答案。
  - `opTag`: the new objective problem tag for categorization. 新的客观题分类标签。
  - `opDifficulty`: the new objective problem difficulty. 新的客观题难度等级。
- Response:
  - `error_message`: If there is no problem with the submitted parameters and access permissions, `success` will be returned. Otherwise, error information related to the request will be returned. 如果提交的参数和访问的权限没有问题，就会返回`success`。否则，返回和请求相关的错误信息。
- Comment: 
  - Only teachers and administrators can update objective problems. 只有教师和管理员可以修改客观题。
  - Teachers can delete objective problems created by themselves. 教师只能修改自己创建的客观题。
  - Administrators can delete objective problems created by anyone. 管理员可以修改任何人创建的客观题。
  - Unchanged parameters should also be sent as input data. Just keep them as their unchanged value. 未更改的参数也是必须的，保持未更改的参数作为输入参数即可。
  - Make sure to check the length of submitted parameters against the length of the field in the table `objective_problem`. 注意检查提交的参数长度符合数据表的定义。

## 4. Get a List of Objective Problems (Get All)

- URL: `/problem_manage/objective_problem_manage/all/`

- Type: `GET`

- Bearer Authorization: `YES`

- Input Data: `None`

- Response: `A List of All Objective Problems` like this:

  ```json
  [
      {
          "op_description": "下图给出的网络从$s$到$t$的最大流是：\n\n![img](https://images.ptausercontent.com/118)\n\nA. 13\n\nB. 14\n\nC. 18\n\nD. 11...",
          "objective_problem_id": "1",
          "op_use_count": "0",
          "op_author_name": "李四(pyxc)",
          "op_difficulty": "5",
          "op_tag": "图论",
          "op_total_score": "8"
      },
      {
          "op_description": "下图给出的网络从$s$到$t$的最大流是：\n\n![img](https://images.ptausercontent.com/264)\n\nA. 119\n\nB. 105\n\nC. 95\n\nD. 89...",
          "objective_problem_id": "2",
          "op_use_count": "1",
          "op_author_name": "李四(pyxc)",
          "op_difficulty": "5",
          "op_tag": "图论",
          "op_total_score": "8"
      },
      {
          "op_description": "**判断题**\n\n最优二叉搜索树的根结点一定存放的是搜索概率最高的那个关键字。 \n\n填写`T/F`...",
          "objective_problem_id": "3",
          "op_use_count": "2",
          "op_author_name": "李四(pyxc)",
          "op_difficulty": "3",
          "op_tag": "数据结构",
          "op_total_score": "8"
      }
  ]
  ```

  

- Comment: 这个API返回的题目描述`op_description`是截短的，超过长度的部分用`...`表示。这个API在异常情况下才会返回`error_message`，正常情况下只会返回一个列表。`op_use_count`表示出题次数，可能需要使用到额外的数据表`op_n_ps`，`n`的含义表示`and`。

## 5. Get One Objective Problem (Get One)

- URL: `/problem_manage/objective_problem_manage/`
- Type: `GET`
- Bearer Authorization: `YES`
- Input Data:
  - `objectiveProblemId`: the id of the objective problem that you want to get. 需要获取信息的客观题id。
- Response:
  - `error_message`: If there is no problem with the submitted parameters and access permissions, `success` will be returned. Otherwise, error information related to the request will be returned. 如果提交的参数和访问的权限没有问题，就会返回`success`。否则，返回和请求相关的错误信息。
  - `objective_problem_id`: the id of the objective problem that you want to get. 需要获取信息的客观题id。
  - `op_use_count`: the use count of the objective problem. 客观题出题次数。
  - `op_author_id`: the id of the creator of the objective problem. 客观题作者id。
  - `op_author_name`: the name of the creator of the objective problem. 客观题作者姓名。
  - `op_description`: objective problem description. 客观题题目描述。
  - `op_total_score`: objective problem total score. 客观题总分。
  - `op_correct_answer`: objective problem correct answer. 客观题正确答案。
  - `op_tag`: objective problem tag for categorization. 客观题分类标签。
  - `op_difficulty`: objective problem difficulty. 客观题难度等级。
- Comment: 不同于Get All，这个API返回的题目描述`op_description`是完整的。`op_use_count`表示出题次数，可能需要使用到额外的数据表`op_n_ps`，`n`的含义表示`and`。