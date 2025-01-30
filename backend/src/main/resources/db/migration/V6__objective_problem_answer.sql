create table objective_problem_answer
(
    objective_problem_answer_id int auto_increment
        primary key,
    author_id                   int           not null,
    objective_problem_id        int           not null,
    problem_set_id              int           not null,
    opa_actual_score            int           null,
    opa_actual_answer           varchar(1024) null,
    constraint objective_problem_answer_problem_set_problem_set_id_fk
        foreign key (problem_set_id) references problem_set (problem_set_id)
            on update cascade on delete cascade,
    constraint objective_problem_answer_user_user_id_fk
        foreign key (author_id) references user (user_id)
            on update cascade on delete cascade,
    constraint objective_problem_objective_problem_id_fk
        foreign key (objective_problem_id) references objective_problem (objective_problem_id)
            on update cascade on delete cascade
);

INSERT INTO yw.objective_problem_answer (objective_problem_answer_id, author_id, objective_problem_id, problem_set_id, opa_actual_score, opa_actual_answer) VALUES (28, 2, 2, 1, 8, 'C');
INSERT INTO yw.objective_problem_answer (objective_problem_answer_id, author_id, objective_problem_id, problem_set_id, opa_actual_score, opa_actual_answer) VALUES (29, 2, 4, 1, 5, 'A');
INSERT INTO yw.objective_problem_answer (objective_problem_answer_id, author_id, objective_problem_id, problem_set_id, opa_actual_score, opa_actual_answer) VALUES (30, 2, 5, 1, 0, 'B');
INSERT INTO yw.objective_problem_answer (objective_problem_answer_id, author_id, objective_problem_id, problem_set_id, opa_actual_score, opa_actual_answer) VALUES (31, 2, 3, 2, 0, 'T');
INSERT INTO yw.objective_problem_answer (objective_problem_answer_id, author_id, objective_problem_id, problem_set_id, opa_actual_score, opa_actual_answer) VALUES (32, 2, 4, 2, 0, 'C');
INSERT INTO yw.objective_problem_answer (objective_problem_answer_id, author_id, objective_problem_id, problem_set_id, opa_actual_score, opa_actual_answer) VALUES (37, 1, 2, 1, 8, 'C');
INSERT INTO yw.objective_problem_answer (objective_problem_answer_id, author_id, objective_problem_id, problem_set_id, opa_actual_score, opa_actual_answer) VALUES (38, 1, 4, 1, 5, 'A');
INSERT INTO yw.objective_problem_answer (objective_problem_answer_id, author_id, objective_problem_id, problem_set_id, opa_actual_score, opa_actual_answer) VALUES (39, 1, 5, 1, 8, 'C');
INSERT INTO yw.objective_problem_answer (objective_problem_answer_id, author_id, objective_problem_id, problem_set_id, opa_actual_score, opa_actual_answer) VALUES (40, 3, 2, 1, 0, 'B');
INSERT INTO yw.objective_problem_answer (objective_problem_answer_id, author_id, objective_problem_id, problem_set_id, opa_actual_score, opa_actual_answer) VALUES (41, 3, 4, 1, 5, 'A');
INSERT INTO yw.objective_problem_answer (objective_problem_answer_id, author_id, objective_problem_id, problem_set_id, opa_actual_score, opa_actual_answer) VALUES (42, 3, 5, 1, 8, 'C');
INSERT INTO yw.objective_problem_answer (objective_problem_answer_id, author_id, objective_problem_id, problem_set_id, opa_actual_score, opa_actual_answer) VALUES (43, 3, 3, 2, 8, 'F');
INSERT INTO yw.objective_problem_answer (objective_problem_answer_id, author_id, objective_problem_id, problem_set_id, opa_actual_score, opa_actual_answer) VALUES (44, 3, 4, 2, 0, 'D');
INSERT INTO yw.objective_problem_answer (objective_problem_answer_id, author_id, objective_problem_id, problem_set_id, opa_actual_score, opa_actual_answer) VALUES (46, 1, 3, 2, 0, 'T');
INSERT INTO yw.objective_problem_answer (objective_problem_answer_id, author_id, objective_problem_id, problem_set_id, opa_actual_score, opa_actual_answer) VALUES (47, 1, 4, 2, 5, 'A');
INSERT INTO yw.objective_problem_answer (objective_problem_answer_id, author_id, objective_problem_id, problem_set_id, opa_actual_score, opa_actual_answer) VALUES (69, 2, 7, 3, 5, 'A');
INSERT INTO yw.objective_problem_answer (objective_problem_answer_id, author_id, objective_problem_id, problem_set_id, opa_actual_score, opa_actual_answer) VALUES (70, 2, 10, 3, 8, 'A');
INSERT INTO yw.objective_problem_answer (objective_problem_answer_id, author_id, objective_problem_id, problem_set_id, opa_actual_score, opa_actual_answer) VALUES (71, 2, 25, 3, 5, 'D');
INSERT INTO yw.objective_problem_answer (objective_problem_answer_id, author_id, objective_problem_id, problem_set_id, opa_actual_score, opa_actual_answer) VALUES (72, 3, 7, 3, 0, 'B');
INSERT INTO yw.objective_problem_answer (objective_problem_answer_id, author_id, objective_problem_id, problem_set_id, opa_actual_score, opa_actual_answer) VALUES (73, 3, 10, 3, 8, 'A');
INSERT INTO yw.objective_problem_answer (objective_problem_answer_id, author_id, objective_problem_id, problem_set_id, opa_actual_score, opa_actual_answer) VALUES (74, 3, 25, 3, 5, 'D');
INSERT INTO yw.objective_problem_answer (objective_problem_answer_id, author_id, objective_problem_id, problem_set_id, opa_actual_score, opa_actual_answer) VALUES (75, 7, 23, 5, 5, 'C');
INSERT INTO yw.objective_problem_answer (objective_problem_answer_id, author_id, objective_problem_id, problem_set_id, opa_actual_score, opa_actual_answer) VALUES (76, 7, 24, 5, 5, 'D');
INSERT INTO yw.objective_problem_answer (objective_problem_answer_id, author_id, objective_problem_id, problem_set_id, opa_actual_score, opa_actual_answer) VALUES (77, 3, 23, 5, 5, 'C');
INSERT INTO yw.objective_problem_answer (objective_problem_answer_id, author_id, objective_problem_id, problem_set_id, opa_actual_score, opa_actual_answer) VALUES (78, 3, 24, 5, 0, 'B');
INSERT INTO yw.objective_problem_answer (objective_problem_answer_id, author_id, objective_problem_id, problem_set_id, opa_actual_score, opa_actual_answer) VALUES (79, 1, 6, 2, 5, 'T');
INSERT INTO yw.objective_problem_answer (objective_problem_answer_id, author_id, objective_problem_id, problem_set_id, opa_actual_score, opa_actual_answer) VALUES (80, 2, 6, 2, 0, 'F');
INSERT INTO yw.objective_problem_answer (objective_problem_answer_id, author_id, objective_problem_id, problem_set_id, opa_actual_score, opa_actual_answer) VALUES (98, 3, 9, 6, 8, 'B');
INSERT INTO yw.objective_problem_answer (objective_problem_answer_id, author_id, objective_problem_id, problem_set_id, opa_actual_score, opa_actual_answer) VALUES (99, 3, 10, 6, 0, 'C');
INSERT INTO yw.objective_problem_answer (objective_problem_answer_id, author_id, objective_problem_id, problem_set_id, opa_actual_score, opa_actual_answer) VALUES (100, 3, 22, 6, 8, 'B');
INSERT INTO yw.objective_problem_answer (objective_problem_answer_id, author_id, objective_problem_id, problem_set_id, opa_actual_score, opa_actual_answer) VALUES (101, 3, 3, 7, 8, 'F');
INSERT INTO yw.objective_problem_answer (objective_problem_answer_id, author_id, objective_problem_id, problem_set_id, opa_actual_score, opa_actual_answer) VALUES (102, 3, 17, 7, 8, 'C');
INSERT INTO yw.objective_problem_answer (objective_problem_answer_id, author_id, objective_problem_id, problem_set_id, opa_actual_score, opa_actual_answer) VALUES (103, 3, 21, 7, 8, 'C');
