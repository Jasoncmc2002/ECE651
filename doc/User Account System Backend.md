# User Story Related

As a general user,

I want to have access authentication and manage my user information,

so that I can log in, log out, register and change basic user information, i.e., my name, username, photo and password.



As a user administrator,

I want to have user administration,

so that I can change other users' information, delete users and import users.

# Database Structure

**Table name: `user`**

| **Field**  | **Type**                   | **Constrain** | **Field Comments**                          |
| ---------- | -------------------------- | ------------- | ------------------------------------------- |
| user_id    | Integer                    | PK            | 用户实体主键                                |
| username   | Variable  characters (100) | Not Null      | 用户名，一般为数字和/或字母组合             |
| name       | Variable  characters (100) | Not Null      | 用户真实姓名                                |
| password   | Variable  characters (100) | Not Null      | 密码，以密文方式存储                        |
| permission | Integer                    | Not Null      | 用户权限，0表示学生，1表示教师，2表示管理员 |
| photo      | Text                       |               | 用户头像数据，以Base64编码存储，不超过50KB  |

# API Requirements

**Note 1: 大家API测试 (Integration Test) 时请把结果截图放在issue的comment中**

**Note 2: 大家记得写一些Unit Test，放在`scr/test/`文件夹中**

**Note 3: 需要验证的API都是Bearer验证，前端Ajax请求例子如下**

![Screenshot 2025-01-25 at 10.37.03 AM](User%20Account%20System%20Backend.assets/Screenshot%202025-01-25%20at%2010.37.03%E2%80%AFAM.png)

| 请求的Key | 含义                                                         |
| --------- | ------------------------------------------------------------ |
| url       | 访问API的地址                                                |
| type      | 请求类型，可以是GET，POST，PUT，DELETE。                     |
| data      | 请求发送的数据，如果API不需要输入数据，则没有这一项          |
| headers   | 请求的附带验证信息，如果API不需要验证，则没有这一项          |
| success   | 请求成功返回的前端处理逻辑（回调函数），几乎所有的API都会返回一个`error_message`告诉前端处理请求的结果 |

## 1. User Login: Obtain JSON Web Token（JWT）

- URL: `/user/account/token/`
- Type: `POST`
- Bearer Authorization: `NO`
- Input Data:
  - `username`: username
  - `password`: password
- Response:
  - `error_message`: 只有成功会返回`success`
  - `token`: JWT token，有效期14天
- Comment：只有用户名密码正确才会获得返回数据，任何一个不正确，将会收到403 Forbidden。

## 2. User Registration

- URL: `/user/account/register/`
- Type: `POST`
- Bearer Authorization: `NO`
- Input Data:
  - `username`: username, not empty, length less than 100
  - `password`: password, not empty, length less than 100
  - `confirmedPassword`: password confirmation, should match the password
- Response:
  - `error_message`: `success` or `reason for failure`.
- Comment：注册失败的原因可以是用户名或密码为空，用户名或密码长度超过100，两次输入密码不一致，用户名已存在。注册时，姓名是不需要输入的，系统会指定默认姓名，用户随后自行修改。

## 3. Get User Information

- URL: `/user/account/info/`
- Type: `GET`
- Bearer Authorization: `YES`
- Input Data: `None`
- Response:
  - `error_message`: `success`
  - `user_id`: the `user_id` value in the database table `user`
  - `name`: the `name` value in the database table `user`
  - `username`: the `username` value in the database table `user`
  - `permission`: the `permission` value in the database table `user`
- Comment：返回的`error_message`永远是success，因为请求的用户信息属于token的持有用户，不存在失败，除非是token过期，会返回403 forbidden。这个API是必要的，因为token中只有用户的id，没有其他的信息。

## 4. Get User Photo

- URL: `/user/account/photo/`
- Type: `GET`
- Bearer Authorization: `YES`
- Input Data: `None`
- Response:
  - `photo`: User photo in Base64 encoding, or an empty string if null stored in the field.
- Comment：返回用户的头像Base64编码的字符串，如果头像是空null，则返回空字符串。

## 5. Update User Information (Except Password)

- URL: `/user/account/update_user_info/`
- Type: `POST`
- Bearer Authorization: `YES`
- Input Data: 
  - `username`: the new  `username` value in the database table `user`
  - `name`: the new `name` value in the database table `user`
