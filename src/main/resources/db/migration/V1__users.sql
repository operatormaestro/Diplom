create table users (
    id serial primary key,
    username varchar(63) not null,
    password varchar(255) not null
);

insert into users (id, username, password)
values (1, 'semavin@mail.com', '$2a$15$/OwNrWhI15zUyKKuhscwBenu6NCbJPjyEmmfB9eyge3KsqTh2.Ky.');
