
/* User 1 */
INSERT INTO `users` (`userName`, `password`, `accountNonExpired`, `email`, `firstName`, `lastName`, `accountNonLocked`, `credentialsNonExpired`, `enabled`) VALUES
('User1', '@c3po1234', 0, NULL, NULL, NULL, 0, 0, 0);
select max(id) from users into @id;
INSERT INTO `roles` (`name`, `userId`) VALUES ('ROLE_USER', @id);
insert into `accumuloroles` (`name`, `userId`) value("Doc1", @id);

/* User 2 */
INSERT INTO `users` (`userName`, `password`, `accountNonExpired`, `email`, `firstName`, `lastName`, `accountNonLocked`, `credentialsNonExpired`, `enabled`) VALUES
('User2', '@c3po1234', 0, NULL, NULL, NULL, 0, 0, 0);
select max(id) from users into @id;
INSERT INTO `roles` (`name`, `userId`) VALUES ('ROLE_USER', @id);
insert into `accumuloroles` (`name`, `userId`) value("Doc2", @id);

/* User 3 */
INSERT INTO `users` (`userName`, `password`, `accountNonExpired`, `email`, `firstName`, `lastName`, `accountNonLocked`, `credentialsNonExpired`, `enabled`) VALUES
('User3', '@c3po1234', 0, NULL, NULL, NULL, 0, 0, 0);
select max(id) from users into @id;
INSERT INTO `roles` (`name`, `userId`) VALUES ('ROLE_USER', @id);
insert into `accumuloroles` (`name`, `userId`) value("Doc3", @id);

/* User 4 */
INSERT INTO `users` (`userName`, `password`, `accountNonExpired`, `email`, `firstName`, `lastName`, `accountNonLocked`, `credentialsNonExpired`, `enabled`) VALUES
('User4', '@c3po1234', 0, NULL, NULL, NULL, 0, 0, 0);
select max(id) from users into @id;
INSERT INTO `roles` (`name`, `userId`) VALUES ('ROLE_USER', @id);
insert into `accumuloroles` (`name`, `userId`) value("Doc4", @id);

/* User 5 */
INSERT INTO `users` (`userName`, `password`, `accountNonExpired`, `email`, `firstName`, `lastName`, `accountNonLocked`, `credentialsNonExpired`, `enabled`) VALUES
('User5', '@c3po1234', 0, NULL, NULL, NULL, 0, 0, 0);
select max(id) from users into @id;
INSERT INTO `roles` (`name`, `userId`) VALUES ('ROLE_USER', @id);
insert into `accumuloroles` (`name`, `userId`) value("Doc5", @id);

/* Admin (accumulo), who has all the privileges */
INSERT INTO `users` (`userName`, `password`, `accountNonExpired`, `email`, `firstName`, `lastName`, `accountNonLocked`, `credentialsNonExpired`, `enabled`) VALUES
('admin', '@c3po1234', 0, NULL, NULL, NULL, 0, 0, 0);

select max(id) from users into @id;

INSERT INTO `roles` (`name`, `userId`) VALUES ('ROLE_USER', @id);
insert into `accumuloroles` (`name`, `userId`) value("Doc1", @id);
insert into `accumuloroles` (`name`, `userId`) value("Doc2", @id);
insert into `accumuloroles` (`name`, `userId`) value("Doc3", @id);
insert into `accumuloroles` (`name`, `userId`) value("Doc4", @id);
insert into `accumuloroles` (`name`, `userId`) value("Doc5", @id);

commit;
