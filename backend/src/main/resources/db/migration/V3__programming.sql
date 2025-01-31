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

INSERT INTO yw.programming (programming_id, p_description, p_total_score, time_limit, code_size_limit, p_tag, p_author_id, p_title, p_judge_code, p_difficulty) VALUES (1, '本题要求将输入的任意3个整数从小到大输出。

#### 输入格式

输入在一行中给出3个整数，其间以空格分隔。

#### 输出格式

在一行中将3个整数从小到大输出，其间以“->”相连。

#### 输入样例

```
4 2 8
```

#### 输出样例

```
2->4->8
```

#### 提示

```python
a = list(map(int, input().split()))
a.sort()
print("{:d}->{:d}->{:d}".format(a[0], a[1], a[2]), end=\'\')
```', 20, 400, 16, '函数', 2, 'L1-010 比较大小', '', 5);
INSERT INTO yw.programming (programming_id, p_description, p_total_score, time_limit, code_size_limit, p_tag, p_author_id, p_title, p_judge_code, p_difficulty) VALUES (2, '这道超级简单的题目没有任何输入。

你只需要在一行中输出著名短句“Hello World!”就可以了。

### 输入样例：

```in
无
```

### 输出样例：

```out
Hello World!
```', 15, 400, 16, '基本语法', 2, 'L1-001 Hello World', '', 1);
INSERT INTO yw.programming (programming_id, p_description, p_total_score, time_limit, code_size_limit, p_tag, p_author_id, p_title, p_judge_code, p_difficulty) VALUES (3, '美国总统奥巴马不仅呼吁所有人都学习编程，甚至以身作则编写代码，成为美国历史上首位编写计算机代码的总统。2014年底，为庆祝“计算机科学教育周”正式启动，奥巴马编写了很简单的计算机代码：在屏幕上画一个正方形。现在你也跟他一起画吧！

### 输入格式：

输入在一行中给出正方形边长$N(3≤N≤21)$和组成正方形边的某种字符`C`，间隔一个空格。

### 输出格式：

输出由给定字符`C`画出的正方形。但是注意到行间距比列间距大，所以为了让结果看上去更像正方形，我们输出的行数实际上是列数的50%（四舍五入取整）。

### 输入样例：

```in
10 a
```

### 输出样例：

```out
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
    
    #python的round满足四舍六入五凑偶
```', 25, 400, 16, '控制语句', 2, 'L1-015 跟奥巴马一起画方块', '', 5);
INSERT INTO yw.programming (programming_id, p_description, p_total_score, time_limit, code_size_limit, p_tag, p_author_id, p_title, p_judge_code, p_difficulty) VALUES (4, '本题要求将输入的日期YYYY-MM-DD的格式，转化为MM/DD/YYYY的格式，月份和日期需要处理前导0，年份保证没有前导0。

#### 输入格式

输入在一行中给出日期YYYY-MM-DD。

#### 输出格式

在一行中将输入的日期转化为MM/DD/YYYY的格式输出。

#### 输入样例

```
2024-5-12
```

#### 输出样例

```
05/12/2024
```

#### 提示

```python
a = input().split(\'-\')
print("{:s}/{:s}/{:s}".format(a[1], a[2], a[0]), end=\'\')
```', 20, 400, 16, '基本语法', 1, '日期格式转换', '', 3);
INSERT INTO yw.programming (programming_id, p_description, p_total_score, time_limit, code_size_limit, p_tag, p_author_id, p_title, p_judge_code, p_difficulty) VALUES (5, '输入两个整数，求这两个整数的和是多少。

#### 输入格式

输入两个整数$A,B$，用空格隔开

#### 输出格式

输出一个整数，表示这两个数的和

#### 数据范围

$0≤A,B≤10^8$

#### 输入样例

```
3 4
```

#### 输出样例

```
7
```

#### Hint

```python
a, b = map(int, raw_input(\'\').split())
print(a + b, end=\'\')
```', 25, 400, 16, '基本语法', 1, '1. A + B', '', 3);
INSERT INTO yw.programming (programming_id, p_description, p_total_score, time_limit, code_size_limit, p_tag, p_author_id, p_title, p_judge_code, p_difficulty) VALUES (6, '本题要求实现函数`f(a, b)`，输入两个整数，求这两个整数的和是多少，并将两个整数的和返回。

#### 输入格式

输入两个整数$A,B$，用空格隔开

#### 输出格式

输出一个整数，表示这两个数的和

#### 数据范围

$0≤A,B≤10^8$

#### 输入样例

```
3 4
```

#### 输出样例

```
7
```

#### 函数题测评程序

```python
# 学生代码将会被嵌在函数题测评程序上方
a, b = map(int, raw_input(\'\').split())
print(f(a, b))
print(str, end=\'\')  # str 是防止利用print代码的随机字符串，细节在此不表
```

#### Hint

```python
def f(a, b):
    return a + b
```', 15, 400, 16, '函数', 1, '函数题A+B', '# 学生代码将会被嵌在函数题测评程序上方

a, b = map(int, raw_input(\'\').split())
print(f(a, b))
print("lalala", end=\'\')', 2);
INSERT INTO yw.programming (programming_id, p_description, p_total_score, time_limit, code_size_limit, p_tag, p_author_id, p_title, p_judge_code, p_difficulty) VALUES (7, '### **题目**

给定一个字符串a, 将a中的大写字母 转换成小写，其它字符不变，并输出

### **输入示例**

```
KDJIskos234k,.;djfeiJ
```

### **输出示例**

```
kdjiskos234k,.;djfeij
```

### **算法**

其实这道题有很多方法，比如可以根据判断是不是大写字母，然后对其ASCII码值加32来转换等等。不过，既然是python题，就用python的方法简简单单完成啦。

需要利用**lower()**函数，注意**lower函数只会将A-Z有效，将对应大写转换为小写，**其他字符不会转换，刚好符合本题要求。

```python
a = input();
print(a.lower(), end=\'\') #输出kdjiskos234k,.;djfeij
```', 20, 400, 16, '控制语句', 2, '大小写转换', '# 学生代码将会被嵌在函数题测评程序上方', 2);
INSERT INTO yw.programming (programming_id, p_description, p_total_score, time_limit, code_size_limit, p_tag, p_author_id, p_title, p_judge_code, p_difficulty) VALUES (8, '### **题目**

给你三个整数a,b,c, 判断能否以它们为三个边长构成三角形。 若能，输出YES，否则输出NO。

### **输入示例**

```
5 5 5
```

### **输出示例**

```
YES
```

### **算法**

三角形任意两边之和大于第三边，可以利用列表排序求出较小的两边，然后相加再和第三边比较，进行判断

```python
a, b, c = map(int, input().split())
L=list()
L.append(a)
L.append(b)
L.append(c)
L.sort() #默认从小到大排序
if L[0]+L[1]>L[2]:
    print(\'YES\', end=\'\')
else:
    print(\'NO\', end=\'\')
#YES
```', 20, 400, 16, '控制语句', 2, '判断三角形', '# 学生代码将会被嵌在函数题测评程序上方', 3);
INSERT INTO yw.programming (programming_id, p_description, p_total_score, time_limit, code_size_limit, p_tag, p_author_id, p_title, p_judge_code, p_difficulty) VALUES (9, '### **题目**

给你一个字符串a和一个正整数n,判断a中是否存在长度为n的回文子串。如果存在，则输出YES，否则输出NO。 回文串的定义：记串str逆序之后的字符串是str1，若str=str1,则称str是回文串，如"abcba".

### **输入示例**

```
abcba
```

### **输出示例**

```
YES
```

### **算法**

根据正整数n进行分割字符串，然后判断字符串是不是回文串。由于python中字符串没有直接提供reverse函数（列表list有，但需要先将字符串转换为列表，较麻烦），所以采用字符串切片。若一个字符串为s，其逆序为s[：：-1]，前两个空表示提取全部，-1表示逆序。

```python
a =input()
n = len(a)
flag = 0
for i in range(len(a)):
    if i + n > len(a): #索引超过最大值，直接提前退出
        break
    str_tmp = a[i : i + n]
    str_tmp_reverse = str_tmp[::-1] #字符串翻转
    if str_tmp == str_tmp_reverse:
        flag = 1
        break
if flag == 1:
    print(\'YES\', end=\'\')
else:
    print(\'NO\', end=\'\')
#输出YES
```', 20, 400, 16, '控制语句', 2, '回文子串', '# 学生代码将会被嵌在函数题测评程序上方', 4);
INSERT INTO yw.programming (programming_id, p_description, p_total_score, time_limit, code_size_limit, p_tag, p_author_id, p_title, p_judge_code, p_difficulty) VALUES (10, '#### 题目

给你一个正整数列表 L, 输出L内所有数字的乘积末尾0的个数。(提示:不要直接相乘,数字很多,相乘得到的结果可能会很大)。

#### 输入示例

```
2 8 3 50
```

#### 输出示例

```
2
```

#### 算法

寻找列表中所有元素中，每个元素因子中$2$和$5$的个数。因为$2 \\times 5 = 10$，所以计算出因子$2$和$5$的对数，就得到乘积末尾$0$的个数。

```python
def find_helper(x):
    tmp = x
    f2 = 0 #记录因子2的个数
    f5 = 0 #记录因子5的个数
    while x % 2 == 0:
        f2 += 1
        x = x / 2
    while tmp%5 == 0:
        f5 += 1
        tmp /= 5
    return f2,f5

L = map(int, input().split())

a2 = 0 #记录列表中因子2的个数
a5 = 0 #记录列表中因子5的个数
for i in L:
    t2,t5 = find_helper(i)
    a2 += t2
    a5 += t5
print(min(a2, a5), end=\'\') #2
```', 25, 400, 16, '函数', 7, '结尾0的个数', '# 学生代码将会被嵌在函数题测评程序上方', 5);
INSERT INTO yw.programming (programming_id, p_description, p_total_score, time_limit, code_size_limit, p_tag, p_author_id, p_title, p_judge_code, p_difficulty) VALUES (11, '#### 题目

给你一个正整数列表 L, 判断列表内所有数字乘积的最后一个非零数字的奇偶性。如果为奇数输出1,偶数则输出0。

#### 输入示例

```
2 8 3 50
```

#### 输出示例

```
0
```

#### 算法

比较粗暴，直接累乘，然后利用算法求末尾第一个非0的数字。

```python
def find_last_is_odd(x): #求解末尾第一个非0的数字
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
```', 25, 400, 16, '控制语句', 7, '结尾非零数的奇偶性', '# 学生代码将会被嵌在函数题测评程序上方', 4);
INSERT INTO yw.programming (programming_id, p_description, p_total_score, time_limit, code_size_limit, p_tag, p_author_id, p_title, p_judge_code, p_difficulty) VALUES (12, '#### 题目

光棍们对1总是那么敏感，因此每年的11.11被戏称为光棍节。小Py光棍几十载，光棍自有光棍的快乐。让我们勇敢地面对光棍的身份吧，现在就证明自己：给你一个整数a，数出a在二进制表示下1的个数，并输出。

#### 输入示例

```
7
```

#### 输出示例

```
3
```

#### 函数测评程序

```python
def binary(x):
    # your implimentation

a = int(input())
print(binary(a))
print(str, end=\'\')  # random str
```

#### 算法

7的二进制是111，所以输出答案是3。这道题考的是如何将十进制整数转化为二进制数，方法就是：**除二取余，逆序读取**。

```python
def binary(x):
    count = 0
    while x > 0:
        if x % 2 == 1:
            count += 1
        x //= 2
    return count 
```', 20, 400, 16, '函数', 7, '函数题：光棍的悲伤', '# 学生代码将会被嵌在函数题测评程序上方

a = int(input())
print(binary(a))
print("lalalalala", end=\'\')  # random str', 3);
INSERT INTO yw.programming (programming_id, p_description, p_total_score, time_limit, code_size_limit, p_tag, p_author_id, p_title, p_judge_code, p_difficulty) VALUES (13, '#### 题目

给你两个正整数a,b, 输出它们公约数的个数。

#### 输入示例

```
24 36
```

#### 输出示例

```
6
```

#### 函数测评程序

```python
def count(x, y):
    # your implimentation

a, b = map(int, input().split())
print(count(a, b), end=\'\')
```

#### 算法

朴素的思想：从1到两数较小的数，看看是否可以同时被两数整除，若可以则结果加一。

```python
def count(x, y):
    # your implimentation
    count = 0
    for i in range(1, min(x, y) + 1):
        if x % i == 0 and y % i == 0:
            count += 1
    return count
```', 25, 400, 16, '函数', 7, '函数题：公约数的个数', '# 学生代码将会被嵌在函数题测评程序上方

a, b = map(int, input().split())
print(count(a, b), end=\'\')', 4);
INSERT INTO yw.programming (programming_id, p_description, p_total_score, time_limit, code_size_limit, p_tag, p_author_id, p_title, p_judge_code, p_difficulty) VALUES (14, '抓不住爱情的我总是眼睁睁看它溜走...

现在来练习一下发现爱的能力，给你一个字符串a,如果其中包含"LOVE"（love不区分大小写)则输出`LOVE`，否则输出`SINGLE`。

#### 输入示例

```in
OurWorldIsFullOfLOVE
```

#### 输出示例

```out
LOVE
```

#### 算法

先利用刚才学到的**lower函数**，将原字符串转换为小写，然后利用**str切片**，得到一个子串，与目标子串（love）进行**比较**。

```python
a = input()
b = a.lower() 
flag = 0 #判断开关
for i in range(len(a)):
    if i + 4 > len(a): #防止超出范围
        break
    tmp = b[i : i + 4]
    if tmp == \'love\':
        flag = 1
if flag:
    print(\'LOVE\', end=\'\')
else:
    print(\'SINGLE\', end=\'\')
```', 25, 200, 16, '控制语句', 7, '单身情歌', '# 学生代码将会被嵌在函数题测评程序上方', 3);
INSERT INTO yw.programming (programming_id, p_description, p_total_score, time_limit, code_size_limit, p_tag, p_author_id, p_title, p_judge_code, p_difficulty) VALUES (15, '#### 题目

请你实现一个函数`encrypt(str, offset)`，给你个小写英文字符串$str$和一个非负数$offset$($0\\leq offset \\le 26$), 将$str$中的每个小写字符替换成字母表中比它大$offset$的字母。这里将字母表的`z`和`a`相连，如果超过了`z`就回到了`a`。

**字符串中不存在非英文字符**

#### 输入示例

```in
cagy 3
```

#### 输出示例

```out
fdjb
```

#### 函数题测评程序

```python
# 学生代码将会被嵌在函数题测评程序上方
L = input().split()
a = L[0]
b = int(L[1])
print(encrypt(a, b), end=\'\')
```

#### 算法

这道题主要用到以下两个函数：

**ord函数：**返回字符的ASCII值

**chr函数：**根据ASCII值得到对应字符

利用ord函数和chr函数进行值的相互转换，**如果判断字符移动后值大于z的，要转变到从a开始**

```python
def encrypt(str, offset):
    res = \'\'
    for ch in str:
        if (ord(ch) + offset) <= ord(\'z\'):
            res += chr(ord(ch) + offset) #直接进行移动
        else:
            res += chr(ord(ch) + offset - ord(\'z\') + ord(\'a\') - 1) #会超过z的，计算出再从a开始要移动的位数
    return res

L = input().split()
a = L[0]
b = int(L[1])
print(encrypt(a, b), end=\'\')
```', 20, 400, 16, '函数', 7, '函数题：信息加密', '# 学生代码将会被嵌在函数题测评程序上方

L = input().split()
a = L[0]
b = int(L[1])
print(encrypt(a, b), end=\'\')', 3);