- Response:
  - `error_message`: `success` or `reason for failure`
- Comment：用户信息更新失败的原因可以是用户名或姓名为空，用户名或姓名长度超过100，用户名已存在。注意，姓名允许重复。如果只更新姓名或用户名的其中一个，未更改的输入参数也是必须的，保持未更改的参数作为输入参数即可。

## 6. Update User Password

- URL: `/user/account/update_password`
- Type: `POST`
- Bearer Authorization: `YES`
- Input Data:
  - `originalPassword`: the original password
  - `password`: new password, length less than 100
  - `confirmedPassword`: new password confirmation, should match the password
- Response:
  - `error_message`: `success` or `reason for failure`.
- Comment：密码更新失败的原因可以是原密码错误，新密码为空，新密码长度超过100，两次输入密码不一致。

## 7. Update User Photo

- URL: `/user/account/update_photo`
- Type: `PUT`
- Bearer Authorization: `YES`
- Input Data:
  - `photo`: the new photo in Base64 encoding, a size smaller than 50 KB
- Response:
  - `error_message`: `success` or `reason for failure`.
- Comment：头像更新失败的原因可以是图片Base64编码大小超过50KB。

## 8. Admin Update User Information (Except Password)

- URL: `/user/account/admin/user_info/`
- Type: `PUT`
- Bearer Authorization: `YES`
- Input Data: 
  - `userId`: the `user_id` of the user that you want to update, has to be an integer
  - `username`: the new `username` for the user, length less than 100
  - `name`: the new `name` for the user, length less than 100
  - `permission`: the `permission` for the user, `0` for students, `1` for teachers, `2` for administrators, other values are invalid 
- Response:
  - `error_message`: `success` or `reason for failure`
- Comment：管理员不可以通过这个API修改自己的信息，API需要检查空字符串和长度问题，通过ID检查需要修改的用户是否存在，权限值是否合法，新用户名是否存在等问题。

## 9. Admin Update User Password

- URL: `/user/account/admin/password/`
- Type: `PUT`
- Bearer Authorization: `YES`
- Input Data:
  - `userId`: the `user_id` of the user that you want to update, has to be an integer
  - `password`: new password, length less than 100
  - `confirmedPassword`: new password confirmation, should match the password
- Response:
  - `error_message`: `success` or `reason for failure`.
- Comment：管理员不可以通过这个API修改自己的密码，API需要检查空字符串和长度问题，通过ID检查需要修改的用户是否存在，两次输入密码是否一致的问题。

## 10. Admin Search User

- URL: `/user/account/admin/search`

- Type: `GET`

- Bearer Authorization: `YES`

- Input Data:

  - `username`: fuzzy search by username
  - `name`: fuzzy search by name

- Response: List of searched results, format like:

  ```JSON
  [
   { user_id: '4', name: 'bb', username: '04', permission: '0' },
   { user_id: '5', name: 'cc', username: '05', permission: '1' },
   { user_id: '6', name: 'dd', username: '06', permission: '2' },
  ]
  ```

- Comment：通过用户名和姓名模糊查找用户信息

## 11. Admin Delete User

- URL: `/user/account/admin/delete/`
- Type: `DELETE`
- Bearer Authorization: `YES`
- Input Data:
  - `userId`: the `user_id` of the user that you want to delete, has to be an integer
- Response:
  - `error_message`: `success` or `reason for failure`.
- Comment：管理员不可以通过这个API删除自己，API需要通过ID检查需要删除的用户是否存在。

## 12. Admin Import User

- URL: `/user/account/admin/batch_create/`
- Type: `POST`
- Bearer Authorization: `YES`
- Input Data:
  - `username`: username of the user that the admin wants to batch-create
  - `name`: name of the user that the admin wants to batch-create
  - `password`: password of the user that the admin wants to batch-create
  - `permission`:  permission of the user that the admin wants to batch-create
- Response:
  - `error_message`: `success` or `reason for failure`.
- Comment：此API用于管理员批量创建用户，前端会对每一个需要创建的用户调用这个API，创建用户。API会自动计算用户的ID，确保用户的ID是自动增加的。