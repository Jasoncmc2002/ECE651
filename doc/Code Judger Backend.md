Code Judger Backend

# User Story Related

As a student,

I want to test my programming codes before submitting my answers,

so that I can check my answers for the programming problems.

As a teacher,

I want the system to automatically grade students' programming problem answers based on test cases,

so that grading on such problems would be less error-prone.

# Database Structure

A database is not required for this function.

# Functional Requirements

To better design the judger, we need to specify what the judger can do. 

- The judger should be able to run the code with the given input and collect the output. Note that comparing output against the test case expected output is not part of the judger's requirements, but the requirement of students submitting their answers.
- The judger should detect syntax errors and run-time errors, giving proper feedback.
- The judger should terminate the code after a certain timeout. This is especially important for a code that never terminates.
- The judger should not let the code access system privilege operations, such as shut down the system. 

# System Design

The following is the sequential diagram of the judger's design. The related Java classes and the interaction between them are shown in the diagram.

![image-20250201144019404](Code%20Judger%20Backend.assets/image-20250201144019404.png)

Note: we use Jython to run Python codes on JVM. JVM does not have system privileges which solve the code-access-system-privilege-operation issue.

# API Requirements

**Note 1: 大家API测试 (Integration Test) 时请把结果截图放在issue的comment中**

**Note 2: 大家记得写一些Unit Test，放在`scr/test/`文件夹中**

**Note 3: 需要验证的API都是Bearer验证，前端Ajax请求例子如下**

![Screenshot 2025-02-01 at 2.44.26 PM](Code%20Judger%20Backend.assets/Screenshot%202025-02-01%20at%202.44.26%E2%80%AFPM.png)

| 请求的Key | 含义                                                         |
| --------- | ------------------------------------------------------------ |
| url       | 访问API的地址                                                |
| type      | 请求类型，可以是GET，POST，PUT，DELETE。                     |
| data      | 请求发送的数据，如果API不需要输入数据，则没有这一项          |
| headers   | 请求的附带验证信息，如果API不需要验证，则没有这一项          |
| success   | 请求成功返回的前端处理逻辑（回调函数），几乎所有的API都会返回一个`error_message`告诉前端处理请求的结果 |

## 1. Special Judge Submission

- URL: `/judge/special_judge/`
- Type: `POST`
- Bearer Authorization: `YES`
- Input Data:
  - `code`: student code that is submitted to run. Note: don't need to worry about the tabs and new lines; they are formatted.
  - `testInput`: input that is given to this special judge run
  - `timeLimit`: time limitation of running the code. Unit: ms
- Response:
  - `error_message`: If there is no problem with the submitted parameters and access permissions, `success` will be returned. Otherwise, error information related to the request will be returned. 如果提交的参数和访问的权限没有问题，就会返回`success`。否则，返回和请求相关的错误信息。
  - `test_output`: If the program runs normally, the output of the program is returned. If the program has a runtime error, including a syntax error, the error content is returned. If the program times out, the program timeout is returned. 如果程序正常运行，返回程序运行的输出。如果程序有运行时错误，包括语法错误，返回错误内容。如果程序运行超时，则返回程序运行超时。
- Comment: This API is currently only available to administrators and is the administrator's debugging port. In the future, when developing student answering, we will implement an API that students can use. 此API目前是只有管理员可以使用，是管理员的调试端口，未来开发学生答题时，再实现学生可以使用的API。