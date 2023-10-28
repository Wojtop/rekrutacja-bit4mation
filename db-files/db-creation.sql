create table nodes
(
    id INT GENERATED ALWAYS AS IDENTITY
        primary key,
    value INT
);

alter table nodes
    owner to postgres;

create table connections
(
    id INT GENERATED ALWAYS AS IDENTITY,
    child_id  INT,
    parent_id INT,

    constraint child_unique unique (child_id),
    constraint fk_node_child
        foreign key (child_id)
            references nodes,
    constraint fk_node_parent
        foreign key (parent_id)
            references nodes
            on delete cascade,
    constraint pk_child_parent primary key (child_id, parent_id)

);
alter table connections
    owner to postgres;
