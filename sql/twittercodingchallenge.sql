
CREATE DATABASE IF NOT EXISTS twittercodingchallenge DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE twittercodingchallenge;

DROP TABLE IF EXISTS User;
CREATE TABLE IF NOT EXISTS User (
  id int(11) NOT NULL PRIMARY KEY AUTO_INCREMENT,
  username varchar(50) NOT NULL UNIQUE,
  name varchar(50) NOT NULL,
  surname varchar(50) NOT NULL,
  email varchar(50) NOT NULL UNIQUE,
  password char(40) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS Follows;
CREATE TABLE IF NOT EXISTS Follows (
  follower int(11) NOT NULL,
  followed int(11) NOT NULL,
  PRIMARY KEY(follower, followed),
  FOREIGN KEY(follower) REFERENCES User(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  FOREIGN KEY(followed) REFERENCES User(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS Tweet;
CREATE TABLE IF NOT EXISTS Tweet (
  id int(11) NOT NULL  PRIMARY KEY AUTO_INCREMENT,
  date datetime NOT NULL,
  text text NOT NULL,
  user_id int(11) NOT NULL,
  FOREIGN KEY(user_id) REFERENCES User(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
