INSERT INTO roles (id, name) VALUES(1, 'ROLE_USER');
INSERT INTO roles (id, name) VALUES(2, 'ROLE_ADMIN');

INSERT INTO authorities (id, name) VALUES(1, 'user:read');
INSERT INTO authorities (id, name) VALUES(2, 'user:create');
INSERT INTO authorities (id, name) VALUES(3, 'user:update');
INSERT INTO authorities (id, name) VALUES(4, 'user:delete');

INSERT INTO roles_authorities(role_id, authority_id) VALUES (2,1);
INSERT INTO roles_authorities(role_id, authority_id) VALUES (2,2);
INSERT INTO roles_authorities(role_id, authority_id) VALUES (2,3);
INSERT INTO roles_authorities(role_id, authority_id) VALUES (2,4);

INSERT INTO users (id, email, first_name, is_active, is_not_locked, join_date, last_name, password, user_id, username) VALUES (1, 'admin@test.pl', 'admin',b'1' ,b'1' , NOW(), 'admin','temporary','5129753162', 'admin');

INSERT INTO users_roles(user_id, role_id) VALUES (1,2);