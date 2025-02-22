Programming Problem and Test Case Management

# User Story Related

As a teacher,

I want to manage programming problems,

so that I can create, edit, delete programming problems.

As a teacher,

I want to manage test cases of programming problems,

so that I can create, delete test cases of programming problems.

As a teacher,

I want to try out test cases of programming problems,

so that my created test cases are correct. (Note: already complete by the code judger sprint development)

# Database Structure

**Table name: `programming`**

| **Field**       | **Type**                    | **Constrain** | **Field Comments**             |
| --------------- | --------------------------- | ------------- | ------------------------------ |
| programming_id  | Integer                     | PK            | 编程题实体主键                 |
| user_id         | Integer                     | FK            | 编程题作者ID                   |
| p_title         | Variable characters (100)   | Not Null      | 编程题标题                     |
| p_description   | Variable characters (10000) | Not Null      | 编程题题干                     |
| p_total_score   | Integer                     | Not Null      | 编程题总分                     |
| time_limit      | Integer                     | Not Null      | 编程题运行时间限制，单位：毫秒 |
| code_size_limit | Integer                     | Not Null      | 编程题代码长度限制，单位：KB   |
| p_tag           | Variable characters (100)   | Not Null      | 编程题标签                     |
| p_difficulty    | Integer                     | Not Null      | 编程题难度系数                 |
| p_judge_code    | Text                        |               | 函数编程题测评代码             |

**Table name: `test_case`**

| **Field**      | **Type**                   | **Constrain** | **Field Comments** |
| -------------- | -------------------------- | ------------- | ------------------ |
| test_case_id   | Integer                    | PK            | 测试用例实体主键   |
| programming_id | Integer                    | FK            | 测试用例所属编程题 |
| tc_input       | Variable characters (1024) | Not Null      | 测试用例输入       |
| tc_output      | Variable characters (1024) | Not Null      | 测试用例期待输出   |

## `p_judge_code` Code Completion Judge Code Explained 函数编程题测评代码说明

`programming`数据表中`p_judge_code`用于函数编程题的测评。函数编程题是一种只要求写片段代码的编程题，通过规定函数的输入参数和输出参数，让学生只写一个函数代码片段，从而节省学生处理输入数据解析(parse)和输出数据格式的工作；让学生能够专注于函数算法的设计，省略了输入输出的设计。

一道函数编程题的例子如下：

> 本题要求实现函数`f(list: l)`，输入一个列表`list`，求这列表中所有数字的和是多少，并将所有数字的的和返回。
>
> #### 输入样例
>
> ```in
> [1, 2, 3, 4]
> ```
>
> #### 输出样例
>
> ```out
> 10
> ```

对于这道函数编程题，学生只需要实现一个列表中数字求和函数，学生不需要写代码将输入字符串`'[1, 2, 3, 4]'`转换为列表，比如：

```python
def add(l: list):
    a = 0
    for i in l:
        a += i
    return a
```

处理解析parse输入和格式化输出的代码由教师完成，这些代码被称为**函数题测评代码**，存储在`p_judge_code`字段中。在测评时，学生的函数代码会通过字符串合并的方式，放在教师的函数题测评代码**上方**，再提交测评机。比如，教师的测评代码可以是这样：

```python
str = input()
l = list(map(int, str[1:-1].split(',')))
print(add(l))
```

在测评时，学生的代码直接字符串连接在测评代码上方，提交给测评机运行的代码如下：

```python
def add(l: list):
    a = 0
    for i in l:
        a += i
    return a

str = input()
l = list(map(int, str[1:-1].split(',')))
print(add(l))
```

提交给测评机运行的代码是完整的，包含处理输入输出和算法的代码，测评过程和普通的编程题一样，可以和普通编程题共用测评机。

# Functional Requirements

- There should be a listing page that lists all programming problems in the database.
- The UI should have a paging function to show the list of all programming problems.

# Quality Requirements

- There should be authentication when using the programming problem management: no access for students, access to owned problems for teachers (can only access problems created by themselves), full access to any problems for administrators. "Access" means edit and delete.
- There should be a main page for both objective and programming problems sub-pages, like a fork in the road. Sub-pages should have a similar UI design, easy to switch between them like using tabs on Chrome.

