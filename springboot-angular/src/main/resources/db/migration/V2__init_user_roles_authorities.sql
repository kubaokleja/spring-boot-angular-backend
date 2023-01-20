INSERT INTO ROLES (id, name) VALUES(1, 'ROLE_USER');
INSERT INTO ROLES (id, name) VALUES(2, 'ROLE_ADMIN');

INSERT INTO AUTHORITIES (id, name) VALUES(1, 'user:read');
INSERT INTO AUTHORITIES (id, name) VALUES(2, 'user:create');
INSERT INTO AUTHORITIES (id, name) VALUES(3, 'user:update');
INSERT INTO AUTHORITIES (id, name) VALUES(4, 'user:delete');

INSERT INTO ROLES_AUTHORITIES(role_id, authority_id) VALUES (2,1);
INSERT INTO ROLES_AUTHORITIES(role_id, authority_id) VALUES (2,2);
INSERT INTO ROLES_AUTHORITIES(role_id, authority_id) VALUES (2,3);
INSERT INTO ROLES_AUTHORITIES(role_id, authority_id) VALUES (2,4);

INSERT INTO USERS (id, email, first_name, is_active, is_not_locked, join_date, last_name, password, user_id, username) VALUES (1, 'admin@test.pl', 'admin',b'1' ,b'1' , NOW(), 'admin','temporary','5129753162', 'admin');

INSERT INTO USERS_ROLES(user_id, role_id) VALUES (1,2);