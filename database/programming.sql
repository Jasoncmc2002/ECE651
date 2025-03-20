create table programming
(
    programming_id  int auto_increment
        primary key,
    p_description   varchar(10000)                not null,
    p_total_score   int                           not null,
    time_limit      int          default 400      not null comment 'unit: ms',
    code_size_limit int          default 16       not null comment 'unit: kb',
    p_tag           varchar(100) default '无分类' not null,
    p_author_id     int                           not null,
    p_title         varchar(100)                  not null comment '编程题标题',
    p_judge_code    text                          null comment '函数题测评代码',
    p_difficulty    int          default 0        not null comment '难度系数',
    constraint programming_user_user_id_fk
        foreign key (p_author_id) references user (user_id)
            on update cascade on delete cascade
);

INSERT INTO yw.programming (programming_id, p_description, p_total_score, time_limit, code_size_limit, p_tag, p_author_id, p_title, p_judge_code, p_difficulty) VALUES (1, 'This question requires you to output any 3 integers from small to large.

#### Input format

Input 3 integers in a line, separated by spaces.

#### Output format

Output 3 integers in a line from small to large, connected by “->”.

#### Input example

```
4 2 8
```

#### Output example

```
2->4->8
```

#### Tips

```python
a = list(map(int, input().split()))
a.sort()
print("{:d}->{:d}->{:d}".format(a[0], a[1], a[2]), end=\'\')
```', 20, 400, 16, 'Function', 2, 'L1-010 Comparison', '', 5);
INSERT INTO yw.programming (programming_id, p_description, p_total_score, time_limit, code_size_limit, p_tag, p_author_id, p_title, p_judge_code, p_difficulty) VALUES (2, 'This super simple question does not require any input.

You just need to output the famous phrase "Hello World!" in one line.

### Input sample:

```in
None
```

### Output sample:

```out
Hello World!
```', 15, 150, 16, 'Syntax', 2, 'L1-001 Hello World', '', 1);
INSERT INTO yw.programming (programming_id, p_description, p_total_score, time_limit, code_size_limit, p_tag, p_author_id, p_title, p_judge_code, p_difficulty) VALUES (3, 'US President Barack Obama not only called on everyone to learn programming, but also set an example by writing code, becoming the first president in US history to write computer code. At the end of 2014, to celebrate the official launch of "Computer Science Education Week", Obama wrote a very simple computer code: draw a square on the screen. Now you can draw it with him!

### Input format:

Input the length of the square side $N(3≤N≤21)$ and a certain character `C` that makes up the side of the square in a line, separated by a space.

### Output format:

Output the square drawn by the given character `C`. But notice that the row spacing is larger than the column spacing, so in order to make the result look more like a square, the number of rows we output is actually 50% of the number of columns (rounded to the nearest integer).

### Input example:

```in
10 a
```

### Output example:

```out
aaaaaaaaaa
aaaaaaaaaa
aaaaaaaaaa
aaaaaaaaaa
aaaaaaaaaa
aaaaaaaaaa
```

### Hint

```python
a, b = input().split()
a = int(a)
c = float(a) / 2 + 0.3
for i in range(0, int(round(c))):
    if i:
        print()
    for j in range(0, a):
        print(b, end = "")
```

', 25, 400, 16, 'Branch', 2, 'L1-015 Draw a Square', '', 5);
INSERT INTO yw.programming (programming_id, p_description, p_total_score, time_limit, code_size_limit, p_tag, p_author_id, p_title, p_judge_code, p_difficulty) VALUES (4, 'This question requires converting the input date format of YYYY-MM-DD to the format of MM/DD/YYYY. The leading zeros of the month and day need to be processed, and the year must not have leading zeros.

#### Input format

Input the date YYYY-MM-DD in one line.

#### Output format

Convert the input date to the format of MM/DD/YYYY in one line and output it.

#### Input sample

```
2024-5-12
```

#### Output sample

```
05/12/2024
```

#### Tips

```python
a = input().split(\'-\')
print("{:s}/{:s}/{:s}".format(a[1], a[2], a[0]), end=\'\')
```', 20, 400, 16, 'Syntax', 1, 'Date format conversion', '', 3);
INSERT INTO yw.programming (programming_id, p_description, p_total_score, time_limit, code_size_limit, p_tag, p_author_id, p_title, p_judge_code, p_difficulty) VALUES (5, 'Input two integers and find the sum of the two integers.

#### Input format

Input two integers $A, B$, separated by a space

#### Output format

Output an integer representing the sum of the two numbers

#### Data range

$0≤A,B≤10^8$

#### Input sample

```
3 4
```

#### Output sample

```
7
```

#### Hint

```python
a, b = map(int, raw_input(\'\').split())
print(a + b, end=\'\')
```', 25, 400, 16, 'Syntax', 1, '1. A + B', '', 3);
INSERT INTO yw.programming (programming_id, p_description, p_total_score, time_limit, code_size_limit, p_tag, p_author_id, p_title, p_judge_code, p_difficulty) VALUES (6, 'This question requires implementing the function `f(a, b)`, inputting two integers, finding the sum of the two integers, and returning the sum of the two integers.

#### Input format

Input two integers $A, B$, separated by a space

#### Output format

Output an integer representing the sum of the two numbers

#### Data range

$0≤A,B≤10^8$

#### Input sample

```
3 4
```

#### Output sample

```
7
```

#### Evaluation program

```python
# The student code will be embedded above the function question assessment program
a, b = map(int, raw_input(\'\').split())
print(f(a, b))
print(str, end=\'\')  # str is a random string
```

#### Hint

```python
def f(a, b):
    return a + b
```', 15, 400, 16, 'Function', 1, 'Code Completion A+B', '# The student code will be embedded above the function question assessment program

a, b = map(int, raw_input(\'\').split())
print(f(a, b))
print("lalala", end=\'\')', 2);
INSERT INTO yw.programming (programming_id, p_description, p_total_score, time_limit, code_size_limit, p_tag, p_author_id, p_title, p_judge_code, p_difficulty) VALUES (7, '### **Description**

Given a string a, convert the uppercase letters in a to lowercase, keep other characters unchanged, and output

### **Input example**

```
KDJIskos234k,.;djfeiJ
```

### **Output example**

```
kdjiskos234k,.;djfeij
```

### **Algorithm**

In fact, there are many ways to solve this problem, such as judging whether it is an uppercase letter, and then converting it by adding 32 to its ASCII code value. However, since it is a python question, it is simply completed with the python method.

You need to use the **lower()** function. Note that the **lower()** function will only work for A-Z, converting the corresponding uppercase to lowercase, **other characters will not be converted, which just meets the requirements of this question.

```python
a = input();
print(a.lower(), end=\'\') #Output kdjiskos234k,.;djfeij
```', 20, 400, 16, 'Branch', 2, 'Case conversion', '# 学生代码将会被嵌在函数题测评程序上方', 2);
INSERT INTO yw.programming (programming_id, p_description, p_total_score, time_limit, code_size_limit, p_tag, p_author_id, p_title, p_judge_code, p_difficulty) VALUES (8, '### **Question**

Give you three integers a, b, c. Determine whether a triangle can be formed with them as the three sides. If yes, output YES, otherwise output NO.

### **Input Example**

```
5 5 5
```

### **Output Example**

```
YES
```

### **Algorithm**

If the sum of any two sides of a triangle is greater than the third side, you can use the list sorting to find the smaller two sides, then add them and compare them with the third side to make a judgment

```python
a, b, c = map(int, input().split())
L=list()
L.append(a)
L.append(b)
L.append(c)
L.sort() # Default Ascending
if L[0]+L[1]>L[2]:
    print(\'YES\', end=\'\')
else:
    print(\'NO\', end=\'\')
#YES
```', 20, 400, 16, 'Branch', 2, 'Determine the triangle', '# 学生代码将会被嵌在函数题测评程序上方', 3);
INSERT INTO yw.programming (programming_id, p_description, p_total_score, time_limit, code_size_limit, p_tag, p_author_id, p_title, p_judge_code, p_difficulty) VALUES (9, '### **Question**

Give you a string a and a positive integer n. Determine whether there is a palindrome substring of length n in a. If it exists, output YES, otherwise output NO. Definition of palindrome: The string after the string str is reversed is str1. If str=str1, then str is called a palindrome, such as "abcba".

### **Input example**

```
abcba
```

### **Output example**

```
YES
```

### **Algorithm**

Split the string according to the positive integer n, and then determine whether the string is a palindrome. Since Python does not directly provide a reverse function for strings (lists have, but the string needs to be converted to a list first, which is more troublesome), string slicing is used. If a string is s, its reverse order is s[::-1]. The first two spaces indicate that all are extracted, and -1 indicates reverse order.

```python
a =input()
n = len(a)
flag = 0
for i in range(len(a)):
    if i + n > len(a): #If the index exceeds the maximum value, exit early
        break
    str_tmp = a[i : i + n]
    str_tmp_reverse = str_tmp[::-1] #String Reversal
    if str_tmp == str_tmp_reverse:
        flag = 1
        break
if flag == 1:
    print(\'YES\', end=\'\')
else:
    print(\'NO\', end=\'\')
#YES
```', 20, 400, 16, 'Branch', 2, 'Palindrome', '# 学生代码将会被嵌在函数题测评程序上方', 4);
INSERT INTO yw.programming (programming_id, p_description, p_total_score, time_limit, code_size_limit, p_tag, p_author_id, p_title, p_judge_code, p_difficulty) VALUES (10, '#### Question

Give you a list of positive integers L, output the number of 0s at the end of the product of all the numbers in L. (Hint: Don\'t multiply directly, there are a lot of numbers, and the result of multiplication may be very large).

#### Input example

```
2 8 3 50
```

#### Output example

```
2
```

#### Algorithm

Find the number of $2$ and $5$ in the factors of each element in the list. Because $2 \\times 5 = 10$, calculate the logarithm of the factors $2$ and $5$, and you will get the number of $0$ at the end of the product.

```python
def find_helper(x):
    tmp = x
    f2 = 0 #Record the number of factors of 2
    f5 = 0 #Record the number of factors of 5
    while x % 2 == 0:
        f2 += 1
        x = x / 2
    while tmp%5 == 0:
        f5 += 1
        tmp /= 5
    return f2,f5

L = map(int, input().split())

a2 = 0 #Record the number of factors of 2
a5 = 0 #Record the number of factors of 5
for i in L:
    t2,t5 = find_helper(i)
    a2 += t2
    a5 += t5
print(min(a2, a5), end=\'\') #2
```', 25, 400, 16, 'Function', 5, 'The number of trailing zeros', '# 学生代码将会被嵌在函数题测评程序上方', 5);
INSERT INTO yw.programming (programming_id, p_description, p_total_score, time_limit, code_size_limit, p_tag, p_author_id, p_title, p_judge_code, p_difficulty) VALUES (11, '#### Question

Give you a list of positive integers L. Determine the parity of the last non-zero digit in the product of all the numbers in the list. Output 1 if it is an odd number, and output 0 if it is an even number.

#### Input example

```
2 8 3 50
```

#### Output example

```
0
```

#### Algorithm

It is relatively crude. It directly accumulates the multiplication and then uses the algorithm to find the first non-zero digit at the end.

```python
def find_last_is_odd(x): #Find the first non-zero number at the end
    if x % 10 != 0:
        return x % 10
    else:
        while x % 10 == 0:
            x //= 10
            if x % 10 != 0:
                return x%10
                break

L = map(int, input().split())
total = 1
for i in L:
    total *= i

if find_last_is_odd(total) % 2 == 0:
    print(0, end=\'\')
else:
    print(1, end=\'\')
```', 25, 400, 16, 'Branch', 5, 'Parity of ending non-zero numbers', '# 学生代码将会被嵌在函数题测评程序上方', 4);
INSERT INTO yw.programming (programming_id, p_description, p_total_score, time_limit, code_size_limit, p_tag, p_author_id, p_title, p_judge_code, p_difficulty) VALUES (12, '#### Question

Singles are always so sensitive to 1, so every year\'s 11.11 is jokingly called Singles\' Day. Little Py has been single for decades, and being single has its own happiness. Let\'s face the identity of being single bravely, and now prove ourselves: give you an integer a, count the number of 1s in a\'s binary representation, and output it.

#### Input example

```
7
```

#### Output example

```
3
```

#### Evaluation program

```python
def binary(x):
    # your implimentation

a = int(input())
print(binary(a))
print(str, end=\'\')  # random str
```

#### Algorithm

The binary number of 7 is 111, so the output answer is 3. This question tests how to convert a decimal integer into a binary number. The method is: **divide by two and take the remainder, read in reverse order**.

```python
def binary(x):
    count = 0
    while x > 0:
        if x % 2 == 1:
            count += 1
        x //= 2
    return count 
```', 20, 400, 16, 'Function', 5, 'Function question: The sadness of being single', '# 学生代码将会被嵌在函数题测评程序上方

a = int(input())
print(binary(a))
print("lalalalala", end=\'\')  # random str', 3);
INSERT INTO yw.programming (programming_id, p_description, p_total_score, time_limit, code_size_limit, p_tag, p_author_id, p_title, p_judge_code, p_difficulty) VALUES (13, '#### Question

Give you two positive integers a and b, output the number of their common divisors.

#### Input example

```
24 36
```

#### Output example

```
6
```

#### Evaluation program

```python
def count(x, y):
    # your implimentation

a, b = map(int, input().split())
print(count(a, b), end=\'\')
```

#### Algorithm

Simple idea: from 1 to the smaller of the two numbers, see if it can be divided by both numbers at the same time, if so, add one to the result.

```python
def count(x, y):
    # your implimentation
    count = 0
    for i in range(1, min(x, y) + 1):
        if x % i == 0 and y % i == 0:
            count += 1
    return count
```', 25, 400, 16, 'Function', 5, 'Function problem: Number of common divisors', '# 学生代码将会被嵌在函数题测评程序上方

a, b = map(int, input().split())
print(count(a, b), end=\'\')', 4);
INSERT INTO yw.programming (programming_id, p_description, p_total_score, time_limit, code_size_limit, p_tag, p_author_id, p_title, p_judge_code, p_difficulty) VALUES (14, 'I can\'t catch love, and I always watch it slip away...

Now let\'s practice the ability to find love. Give you a string a. If it contains "LOVE" (love is not case-sensitive), output `LOVE`, otherwise output `SINGLE`.

#### Input example

```in
OurWorldIsFullOfLOVE
```

#### Output example

```out
LOVE
```

#### Algorithm

First, use the **lower function** just learned to convert the original string to lowercase, then use **str slice** to get a substring, and **compare** with the target substring (love).

```python
a = input()
b = a.lower() 
flag = 0 
for i in range(len(a)):
    if i + 4 > len(a): #Prevent out of range
        break
    tmp = b[i : i + 4]
    if tmp == \'love\':
        flag = 1
if flag:
    print(\'LOVE\', end=\'\')
else:
    print(\'SINGLE\', end=\'\')
```', 25, 200, 16, 'Branch', 5, 'Single Love Song', '# 学生代码将会被嵌在函数题测评程序上方', 3);
INSERT INTO yw.programming (programming_id, p_description, p_total_score, time_limit, code_size_limit, p_tag, p_author_id, p_title, p_judge_code, p_difficulty) VALUES (15, '#### Question

Please implement a function `encrypt(str, offset)`. Given a lowercase English string $str$ and a non-negative number $offset$($0\\leq offset \\le 26$), replace each lowercase character in $str$ with a letter in the alphabet that is $offset$ greater than it. Here, `z` and `a` in the alphabet are connected. If it exceeds `z`, it returns to `a`.

**There are no non-English characters in the string**

#### Input example

```in
cagy 3
```

#### Output example

```out
fdjb
```

#### Evaluation program

```python
# The student code will be embedded above the function question assessment program
L = input().split()
a = L[0]
b = int(L[1])
print(encrypt(a, b), end=\'\')
```

#### Algorithm

This question mainly uses the following two functions:

**ord function:** returns the ASCII value of the character

**chr function:** gets the corresponding character according to the ASCII value

Use the ord function and the chr function to convert values. **If the value of the character after moving is greater than z, it should be changed to start from a**

```python
def encrypt(str, offset):
    res = \'\'
    for ch in str:
        if (ord(ch) + offset) <= ord(\'z\'):
            res += chr(ord(ch) + offset) #Directly move
        else:
            res += chr(ord(ch) + offset - ord(\'z\') + ord(\'a\') - 1) #It will exceed z, calculate the number of digits to move from a
    return res

L = input().split()
a = L[0]
b = int(L[1])
print(encrypt(a, b), end=\'\')
```', 20, 400, 16, 'Function', 5, 'Code Completion: Information encryption', '# The student code will be embedded above the function question assessment program

L = input().split()
a = L[0]
b = int(L[1])
print(encrypt(a, b), end=\'\')', 3);
INSERT INTO yw.programming (programming_id, p_description, p_total_score, time_limit, code_size_limit, p_tag, p_author_id, p_title, p_judge_code, p_difficulty) VALUES (16, 'Input two integers and find the sum of the two integers.

#### Input format

Input two integers $A, B$, separated by a space

#### Output format

Output an integer representing the sum of the two numbers

#### Data range

$0≤A,B≤10^8$

#### Input sample

```
3 4
```

#### Output sample

```
7
```

#### Hint

```python
a, b = map(int, raw_input(\'\').split())
print(a + b, end=\'\')
```', 20, 400, 16, 'None', 2, 'A + B', '# Student answers will be placed on top of the judge code', 1);
