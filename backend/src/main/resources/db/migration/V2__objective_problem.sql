create table objective_problem
(
    objective_problem_id int auto_increment
        primary key,
    author_id            int                           not null,
    op_description       varchar(10000)                not null,
    op_total_score       int                           not null,
    op_correct_answer    varchar(1024)                 not null,
    op_tag               varchar(100) default '无分类' not null,
    op_difficulty        int          default 1        not null comment '难度系数',
    constraint objective_problem_user_user_id_fk
        foreign key (author_id) references user (user_id)
            on update cascade on delete cascade
);

INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (1, 2, '下图给出的网络从$s$到$t$的最大流是：

![img](https://images.ptausercontent.com/118)

A. 13

B. 14

C. 18

D. 11', 8, 'A', '图论', 5);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (2, 2, '下图给出的网络从$s$到$t$的最大流是：

![img](https://images.ptausercontent.com/264)

A. 119

B. 105

C. 95

D. 89', 8, 'C', '图论', 5);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (3, 2, '**判断题**

最优二叉搜索树的根结点一定存放的是搜索概率最高的那个关键字。 

填写`T/F`', 8, 'F', '数据结构', 3);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (4, 2, '**单选题**

下列哪个函数是$O(N)$的？

A. $(logN)^2$

B. $(NlogN) / 1000$

C. $N(logN)^2$

D. $N^2 / 1000$', 5, 'A', '时间复杂度', 1);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (5, 2, '**单选题**

下列代码

```c++
if ( A > B ) {
    for ( i=0; i<N; i++ )
        for ( j=N*N; j>i; j-- )
            A += B;
}
else {
    for ( i=0; i<N*2; i++ )
        for ( j=N*2; j>i; j-- )
            A += B;
}
```

的时间复杂度是：

A. $O(N)$

B. $O(N^2)$

C. $O(N^3)$

D. $O(N^4)$', 8, 'C', '时间复杂度', 1);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (6, 2, '**判断题**

用动态规划而非递归的方法去解决问题时，关键是将子问题的计算结果保存起来，使得每个不同的子问题只需要被计算一次。子问题的解可以被保存在数组或哈希散列表中。

填写`T/F`', 5, 'T', '动态规划', 4);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (7, 1, '`Python`不支持的数据类型有（ A ）。

A. `char` B. `int`

C. `float` D. `list`', 5, 'A', '语法', 1);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (8, 2, '下列哪个语句在`Python`中是非法的？（ B ）。

A. `x = y = z = 1`

B. `x = (y = z + 1)`

C. `x, y = y, x` 

D. `x += y`', 5, 'B', '语法', 1);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (9, 2, '下面哪个不是`Python`合法的标识符（ B ）。

A. `int32` B. `40XL`

C. `self` D. `__name__`', 8, 'B', '语法', 2);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (10, 2, '下列哪种说法是错误的（ A ）。

A. 除字典类型外，所有标准对象均可以用于布尔测试

B. 空字符串的布尔值是`False`

C. 空列表对象的布尔值是`False`

D. 值为0的任何数字对象的布尔值是`False`', 8, 'A', '语法', 2);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (11, 7, '以下关于模块说法错误的是（ C ）。

A. 一个`xx.py`就是一个模块

B. 任何一个普通的`xx.py`文件可以作为模块导入

C. 模块文件的扩展名不一定是`.py`

D. 运行时会从制定的目录搜索导入的模块，如果没有，会报错异常', 5, 'C', '模块', 3);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (12, 7, '程序如下：

```python
try:
	number = int(input("请输入数字："))
	print("number:",number)
	print("=======hello======")
except Exception as e:
	#  报错错误日志
	print("打印异常详情信息： ",e)
else:
	print("没有异常")
finally:
	# 关闭资源
	print("finally")
print("end")
```

输入的是 1a 结果是：（ B ）。

A. `number: 1` 打印异常详情信息： `invalid literal for int() with base 10: finally end`

B. 打印异常详情信息： `invalid literal for int() with base 10: finally end`

C. `========hello=========== `打印异常详情信息： `invalid literal for int() with base 10: finally end`

D. 以上都正确', 8, 'B', '异常处理', 5);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (13, 7, '对以下程序描述错误的是（ A ）。

```python
try:
	# 语句块1
except IndexError as i:
	# 语句块2
```

A. 改程序对异常处理了，因此一定不会终止程序

B. 改程序对异常处理了，不一定不会因异常引发终止

C. 语句块1，如果抛出`IndexError `异常，不会因为异常终止程序

D. 语句块2 不一定会执行', 5, 'A', '异常处理', 3);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (14, 7, '有关异常说法正确的是（ B ）。

A. 程序中抛出异常终止程序

B. 程序中抛出异常不一定终止程序

C. 拼写错误会导致程序终止

D. 缩进错误会导致程序终止', 5, 'B', '异常处理', 2);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (15, 7, '以下哪项Python能正常启动（ D ）。

A. 拼写错误 

B. 错误表达式

C. 缩进错误 

D. 手动抛出异常', 5, 'D', '异常处理', 1);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (16, 7, '定义类如下：

```python
class A():
	def a():
		print("a")
class B ():
	def b():
		print("b")
class C():
	def c():
		print(c)
class D(A,C):
	def d():
		print("d")

d = D()
d.a()
d.b()
d.d()
```

以下程序能执行的结果是（ D ）。

A. a,b,d 

B. a,d

C. d,a 

D. 执行会报错', 5, 'D', '异常处理', 4);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (17, 7, '定义类如下：

```python
class Hello(object):
	def __init__(self,name)
		self.name=name
	def showInfo(self)
		print(self.name)
```

下面代码能正常执行的（ C ）。

A.

```python
h = Hello
h.showInfo()
```


B. 

```python
h = Hello()
h.showInfo("张三")
```

C. 

```python
h = Hello("张三")
h.showInfo()
```

D. 

```python
h = Hello("admin")
showInfo
```', 8, 'C', '类和对象', 5);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (18, 7, '关于Python类说法错误的是（ B ）。

A. 类的实例方法必须创建对象后才可以调用

B. 类的实例方法必须创建对象前才可以调用

C. 类的类方法可以用对象和类名来调用

D. 类的静态属性可以用类名和对象来调用', 5, 'B', '类和对象', 1);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (19, 7, '定义类如下：

```python
class Hello():
	def showInfo(self):
		print(self.x)
```

下面描述正确的是（ C ）。

A. 这个类的语法有问题

B. 该类可以实例化

C. 在`pycharm`工具中会出现语法错误，说`self`没有定义

D. 该类可以实例化，并且能正常通过对象调用`showInfo()`', 5, 'C', '类和对象', 1);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (20, 7, '定义类如下：

```python
class Hello(object):
	pass
```

下面说明错误的是（ D ）。

A. 该类实例中包含`__dir__()`方法

B. 该类实例中包含`__hash__()`方法

C. 该类实例中包含`__dir__()`，还包含`__hash__()`

D. 该类没有定义任何方法，所以该实例中没有包含任何方法', 8, 'D', '类和对象', 4);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (21, 7, '函数如下：

```python
def chanageList(list):
	list.append("end")
	print("list",list)
# 调用
strs =["1","2"]
chanageList(strs)
print("strs",strs)
```

下面对 `strs` 和 `list` 的值输出正确的是（ C ）。

A. `strs ["1","2"]` 

B. `list["1","2"]`

C. `list ["1","2","end"]` 

D. `strs ["1","2","end","1","2"]`', 8, 'C', '函数', 5);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (22, 7, '函数如下：

```python
def chanageInt(number2):
	number2 = number2+1
	print("changeInt: number2= ",number2)

# 调用
number1 = 2
chanageInt(number1)
print("number:",number1)
```

打印结果哪项是正确的（ B ）。

A. `changeInt: number2= 3 number: 3`

B. `changeInt: number2= 3 number: 2`

C. `number: 2 changeInt: number2= 2`

D. `number: 2 changeInt: number2= 3`', 8, 'B', '函数', 4);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (23, 7, '函数如下：

```python
def showNnumber(numbers):
	for n in numbers:
		print(n)
```

下面那些在调用函数时会报错（ C ）。

A. `showNumer([2,4,5])` 

B. `showNnumber("abcesf")`

C. `showNnumber(3.4)` 

D. `showNumber((12,4,5))`', 5, 'C', '函数', 3);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (24, 7, '调用以下函数返回的值（ D ）。

```python
def myfun():
	pass
```

A. `0 `

B. 出错不能运行

C. 空字符串 

D. 无', 5, 'D', '函数', 2);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (25, 7, '以下不是Python中的关键字（ D ）。

A. `raise` 

B. `with`

C. `import` 

D. `final`', 5, 'D', '语法', 2);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (26, 7, '如下代码，打印的结果是（ B ）。

```python
str1 = "Runoob example....wow!!!"
str2 = "exam";
print(str1.find(str2, 5)) 
```

A. `6 `

B. `7`

C. `8 `

D. `-1`', 5, 'B', '语法', 2);
