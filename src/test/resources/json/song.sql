create table Song
(
    id     bigint auto_increment primary key,
    title  varchar(255)  not null,
    artist varchar(255)  not null,
    year   int           null,
    json   varchar(1000) null
);

