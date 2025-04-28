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

INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (1, 2, 'The maximum flow from $s$ to $t$ in the network given in the figure below is:

![img](https://images.ptausercontent.com/118)

A. 13

B. 14

C. 18

D. 11', 8, 'A', 'Graph', 5);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (2, 2, 'The maximum flow from $s$ to $t$ in the network given in the figure below is:

![img](https://images.ptausercontent.com/264)

A. 119

B. 105

C. 95

D. 89', 8, 'C', 'Graph', 5);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (3, 2, '**Fill in `T/F`**

The root node of the optimal binary search tree must store the keyword with the highest search probability.', 8, 'F', 'DS', 3);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (4, 2, '**Single-choice question**

Which of the following functions is $O(N)$?

A. $(logN)^2$

B. $(NlogN) / 1000$

C. $N(logN)^2$

D. $N^2 / 1000$', 5, 'A', 'TE', 1);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (5, 2, '**Single-choice question**

The time efficiency of the following code is:

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

A. $O(N)$

B. $O(N^2)$

C. $O(N^3)$

D. $O(N^4)$', 8, 'C', 'TE', 1);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (6, 2, '**True or false**

When using dynamic programming instead of recursion to solve a problem, the key is to save the results of the subproblems so that each different subproblem only needs to be calculated once. The solutions to the subproblems can be saved in an array or a hash table.

Fill in `T/F`', 5, 'T', 'DP', 4);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (7, 1, 'The data types that `Python` does not support are (A).

A. `char` B. `int`

C. `float` D. `list`', 5, 'A', 'Syntax', 1);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (8, 2, 'Which of the following statements is illegal in `Python`? ( B ).

A. `x = y = z = 1`

B. `x = (y = z + 1)`

C. `x, y = y, x` 

D. `x += y`', 5, 'B', 'Syntax', 1);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (9, 2, 'Which of the following is not a legal identifier in `Python` (B).

A. `int32` B. `40XL`

C. `self` D. `__name__`', 8, 'B', 'Syntax', 2);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (10, 2, 'Which of the following statements is incorrect (A).

A. All standard objects except dictionary types can be used for Boolean tests

B. The Boolean value of an empty string is `False`

C. The Boolean value of an empty list object is `False`

D. The Boolean value of any numeric object with a value of 0 is `False`', 8, 'A', 'Syntax', 2);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (11, 5, 'The following statement about modules is wrong (C).

A. A `xx.py` is a module

B. Any ordinary `xx.py` file can be imported as a module

C. The extension of the module file is not necessarily `.py`

D. The runtime will search for the imported module from the specified directory, and if it is not found, an error will be reported.', 5, 'C', 'Class', 3);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (12, 5, 'The program is as follows:

```python
try:
    number = int(input("Please enter a number:"))
    print("number:",number)
    print("=======hello======")
except Exception as e:
    # Error log
    print("Print exception details: ",e)
else:
    print("No exception")
finally:
    # Close resources
    print("finally")
print("end")
```

The input is `1a` and the result is: (B).

A. `number: 1` prints exception details: `invalid literal for int() with base 10: finally end`

B. Prints exception details: `invalid literal for int() with base 10: finally end`

C. `=======hello===========` prints exception details: `invalid literal for int() with base 10: finally end`

D. All of the above are correct', 8, 'B', 'Exception', 5);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (13, 5, 'The wrong description of the following program is (A).

```python
try:
    # statement block 1
except IndexError as i:
    # statement block 2
```

A. The program handles the exception, so it will not terminate the program

B. The program handles the exception, but it may not terminate due to the exception

C. Statement block 1, if the `IndexError` exception is thrown, the program will not terminate due to the exception

D. Statement block 2 may not be executed', 5, 'A', 'Exception', 3);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (14, 5, 'The correct statement about exceptions is (B).

A. Throwing an exception in a program terminates the program

B. Throwing an exception in a program does not necessarily terminate the program

C. Spelling errors will cause the program to terminate

D. Indentation errors will cause the program to terminate', 5, 'B', 'Exception', 2);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (15, 5, 'Which of the following Python can start normally (D).

A. Spelling error

B. Wrong expression

C. Indentation error

D. Manually throw an exception', 5, 'D', 'Exception', 1);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (16, 5, 'Define the class as follows:

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

The result of executing the following program is (D).

A. a,b,d

B. a,d

C. d,a

D. Execution will result in an error', 5, 'D', 'Class', 4);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (17, 5, 'Define the class as follows:

```python
class Hello(object):
	def __init__(self,name)
		self.name=name
	def showInfo(self)
		print(self.name)
```

The following code can be executed normally (C).

A.

```python
h = Hello
h.showInfo()
```


B. 

```python
h = Hello()
h.showInfo("admin")
```

C. 

```python
h = Hello("admin")
h.showInfo()
```

D. 

```python
h = Hello("admin")
showInfo
```', 8, 'C', 'Class', 5);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (18, 5, 'The wrong statement about Python classes is (B).

A. Class instance methods can only be called after creating an object

B. Class instance methods can only be called before creating an object

C. Class methods can be called using objects and class names

D. Class static attributes can be called using class names and objects', 5, 'B', 'Class', 1);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (19, 5, 'Define the class as follows:

```python
class Hello():
	def showInfo(self):
		print(self.x)
```

The correct description is (C).

A. The syntax of this class is wrong

B. The class can be instantiated

C. A syntax error will appear in the `pycharm` tool, saying that `self` is not defined

D. The class can be instantiated and `showInfo()` can be called normally through the object', 5, 'C', 'Class', 1);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (20, 5, 'Define the class as follows:

```python
class Hello(object):
	pass
```

The following statement is wrong (D).

A. The class instance contains the `__dir__()` method

B. The class instance contains the `__hash__()` method

C. The class instance contains `__dir__()` and `__hash__()`

D. The class does not define any methods, so the instance does not contain any methods', 8, 'D', 'Class', 4);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (21, 5, 'The function is as follows:

```python
def chanageList(list):
	list.append("end")
	print("list",list)
# Call Function
strs =["1","2"]
chanageList(strs)
print("strs",strs)
```

The correct output for the values of `strs` and `list` is (C).

A. `strs ["1","2"]` 

B. `list["1","2"]`

C. `list ["1","2","end"]` 

D. `strs ["1","2","end","1","2"]`', 8, 'C', 'Function', 5);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (22, 5, 'The function is as follows:

```python
def chanageInt(number2):
	number2 = number2+1
	print("changeInt: number2= ",number2)

# Call Function
number1 = 2
chanageInt(number1)
print("number:",number1)
```

Which of the printed results is correct (B).

A. `changeInt: number2= 3 number: 3`

B. `changeInt: number2= 3 number: 2`

C. `number: 2 changeInt: number2= 2`

D. `number: 2 changeInt: number2= 3`', 8, 'B', 'Function', 4);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (23, 5, 'The function is as follows:

```python
def showNnumber(numbers):
	for n in numbers:
		print(n)
```

Which of the following will throw an error when calling the function? ( C )

A. `showNumer([2,4,5])` 

B. `showNnumber("abcesf")`

C. `showNnumber(3.4)` 

D. `showNumber((12,4,5))`', 5, 'C', 'Function', 3);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (24, 5, 'The value returned by calling the following function ( D ).

```python
def myfun():
	pass
```

A. `0 `

B. Error and cannot be run

C. Empty string

D. None', 5, 'D', 'Function', 2);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (25, 5, 'The following is not a keyword in Python (D).

A. `raise` 

B. `with`

C. `import` 

D. `final`', 5, 'D', 'Syntax', 2);
INSERT INTO yw.objective_problem (objective_problem_id, author_id, op_description, op_total_score, op_correct_answer, op_tag, op_difficulty) VALUES (26, 5, 'The following code prints the result (B).

```python
str1 = "Runoob example....wow!!!"
str2 = "exam";
print(str1.find(str2, 5)) 
```

A. `6 `

B. `7`

C. `8 `

D. `-1`', 5, 'B', 'Syntax', 2);