# API Used

**Note 1: 大家API测试 (Integration Test) 时请把结果截图放在issue的comment中**

**Note 2: 大家记得写一些Unit Test，放在`scr/test/`文件夹中**

**Note 3: 需要验证的API都是Bearer验证，前端Ajax请求例子如下**

![Screenshot 2025-02-13 at 2.31.05 PM](Programming%20Problem%20and%20Test%20Case%20Management%20Backend.assets/Screenshot%202025-02-13%20at%202.31.05%E2%80%AFPM.png)

| 请求的Key | 含义                                                         |
| --------- | ------------------------------------------------------------ |
| url       | 访问API的地址                                                |
| type      | 请求类型，可以是GET，POST，PUT，DELETE。                     |
| data      | 请求发送的数据，如果API不需要输入数据，则没有这一项          |
| headers   | 请求的附带验证信息，如果API不需要验证，则没有这一项          |
| success   | 请求成功返回的前端处理逻辑（回调函数），几乎所有的API都会返回一个`error_message`告诉前端处理请求的结果 |

## 1. Create Programming Problems

- URL: `/problem_manage/programming_manage/`
- Type: `POST`
- Bearer Authorization: `YES`
- Input Data:
  - `pDescription`: programming problem description. 编程题题目描述。
  - `pTotalScore`: programming problem total score. 编程题总分。
  - `timeLimit`: programming problem running time limit, unit: ms. 编程题运行时间限制，单位：毫秒。
  - `codeSizeLimit`: programming problem answer code size limit, unit: KB. 编程题的作答代码长度限制，单位：KB。
  - `pTag`: programming problem tag for categorization. 编程题分类标签。
  - `pTitle`: programming problem title. 编程题标题。
  - `pJudgeCode`: function programming problem judge code. 函数题测评代码，如果是普通编程题则为空。
  - `pDifficulty`: programming problem difficulty. 编程题难度等级。
- Response:
  - `error_message`: If there is no problem with the submitted parameters and access permissions, `success` will be returned. Otherwise, error information related to the request will be returned. 如果提交的参数和访问的权限没有问题，就会返回`success`。否则，返回和请求相关的错误信息。
  - `programming_id`: The id of the newly created programming problem, coresponding to `programming_id` in the table `programming`. 新创建的编程题id。
- Comment: Only teachers and administrators can create programming problems. Make sure to check the length of submitted parameters against the length of the field in the table `programming`. 只有教师和管理员可以创建编程题。注意检查提交的参数长度符合数据表的定义。

## 2. Delete Programming Problems

- URL: `/problem_manage/programming_manage/`
- Type: `DELETE`
- Bearer Authorization: `YES`
- Input Data:
  - `programmingId`: the id of the programming problem that you want to delete. 需要删除的编程题id。
- Response:
  - `error_message`: If there is no problem with the submitted parameters and access permissions, `success` will be returned. Otherwise, error information related to the request will be returned. 如果提交的参数和访问的权限没有问题，就会返回`success`。否则，返回和请求相关的错误信息。
- Comment: Only teachers and administrators can delete programming problems. Teachers can delete programming problems created by themselves. Administrators can delete programming problems created by anyone. 只有教师和管理员可以删除编程题。教师只能删除自己创建的编程题，管理员可以删除任何人创建的编程题。

## 3. Update Programming Problems

- URL: `/problem_manage/programming_manage/`
- Type: `PUT`
- Bearer Authorization: `YES`
- Input Data:
  - `programmingId`: the id of the programming problem that you want to update. 需要更新的编程题id。
  - `pDescription`: the new programming problem description. 新的编程题题目描述。
  - `pTotalScore`: the new programming problem total score. 新的编程题总分。
  - `timeLimit`: the new programming problem running time limit, unit: ms. 新的编程题运行时间限制，单位：毫秒。
  - `codeSizeLimit`: the new programming problem answer code size limit, unit: KB. 新的编程题的作答代码长度限制，单位：KB。
  - `pTag`: the new programming problem tag for categorization. 新的编程题分类标签。
  - `pTitle`: the new programming problem title. 新的编程题标题。
  - `pJudgeCode`: the new function programming problem judge code. 新的函数题测评代码，如果是普通编程题则为空。
  - `pDifficulty`: the new programming problem difficulty. 新的编程题难度等级。
