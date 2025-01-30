create table programming_answer
(
    programming_answer_id int auto_increment
        primary key,
    author_id             int            not null,
    problem_set_id        int            not null,
    programming_id        int            not null,
    pa_code               varchar(16000) null,
    pa_actual_score       int            null,
    pass_count            int            null,
    constraint programming_answer_problem_set_problem_set_id_fk
        foreign key (problem_set_id) references problem_set (problem_set_id)
            on update cascade on delete cascade,
    constraint programming_answer_programming_programming_id_fk
        foreign key (programming_id) references programming (programming_id)
            on update cascade on delete cascade,
    constraint programming_answer_user_user_id_fk
        foreign key (author_id) references user (user_id)
            on update cascade on delete cascade
);

INSERT INTO yw.programming_answer (programming_answer_id, author_id, problem_set_id, programming_id, pa_code, pa_actual_score, pass_count) VALUES (24, 2, 1, 1, 'a = list(map(int, input().split()))
a.sort()
print("{:d}->{:d}->{:d}".format(a[0], a[1], a[2]), end=\'\')', 20, 2);
INSERT INTO yw.programming_answer (programming_answer_id, author_id, problem_set_id, programming_id, pa_code, pa_actual_score, pass_count) VALUES (25, 2, 1, 2, 'print("Hello World!", end=\'\')', 15, 1);
INSERT INTO yw.programming_answer (programming_answer_id, author_id, problem_set_id, programming_id, pa_code, pa_actual_score, pass_count) VALUES (26, 2, 2, 1, 'a = list(map(int, input().split()))
a.sort()
print("{:d}->{:d}->{:d}".format(a[0], a[1], a[2]), end=\'\')', 20, 2);
INSERT INTO yw.programming_answer (programming_answer_id, author_id, problem_set_id, programming_id, pa_code, pa_actual_score, pass_count) VALUES (27, 1, 2, 1, 'a = list(map(int, input().split()))
a.sort()
print("{:d}->{:d}->{:d}".format(a[0], a[1], a[2]), end=\'\')
', 20, 2);
INSERT INTO yw.programming_answer (programming_answer_id, author_id, problem_set_id, programming_id, pa_code, pa_actual_score, pass_count) VALUES (28, 1, 1, 1, 's = 0
for i in range(0, 1000):
    for j in range(0, 1000):
        for k in range(0, 1000):
            s = s + i * j * 
print(s)', 0, 0);
INSERT INTO yw.programming_answer (programming_answer_id, author_id, problem_set_id, programming_id, pa_code, pa_actual_score, pass_count) VALUES (29, 1, 1, 2, 'print("Hello World!", end=\'\')', 15, 1);
INSERT INTO yw.programming_answer (programming_answer_id, author_id, problem_set_id, programming_id, pa_code, pa_actual_score, pass_count) VALUES (30, 3, 1, 1, 'a = list(map(int, input().split()))
a.sort()
print("{:d}->{:d}->{:d}".format(a[0], a[1], a[2]), end=\'\')', 20, 2);
INSERT INTO yw.programming_answer (programming_answer_id, author_id, problem_set_id, programming_id, pa_code, pa_actual_score, pass_count) VALUES (31, 3, 1, 2, 'print("Hello World!")', 0, 0);
INSERT INTO yw.programming_answer (programming_answer_id, author_id, problem_set_id, programming_id, pa_code, pa_actual_score, pass_count) VALUES (32, 3, 2, 1, 'print("2->4->8", end=\'\')', 10, 1);
INSERT INTO yw.programming_answer (programming_answer_id, author_id, problem_set_id, programming_id, pa_code, pa_actual_score, pass_count) VALUES (35, 1, 2, 6, 'def f(a, b):
    return a + b', 15, 3);
INSERT INTO yw.programming_answer (programming_answer_id, author_id, problem_set_id, programming_id, pa_code, pa_actual_score, pass_count) VALUES (36, 2, 2, 6, 'def f(a, b):
    return 4', 10, 2);
INSERT INTO yw.programming_answer (programming_answer_id, author_id, problem_set_id, programming_id, pa_code, pa_actual_score, pass_count) VALUES (37, 3, 2, 6, 'def f(a, b):
    print(a + b)', 0, 0);
INSERT INTO yw.programming_answer (programming_answer_id, author_id, problem_set_id, programming_id, pa_code, pa_actual_score, pass_count) VALUES (38, 1, 2, 2, 'print(\'Hello World!\', end=\'\')', 15, 1);
INSERT INTO yw.programming_answer (programming_answer_id, author_id, problem_set_id, programming_id, pa_code, pa_actual_score, pass_count) VALUES (39, 2, 2, 2, 'print("Hello World!")', 0, 0);
INSERT INTO yw.programming_answer (programming_answer_id, author_id, problem_set_id, programming_id, pa_code, pa_actual_score, pass_count) VALUES (48, 3, 2, 2, 'print("Hello World!", end=\'\')', 15, 1);
INSERT INTO yw.programming_answer (programming_answer_id, author_id, problem_set_id, programming_id, pa_code, pa_actual_score, pass_count) VALUES (58, 2, 3, 4, 'a = input().split(\'-\')
print("{:s}/{:s}/{:s}".format(a[1], a[2], a[0]), end=\'\')', 20, 4);
INSERT INTO yw.programming_answer (programming_answer_id, author_id, problem_set_id, programming_id, pa_code, pa_actual_score, pass_count) VALUES (59, 2, 3, 5, 'a, b = map(int, raw_input(\'\').split())
print(a + b, end=\'\')', 25, 5);
INSERT INTO yw.programming_answer (programming_answer_id, author_id, problem_set_id, programming_id, pa_code, pa_actual_score, pass_count) VALUES (60, 3, 3, 4, 'a = input().split(\'-\')
print("{:s}/{:s}/{:s}".format(a[1], a[2], a[0]), end=\'\')', 20, 4);
INSERT INTO yw.programming_answer (programming_answer_id, author_id, problem_set_id, programming_id, pa_code, pa_actual_score, pass_count) VALUES (61, 3, 3, 5, 'a, b = map(int, raw_input(\'\').split())
print(a + b, end=\'\')', 25, 5);
INSERT INTO yw.programming_answer (programming_answer_id, author_id, problem_set_id, programming_id, pa_code, pa_actual_score, pass_count) VALUES (62, 2, 4, 3, 'a, b = input().split()
a = int(a)
c = float(a) / 2 + 0.3
for i in range(0, int(round(c))):
    if i:
        print()
    for j in range(0, a):
        print(b, end = "")
    
    #python的round满足四舍六入五凑偶', 25, 2);
INSERT INTO yw.programming_answer (programming_answer_id, author_id, problem_set_id, programming_id, pa_code, pa_actual_score, pass_count) VALUES (63, 2, 4, 7, 'a = input();
print(a.lower(), end=\'\') #输出kdjiskos234k,.;djfeij', 20, 4);
INSERT INTO yw.programming_answer (programming_answer_id, author_id, problem_set_id, programming_id, pa_code, pa_actual_score, pass_count) VALUES (64, 3, 4, 3, 'a, b = input().split()
a = int(a)
c = float(a) / 2 + 0.3
for i in range(0, int(round(c))):
    if i:
        print()
    for j in range(0, a):
        print(b, end = "")
    
    #python的round满足四舍六入五凑偶', 25, 2);
INSERT INTO yw.programming_answer (programming_answer_id, author_id, problem_set_id, programming_id, pa_code, pa_actual_score, pass_count) VALUES (65, 3, 4, 7, 'a = input();
print(a.lower(), end=\'\') #输出kdjiskos234k,.;djfeij', 20, 4);
INSERT INTO yw.programming_answer (programming_answer_id, author_id, problem_set_id, programming_id, pa_code, pa_actual_score, pass_count) VALUES (67, 7, 5, 6, 'def f(a, b):
    return a + b', 15, 3);
INSERT INTO yw.programming_answer (programming_answer_id, author_id, problem_set_id, programming_id, pa_code, pa_actual_score, pass_count) VALUES (69, 3, 5, 6, 'def f(a, b):
    return a + b', 15, 3);
INSERT INTO yw.programming_answer (programming_answer_id, author_id, problem_set_id, programming_id, pa_code, pa_actual_score, pass_count) VALUES (80, 3, 6, 11, 'def find_last_is_odd(x): #求解末尾第一个非0的数字
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
    print(1, end=\'\')', 25, 5);
INSERT INTO yw.programming_answer (programming_answer_id, author_id, problem_set_id, programming_id, pa_code, pa_actual_score, pass_count) VALUES (81, 3, 6, 14, 's = 0
for i in range(0, 1000):
    for j in range(0, 1000):
        for k in range(0, 1000):
            s = s + i * j * k
print(s', 0, 0);
INSERT INTO yw.programming_answer (programming_answer_id, author_id, problem_set_id, programming_id, pa_code, pa_actual_score, pass_count) VALUES (82, 3, 7, 9, 'a =input()
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
#输出YES', 20, 4);
INSERT INTO yw.programming_answer (programming_answer_id, author_id, problem_set_id, programming_id, pa_code, pa_actual_score, pass_count) VALUES (83, 3, 7, 12, 'def binary(x):
    count = 0
    while x > 0:
        if x % 2 == 1:
            count += 1
        x //= 2
    return count ', 20, 4);
