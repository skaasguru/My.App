CREATE DATABASE myapp;

USE myapp;

CREATE TABLE `users` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(50) NOT NULL,
	`email` VARCHAR(50) NOT NULL UNIQUE,
	`password` VARCHAR(50) NOT NULL,
	PRIMARY KEY (`id`)
)
ENGINE=InnoDB;


CREATE TABLE `contacts` (
	`id` INT NOT NULL AUTO_INCREMENT,
	`user_id` INT NOT NULL,
	`name` VARCHAR(50) NOT NULL,
	`phone` VARCHAR(50) NOT NULL,
	PRIMARY KEY (`id`),
	CONSTRAINT `FK__users` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
)
ENGINE=InnoDB;


INSERT INTO `users` (`name`, `email`, `password`) VALUES ('admin', 'admin@example.com', 'password');

INSERT INTO `contacts` (`user_id`, `name`, `phone`) VALUES ('1', 'SuperAdmin', '1234567890');
INSERT INTO `contacts` (`user_id`, `name`, `phone`) VALUES ('1', 'HyperAdmin', '0987654321');

quit;