- Response:
  - `error_message`: If there is no problem with the submitted parameters and access permissions, `success` will be returned. Otherwise, error information related to the request will be returned. 如果提交的参数和访问的权限没有问题，就会返回`success`。否则，返回和请求相关的错误信息。
- Comment: 
  - Only teachers and administrators can update programming problems. 只有教师和管理员可以修改编程题。
  - Teachers can delete programming problems created by themselves. 教师只能修改自己创建的编程题。
  - Administrators can delete programming problems created by anyone. 管理员可以修改任何人创建的编程题。
  - Unchanged parameters should also be sent as input data. Just keep them as their unchanged value. 未更改的参数也是必须的，保持未更改的参数作为输入参数即可。
  - Make sure to check the length of submitted parameters against the length of the field in the table `programming`. 注意检查提交的参数长度符合数据表的定义。

## 4. Get a List of Programming Problems (Get All)

- URL: `/problem_manage/programming_manage/all/`

- Type: `GET`

- Bearer Authorization: `YES`

- Input Data: `None`

- Response: `A List of All Programming Problems` like this:

  ```json
  [
      {
          "programming_id": "1",
          "p_title": "L1-010 比较大小",
          "p_total_score": "20",
          "p_difficulty": "5",
          "p_tag": "函数",
          "p_use_count": "2",
          "p_author_name": "李四(pyxc)"
      },
      {
          "programming_id": "2",
          "p_title": "L1-001 Hello World",
          "p_total_score": "15",
          "p_difficulty": "1",
          "p_tag": "基本语法",
          "p_use_count": "3",
          "p_author_name": "李四(pyxc)"
      },
      {
          "programming_id": "3",
          "p_title": "L1-015 跟奥巴马一起画方块",
          "p_total_score": "25",
          "p_difficulty": "5",
          "p_tag": "控制语句",
          "p_use_count": "1",
          "p_author_name": "李四(pyxc)"
      }
  ]
  ```

- Comment: 这个API在异常情况下才会返回`error_message`，正常情况下只会返回一个列表。`p_use_count`表示出题次数，可能需要使用到额外的数据表`p_n_ps`，`n`的含义表示`and`。

## 5. Get One Programming Problem (Get One)

- URL: `/problem_manage/programming_manage/`
- Type: `GET`
- Bearer Authorization: `YES`
- Input Data:
  - `programmingId`: the id of the programming problem that you want to get. 需要获取信息的编程题id。
- Response:
  - `error_message`: If there is no problem with the submitted parameters and access permissions, `success` will be returned. Otherwise, error information related to the request will be returned. 如果提交的参数和访问的权限没有问题，就会返回`success`。否则，返回和请求相关的错误信息。
  - `programming_id`: the id of the programming problem that you want to get. 需要获取信息的编程题id。
  - `p_use_count`: the use count of the programming problem. 编程题出题次数。
  - `p_description`: programming problem description. 编程题题目描述。
  - `p_total_score`: programming problem total score. 编程题总分。
  - `time_limit`: programming problem running time limit, unit: ms. 编程题运行时间限制，单位：毫秒。
  - `code_size_limit`: programming problem answer code size limit, unit: KB. 编程题的作答代码长度限制，单位：KB。
  - `p_tag`: programming problem tag for categorization. 编程题分类标签。
  - `p_author_id`: the id of the creator of the programming problem. 编程题作者id。
  - `p_author_name`: the name of the creator of the programming problem. 编程题作者姓名。
  - `p_title`: programming problem title. 编程题标题。
  - `p_judge_code`: function programming problem judge code. 函数题测评代码，如果是普通编程题则为空。
  - `p_difficulty`: programming problem difficulty. 编程题难度等级。
