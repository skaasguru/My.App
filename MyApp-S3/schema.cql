
CREATE KEYSPACE myapp WITH REPLICATION = { 'class': 'SimpleStrategy', 'replication_factor': 1 };

USE myapp ;

CREATE TABLE users ( id UUID PRIMARY KEY, name TEXT, email TEXT, password TEXT );

CREATE TABLE contacts ( user_id UUID, id UUID, name TEXT, phone TEXT, PRIMARY KEY  (user_id, id ));

CREATE INDEX ON users( email );

INSERT INTO users (id, name, email, password) VALUES (30cb6d80-5482-11e8-b021-f1e498f37799, 'Admin', 'admin@example.com', 'password');

INSERT INTO contacts (user_id, id, name, phone) VALUES (30cb6d80-5482-11e8-b021-f1e498f37799, 5fc03087-d265-11e7-b8c6-83e29cd24f4c, 'SuperAdmin', '1234567890');

INSERT INTO contacts (user_id, id, name, phone) VALUES (30cb6d80-5482-11e8-b021-f1e498f37799, 3b662990-54f2-11e8-9332-51a0213a107d, 'HyperAdmin', '0987654321');

EXIT;
