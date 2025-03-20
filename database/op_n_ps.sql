create table op_n_ps
(
    objective_problem_id int not null,
    problem_set_id       int not null,
    constraint op_n_ps_objective_problem_objective_problem_id_fk
        foreign key (objective_problem_id) references objective_problem (objective_problem_id)
            on update cascade on delete cascade,
    constraint op_n_ps_problem_set_problem_set_id_fk
        foreign key (problem_set_id) references problem_set (problem_set_id)
            on update cascade on delete cascade
);

INSERT INTO yw.op_n_ps (objective_problem_id, problem_set_id) VALUES (4, 2);
INSERT INTO yw.op_n_ps (objective_problem_id, problem_set_id) VALUES (3, 2);
INSERT INTO yw.op_n_ps (objective_problem_id, problem_set_id) VALUES (5, 1);
INSERT INTO yw.op_n_ps (objective_problem_id, problem_set_id) VALUES (2, 1);
INSERT INTO yw.op_n_ps (objective_problem_id, problem_set_id) VALUES (4, 1);
INSERT INTO yw.op_n_ps (objective_problem_id, problem_set_id) VALUES (7, 3);
INSERT INTO yw.op_n_ps (objective_problem_id, problem_set_id) VALUES (10, 3);
INSERT INTO yw.op_n_ps (objective_problem_id, problem_set_id) VALUES (25, 3);
INSERT INTO yw.op_n_ps (objective_problem_id, problem_set_id) VALUES (24, 5);
INSERT INTO yw.op_n_ps (objective_problem_id, problem_set_id) VALUES (23, 5);
INSERT INTO yw.op_n_ps (objective_problem_id, problem_set_id) VALUES (6, 2);
INSERT INTO yw.op_n_ps (objective_problem_id, problem_set_id) VALUES (25, 5);
INSERT INTO yw.op_n_ps (objective_problem_id, problem_set_id) VALUES (9, 6);
INSERT INTO yw.op_n_ps (objective_problem_id, problem_set_id) VALUES (10, 6);
INSERT INTO yw.op_n_ps (objective_problem_id, problem_set_id) VALUES (22, 6);
INSERT INTO yw.op_n_ps (objective_problem_id, problem_set_id) VALUES (17, 7);
INSERT INTO yw.op_n_ps (objective_problem_id, problem_set_id) VALUES (21, 7);
INSERT INTO yw.op_n_ps (objective_problem_id, problem_set_id) VALUES (3, 7);
INSERT INTO yw.op_n_ps (objective_problem_id, problem_set_id) VALUES (1, 9);
INSERT INTO yw.op_n_ps (objective_problem_id, problem_set_id) VALUES (3, 9);
INSERT INTO yw.op_n_ps (objective_problem_id, problem_set_id) VALUES (12, 10);
INSERT INTO yw.op_n_ps (objective_problem_id, problem_set_id) VALUES (15, 10);
INSERT INTO yw.op_n_ps (objective_problem_id, problem_set_id) VALUES (8, 10);
INSERT INTO yw.op_n_ps (objective_problem_id, problem_set_id) VALUES (20, 10);
INSERT INTO yw.op_n_ps (objective_problem_id, problem_set_id) VALUES (23, 10);
INSERT INTO yw.op_n_ps (objective_problem_id, problem_set_id) VALUES (11, 11);
INSERT INTO yw.op_n_ps (objective_problem_id, problem_set_id) VALUES (16, 11);
INSERT INTO yw.op_n_ps (objective_problem_id, problem_set_id) VALUES (8, 11);
