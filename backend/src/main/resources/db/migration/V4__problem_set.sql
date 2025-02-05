create table problem_set
(
    problem_set_id int auto_increment
        primary key,
    ps_name        varchar(100)  not null,
    ps_author_id   int           not null,
    ps_start_time  datetime      not null,
    ps_end_time    datetime      not null,
    duration       int default 0 not null,
    constraint problem_set_user_user_id_fk
        foreign key (ps_author_id) references user (user_id)
            on update cascade on delete cascade
);

INSERT INTO yw.problem_set (problem_set_id, ps_name, ps_author_id, ps_start_time, ps_end_time, duration) VALUES (1, '摸底考试', 2, '2024-05-15 19:00:00', '2024-05-15 21:35:00', 30);
INSERT INTO yw.problem_set (problem_set_id, ps_name, ps_author_id, ps_start_time, ps_end_time, duration) VALUES (2, '第1次作业-基本语法', 2, '2024-04-15 17:47:00', '2024-05-16 18:10:00', 0);
INSERT INTO yw.problem_set (problem_set_id, ps_name, ps_author_id, ps_start_time, ps_end_time, duration) VALUES (3, '第2次作业-基本数据类型与表达式', 2, '2024-05-18 15:56:00', '2024-05-18 16:04:00', 0);
INSERT INTO yw.problem_set (problem_set_id, ps_name, ps_author_id, ps_start_time, ps_end_time, duration) VALUES (4, '第3次作业-控制语句', 2, '2024-05-18 16:03:00', '2024-05-18 16:09:00', 0);
INSERT INTO yw.problem_set (problem_set_id, ps_name, ps_author_id, ps_start_time, ps_end_time, duration) VALUES (5, '第4次作业-函数', 7, '2024-05-18 16:06:00', '2024-05-25 18:12:00', 0);
INSERT INTO yw.problem_set (problem_set_id, ps_name, ps_author_id, ps_start_time, ps_end_time, duration) VALUES (6, '中期考前测试', 7, '2024-05-27 20:11:00', '2024-05-27 20:15:00', 0);
INSERT INTO yw.problem_set (problem_set_id, ps_name, ps_author_id, ps_start_time, ps_end_time, duration) VALUES (7, '中期考试', 7, '2024-05-27 20:16:00', '2024-05-27 20:20:00', 2);