- Comment: `p_use_count`表示出题次数，可能需要使用到额外的数据表`p_n_ps`，`n`的含义表示`and`。

## 6. Create Test Cases

- URL: `/problem_manage/test_case_manage/`
- Type: `POST`
- Bearer Authorization: `YES`
- Input Data:
  - `programmingId`: the id of the programming problem that you want to create test case for. 需要创建测试用例的编程题ID。
  - `tcInput`: test case input. 测试用例输入。
  - `tcOutput`: test case expected output. 测试用例期望输出。
  - `respId`: flag for whether the backend should return the test case id or not. `respId='yes'` for return the id, `respId='no'`, `respId=NULL` or `respId='any other strings'` for not return the id. 是否返回新建测试用例的id，`yes`则后端会返回新建测试用例的id，`no`、`NULL`或者其他字符串则不会返回新建测试用例的id。
- Response:
  - `error_message`: If there is no problem with the submitted parameters and access permissions, `success` will be returned. Otherwise, error information related to the request will be returned. 如果提交的参数和访问的权限没有问题，就会返回`success`。否则，返回和请求相关的错误信息。
  - `test_case_id`: The id of the newly created test case, coresponding to `test_case_id` in the table `test_case`. Only returned when `respId='yes'`.  新创建的测试用例id，只在`respId='yes'`时返回新创建的测试用例id。
- Comment: 
  - Only teachers and administrators can create test cases. 只有教师和管理员可以创建测试用例。
  - Teachers can create test cases to the programming problems they created. Consider creating test cases as a modification to the programming problems. 教师只能向自己创建的编程题添加测试用例，添加测试用例类似于修改编程题，修改编程题的权限规则适用于添加测试用例。
  - Administrators can create test cases to any programming problems. 管理员可以向任何编程题添加测试用例。
  - Make sure to check the length of submitted parameters against the length of the field in the table `test_case`. 注意检查提交的参数长度符合数据表的定义。

## 7. Delete Test Cases

- URL: `/problem_manage/test_case_manage/`
- Type: `DELETE`
- Bearer Authorization: `YES`
- Input Data:
  - `testCaseId`: the id of the test case that you want to delete. 需要删除的测试用例ID。
- Response:
  - `error_message`: If there is no problem with the submitted parameters and access permissions, `success` will be returned. Otherwise, error information related to the request will be returned. 如果提交的参数和访问的权限没有问题，就会返回`success`。否则，返回和请求相关的错误信息。
- Comment: 
  - Only teachers and administrators can delete test cases. 只有教师和管理员可以删除测试用例。
  - Teachers can delete test cases from the programming problems they created. Consider deleting test cases as a modification to the programming problems. 教师只能向自己创建的编程题删除测试用例，删除测试用例类似于修改编程题，修改编程题的权限规则适用于删除测试用例。
  - Administrators can delete test cases from any programming problems. 管理员可以向任何编程题删除测试用例。

## 8. Get All Test Cases by Programming ID

- URL: `/problem_manage/test_case_manage/by_programming_id/`

- Type: `GET`

- Bearer Authorization: `YES`

- Input Data:

  - `programmingId`: the id of the programming problem from which test cases you want to get. 需要获取所有测试用例的编程题ID。

- Response: `A List of All Test Cases` like this:

  ```json
  [
      {
          "test_case_id": "28",
          "programming_id": "5",
          "tc_input": "3 4",
          "tc_output": "7"
      },
      {
          "test_case_id": "29",
          "programming_id": "5",
          "tc_input": "1 2",
          "tc_output": "3"
      },
      {
          "test_case_id": "30",
          "programming_id": "5",
          "tc_input": "0 0",
          "tc_output": "0"
      }
  ]
  ```

- Comment: 这个API在异常情况下才会返回`error_message`，正常情况下只会返回一个列表。

# Additional Notes

题目集编程题信息数据表`p_n_ps`: 
题目集编程题信息数据表用于记录编程题属于某个题目集的信息，当编程题被添加到题目集中时，将会创建这条记录，该记录包含编程题id和题目集id。

