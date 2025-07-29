<<<<<<< HEAD
MERGE INTO GENRE (id, name) KEY (id) VALUES (1, 'Комедия');
MERGE INTO GENRE (id, name) KEY (id) VALUES (2, 'Драма');
MERGE INTO GENRE (id, name) KEY (id) VALUES (3, 'Мультфильм');
MERGE INTO GENRE (id, name) KEY (id) VALUES (4, 'Триллер');
MERGE INTO GENRE (id, name) KEY (id) VALUES (5, 'Документальный');
MERGE INTO GENRE (id, name) KEY (id) VALUES (6, 'Боевик');

MERGE INTO MPA (id,name) KEY (id) VALUES (1,'G');
MERGE INTO MPA (id,name) KEY (id) VALUES (2,'PG');
MERGE INTO MPA (id,name) KEY (id) VALUES (3,'PG-13');
MERGE INTO MPA (id,name) KEY (id) VALUES (4,'R');
MERGE INTO MPA (id,name) KEY (id) VALUES (5,'NC-17');
=======
MERGE INTO friendship_status (name) KEY(name) VALUES ('PENDING_SENT');
MERGE INTO friendship_status (name) KEY(name) VALUES ('FRIENDS');
MERGE INTO friendship_status (name) KEY(name) VALUES ('DECLINED');

MERGE INTO genre (name) KEY(name) VALUES ('Комедия');
MERGE INTO genre (name) KEY(name) VALUES ('Драма');
MERGE INTO genre (name) KEY(name) VALUES ('Мультфильм');
MERGE INTO genre (name) KEY(name) VALUES ('Триллер');
MERGE INTO genre (name) KEY(name) VALUES ('Документальный');
MERGE INTO genre (name) KEY(name) VALUES ('Боевик');

MERGE INTO mpa (name) KEY(name) VALUES ('G');
MERGE INTO mpa (name) KEY(name) VALUES ('PG');
MERGE INTO mpa (name) KEY(name) VALUES ('PG-13');
MERGE INTO mpa (name) KEY(name) VALUES ('R');
MERGE INTO mpa (name) KEY(name) VALUES ('NC-17');
>>>>>>> 97c2343 (скопирован мейн для группового занятия)
