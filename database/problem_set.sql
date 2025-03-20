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

INSERT INTO yw.problem_set (problem_set_id, ps_name, ps_author_id, ps_start_time, ps_end_time, duration) VALUES (1, 'Entry Exam', 2, '2025-01-15 19:00:00', '2025-01-15 21:35:00', 30);
INSERT INTO yw.problem_set (problem_set_id, ps_name, ps_author_id, ps_start_time, ps_end_time, duration) VALUES (2, 'Assignment 1 - Basic Syntax', 2, '2025-02-15 17:47:00', '2025-02-16 18:10:00', 0);
INSERT INTO yw.problem_set (problem_set_id, ps_name, ps_author_id, ps_start_time, ps_end_time, duration) VALUES (3, 'Assignment 2 - basic data types and expressions', 2, '2025-02-18 15:56:00', '2025-02-18 16:04:00', 0);
INSERT INTO yw.problem_set (problem_set_id, ps_name, ps_author_id, ps_start_time, ps_end_time, duration) VALUES (4, 'Assignment 3 - Control Statements', 2, '2025-02-18 16:03:00', '2025-02-18 16:09:00', 0);
INSERT INTO yw.problem_set (problem_set_id, ps_name, ps_author_id, ps_start_time, ps_end_time, duration) VALUES (5, 'Assignment 4 - Functions', 5, '2025-02-18 16:06:00', '2025-02-25 18:12:00', 0);
INSERT INTO yw.problem_set (problem_set_id, ps_name, ps_author_id, ps_start_time, ps_end_time, duration) VALUES (6, 'Midterm Practice', 5, '2025-02-27 20:11:00', '2025-02-27 20:15:00', 0);
INSERT INTO yw.problem_set (problem_set_id, ps_name, ps_author_id, ps_start_time, ps_end_time, duration) VALUES (7, 'Midterm Exam', 5, '2025-02-19 20:16:00', '2025-02-19 20:20:00', 2);
INSERT INTO yw.problem_set (problem_set_id, ps_name, ps_author_id, ps_start_time, ps_end_time, duration) VALUES (8, 'Final Exam', 2, '2025-03-01 18:30:00', '2025-03-01 19:45:00', 60);
INSERT INTO yw.problem_set (problem_set_id, ps_name, ps_author_id, ps_start_time, ps_end_time, duration) VALUES (9, 'TEST12345', 1, '2025-03-04 23:00:00', '2025-03-04 23:40:00', 2);
INSERT INTO yw.problem_set (problem_set_id, ps_name, ps_author_id, ps_start_time, ps_end_time, duration) VALUES (10, 'TEST 2', 2, '2025-03-10 17:08:00', '2025-03-11 19:43:00', 2);
INSERT INTO yw.problem_set (problem_set_id, ps_name, ps_author_id, ps_start_time, ps_end_time, duration) VALUES (11, 'ASSI 3', 1, '2025-03-10 17:22:00', '2025-03-11 19:42:00', 0);
