create table if not exists USERS
(
    id int not null primary key AUTO_INCREMENT,
    email varchar(255) not null,
    login varchar(255) not null,
    name varchar(255),
    birthday date
);

create table if not exists MPA
(
    id int not null primary key AUTO_INCREMENT,
    name varchar(255)
);

create table if not exists FILMS
(
    id int not null primary key AUTO_INCREMENT,
    name varchar(255),
    description varchar(255),
    duration int,
    release_date date,
    mpa_id int references MPA (id)
);

create table if not exists FRIENDS
(
    user_id int not null references USERS (id),
    friend_id int not null references USERS (id),
    status BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (user_id, friend_id),
    UNIQUE (user_id, friend_id)
);

create table if not exists GENRE
(
    id int not null primary key AUTO_INCREMENT,
    name varchar(255)
);

create table if not exists FILM_GENRE
(
    id_film int not null references FILMS (id),
    id_genre int not null references GENRE (id),
    PRIMARY KEY (id_film, id_genre)
);

create table if not exists LIKES
(
    id_film int not null references FILMS (id),
    user_id int not null references USERS (id),
    PRIMARY KEY (id_film, user_id)
);