create table test_case
(
    test_case_id   int auto_increment
        primary key,
    programming_id int           not null,
    tc_input       varchar(1024) null,
    tc_output      varchar(1024) not null,
    constraint test_case_programming_programming_id_fk
        foreign key (programming_id) references programming (programming_id)
            on update cascade on delete cascade
);

INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (13, 1, '4 2 8', '2->4->8');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (14, 1, '1 2 3', '1->2->3');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (21, 2, '', 'Hello World!');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (25, 3, '10 a', 'aaaaaaaaaa
aaaaaaaaaa
aaaaaaaaaa
aaaaaaaaaa
aaaaaaaaaa');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (26, 3, '4 a', 'aaaa
aaaa');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (28, 5, '3 4', '7');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (29, 5, '1 2', '3');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (30, 5, '0 0', '0');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (31, 5, '100000000 100000000', '200000000');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (32, 5, '5 6', '11');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (33, 6, '1 3', '4
lalala');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (34, 6, '2 2', '4
lalala');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (35, 6, '2 3', '5
lalala');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (40, 7, '....', '....');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (41, 7, 'KDJIskos234k,.;djfeiJ', 'kdjiskos234k,.;djfeij');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (42, 7, 'AbCdEf', 'abcdef');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (43, 7, '1234', '1234');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (45, 8, '6 6 6', 'YES');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (46, 8, '1 2 3', 'NO');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (47, 8, '3 4 5', 'YES');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (48, 8, '1 2 2', 'YES');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (49, 9, 'abcda', 'NO');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (50, 9, 'abcba', 'YES');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (51, 9, 'ab', 'NO');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (52, 9, 'a', 'YES');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (53, 4, '2024-05-12', '05/12/2024');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (54, 4, '2024-05-01', '05/01/2024');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (55, 4, '1000-05-01', '05/01/1000');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (56, 4, '9999-12-31', '12/31/9999');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (59, 10, '2 8 3 50', '2');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (60, 10, '1000 2000 3000 5000', '13');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (61, 10, '1 2 3 4', '0');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (62, 10, '1234 5678 910 1112', '1');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (63, 10, '2 2 2 2 5 5 5 5 5', '4');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (64, 11, '2 8 3 50', '0');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (65, 11, '1 2 3 4', '0');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (66, 11, '5 5 5 5', '1');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (67, 11, '1235 7894 4568 1256', '0');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (68, 11, '9999 7777 1111 5555', '1');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (70, 12, '7', '3
lalalalala');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (71, 12, '123456789', '16
lalalalala');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (72, 12, '777766665555', '20
lalalalala');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (73, 12, '9999999966666666', '29
lalalalala');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (74, 13, '24 36', '6');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (75, 13, '1234 5678', '2');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (76, 13, '1111 2222', '4');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (77, 13, '31 97', '1');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (78, 13, '1 2', '1');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (97, 14, 'OurWorldIsFullOfL*O.._V-+E', 'SINGLE');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (98, 14, '...+=-LoVe+=$', 'LOVE');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (99, 14, 'OurWorldIsFullOfLOV.E', 'SINGLE');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (100, 14, 'LovE', 'LOVE');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (101, 14, 'OurWorldIsFullOfLOVE', 'LOVE');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (102, 15, 'ebullient 19', 'xuneebxgm');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (103, 15, 'illuminati 13', 'vyyhzvangv');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (104, 15, 'pneumonoultramicroscopicsilicovolcanoconiosis 17', 'gevldfeflckirdztifjtfgztjzcztfmfctreftfezfjzj');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (105, 15, 'countermand 23', 'zlrkqbojxka');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (118, 16, '1 2', '3');
INSERT INTO yw.test_case (test_case_id, programming_id, tc_input, tc_output) VALUES (119, 16, '1000000 1000000', '2000000');
