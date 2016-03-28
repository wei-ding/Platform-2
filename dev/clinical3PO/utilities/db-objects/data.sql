
/* USERNAMEHERE 1 */
INSERT INTO `users` (`userName`, `password`, `accountNonExpired`, `email`, `firstName`, `lastName`, `accountNonLocked`, `credentialsNonExpired`, `enabled`) VALUES
('USERNAMEHERE1', 'PASSWORDHERE', 0, NULL, NULL, NULL, 0, 0, 0);
select max(id) from users into @id;
INSERT INTO `roles` (`name`, `userId`) VALUES ('ROLE_USER', @id);
insert into `accumuloroles` (`name`, `userId`) value("Doc1", @id);

/* USERNAMEHERE 2 */
INSERT INTO `users` (`userName`, `password`, `accountNonExpired`, `email`, `firstName`, `lastName`, `accountNonLocked`, `credentialsNonExpired`, `enabled`) VALUES
('USERNAMEHERE2', 'PASSWORDHERE', 0, NULL, NULL, NULL, 0, 0, 0);
select max(id) from users into @id;
INSERT INTO `roles` (`name`, `userId`) VALUES ('ROLE_USER', @id);
insert into `accumuloroles` (`name`, `userId`) value("Doc2", @id);

/* USERNAMEHERE 3 */
INSERT INTO `users` (`userName`, `password`, `accountNonExpired`, `email`, `firstName`, `lastName`, `accountNonLocked`, `credentialsNonExpired`, `enabled`) VALUES
('USERNAMEHERE3', 'PASSWORDHERE', 0, NULL, NULL, NULL, 0, 0, 0);
select max(id) from users into @id;
INSERT INTO `roles` (`name`, `userId`) VALUES ('ROLE_USER', @id);
insert into `accumuloroles` (`name`, `userId`) value("Doc3", @id);

/* USERNAMEHERE 4 */
INSERT INTO `users` (`userName`, `password`, `accountNonExpired`, `email`, `firstName`, `lastName`, `accountNonLocked`, `credentialsNonExpired`, `enabled`) VALUES
('USERNAMEHERE4', 'PASSWORDHERE', 0, NULL, NULL, NULL, 0, 0, 0);
select max(id) from users into @id;
INSERT INTO `roles` (`name`, `userId`) VALUES ('ROLE_USER', @id);
insert into `accumuloroles` (`name`, `userId`) value("Doc4", @id);

/* USERNAMEHERE 5 */
INSERT INTO `users` (`userName`, `password`, `accountNonExpired`, `email`, `firstName`, `lastName`, `accountNonLocked`, `credentialsNonExpired`, `enabled`) VALUES
('USERNAMEHERE5', 'PASSWORDHERE', 0, NULL, NULL, NULL, 0, 0, 0);
select max(id) from users into @id;
INSERT INTO `roles` (`name`, `userId`) VALUES ('ROLE_USER', @id);
insert into `accumuloroles` (`name`, `userId`) value("Doc5", @id);

/* Admin (accumulo), who has all the privileges */
INSERT INTO `users` (`userName`, `password`, `accountNonExpired`, `email`, `firstName`, `lastName`, `accountNonLocked`, `credentialsNonExpired`, `enabled`) VALUES
('admin', 'PASSWORDHERE', 0, NULL, NULL, NULL, 0, 0, 0);

select max(id) from users into @id;

INSERT INTO `roles` (`name`, `userId`) VALUES ('ROLE_USER', @id);
insert into `accumuloroles` (`name`, `userId`) value("Doc1", @id);
insert into `accumuloroles` (`name`, `userId`) value("Doc2", @id);
insert into `accumuloroles` (`name`, `userId`) value("Doc3", @id);
insert into `accumuloroles` (`name`, `userId`) value("Doc4", @id);
insert into `accumuloroles` (`name`, `userId`) value("Doc5", @id);

commit;
