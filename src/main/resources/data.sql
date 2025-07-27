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