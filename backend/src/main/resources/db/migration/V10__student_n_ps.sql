create table student_n_ps
(
    student_id       int      not null,
    problem_set_id   int      not null,
    first_start_time datetime null,
    constraint student_n_ps_problem_set_problem_set_id_fk
        foreign key (problem_set_id) references problem_set (problem_set_id)
            on update cascade on delete cascade,
    constraint student_n_ps_user_user_id_fk
        foreign key (student_id) references user (user_id)
            on update cascade on delete cascade
);

INSERT INTO yw.student_n_ps (student_id, problem_set_id, first_start_time) VALUES (2, 2, '2024-04-15 17:50:29');
INSERT INTO yw.student_n_ps (student_id, problem_set_id, first_start_time) VALUES (3, 2, '2024-04-15 18:01:39');
INSERT INTO yw.student_n_ps (student_id, problem_set_id, first_start_time) VALUES (3, 1, '2024-04-15 18:00:47');
INSERT INTO yw.student_n_ps (student_id, problem_set_id, first_start_time) VALUES (2, 1, '2024-04-18 14:11:34');
INSERT INTO yw.student_n_ps (student_id, problem_set_id, first_start_time) VALUES (1, 1, '2024-04-18 10:13:19');
INSERT INTO yw.student_n_ps (student_id, problem_set_id, first_start_time) VALUES (1, 2, '2024-05-03 18:57:24');
INSERT INTO yw.student_n_ps (student_id, problem_set_id, first_start_time) VALUES (3, 3, '2024-05-18 16:01:51');
INSERT INTO yw.student_n_ps (student_id, problem_set_id, first_start_time) VALUES (2, 3, '2024-05-18 15:59:34');
INSERT INTO yw.student_n_ps (student_id, problem_set_id, first_start_time) VALUES (2, 4, '2024-05-18 16:04:42');
INSERT INTO yw.student_n_ps (student_id, problem_set_id, first_start_time) VALUES (3, 4, '2024-05-18 16:05:08');
INSERT INTO yw.student_n_ps (student_id, problem_set_id, first_start_time) VALUES (7, 5, '2024-05-18 16:07:03');
INSERT INTO yw.student_n_ps (student_id, problem_set_id, first_start_time) VALUES (3, 5, '2024-05-18 16:07:48');
INSERT INTO yw.student_n_ps (student_id, problem_set_id, first_start_time) VALUES (3, 6, '2024-05-27 20:13:13');
INSERT INTO yw.student_n_ps (student_id, problem_set_id, first_start_time) VALUES (3, 7, '2024-05-27 20:17:20');
