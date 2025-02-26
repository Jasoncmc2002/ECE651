create table p_n_ps
(
    problem_set_id int not null,
    programming_id int not null,
    constraint ps_n_p_problem_set_problem_set_id_fk
        foreign key (problem_set_id) references problem_set (problem_set_id)
            on update cascade on delete cascade,
    constraint ps_n_p_programming_programming_id_fk
        foreign key (programming_id) references programming (programming_id)
            on update cascade on delete cascade
);

INSERT INTO yw.p_n_ps (problem_set_id, programming_id) VALUES (1, 1);
INSERT INTO yw.p_n_ps (problem_set_id, programming_id) VALUES (2, 1);
INSERT INTO yw.p_n_ps (problem_set_id, programming_id) VALUES (1, 2);
INSERT INTO yw.p_n_ps (problem_set_id, programming_id) VALUES (2, 6);
INSERT INTO yw.p_n_ps (problem_set_id, programming_id) VALUES (2, 2);
INSERT INTO yw.p_n_ps (problem_set_id, programming_id) VALUES (3, 5);
INSERT INTO yw.p_n_ps (problem_set_id, programming_id) VALUES (3, 4);
INSERT INTO yw.p_n_ps (problem_set_id, programming_id) VALUES (4, 3);
INSERT INTO yw.p_n_ps (problem_set_id, programming_id) VALUES (4, 7);
INSERT INTO yw.p_n_ps (problem_set_id, programming_id) VALUES (5, 6);
INSERT INTO yw.p_n_ps (problem_set_id, programming_id) VALUES (5, 2);
INSERT INTO yw.p_n_ps (problem_set_id, programming_id) VALUES (5, 8);
INSERT INTO yw.p_n_ps (problem_set_id, programming_id) VALUES (6, 14);
INSERT INTO yw.p_n_ps (problem_set_id, programming_id) VALUES (6, 11);
INSERT INTO yw.p_n_ps (problem_set_id, programming_id) VALUES (7, 12);
INSERT INTO yw.p_n_ps (problem_set_id, programming_id) VALUES (7, 9);
