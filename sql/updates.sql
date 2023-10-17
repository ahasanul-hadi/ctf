# Settings Table
INSERT INTO `settings` (`id`, `end_time`, `event_name`, `is_scoreboard_frozen`, `start_time`) VALUES ('1', '2023-10-21 18:00:24.708000', 'Cyberdrill 2023', b'0', '2023-10-21 10:00:24.708000');


# User Table
INSERT INTO `users` (`id`, `account_non_expired`, `account_non_locked`, `address`, `avatar_id`, `credentials_non_expired`, `designation`, `education`, `email`, `enabled`, `mobile`, `name`, `password`, `personal_info`, `role`, `team_id`) VALUES
(2, b'1', b'1', NULL, '', b'1', 'App Dev', NULL, 'user@cirt', b'1', '+8801723808031', 'Ahasanul Hadi', '$2a$10$JCt9OM7DEwVjffWkmcyyye73dI3g8VvgSkAiCtZxjAXJBma6aYVXC', NULL, 'MEMBER', NULL),
(1, b'1', b'1', NULL, '10', b'1', 'Soft dev', NULL, 'admin@cirt', b'1', '017238080', 'Ahasanul Hadi', '$2a$10$JCt9OM7DEwVjffWkmcyyye73dI3g8VvgSkAiCtZxjAXJBma6aYVXC', NULL, 'ADMIN', NULL);