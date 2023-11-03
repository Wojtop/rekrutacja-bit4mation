create table nodes
(
    id    INT NOT NULL,
    value INT NOT NULL DEFAULT 0
);

alter table nodes
    owner to postgres;

create table connections
(
    id        INT NOT NULL,
    child_id  INT NOT NULL,
    parent_id INT NOT NULL
);
alter table connections
    owner to postgres;

INSERT INTO public.nodes (id, value)
VALUES (1, 100);
INSERT INTO public.nodes (id, value)
VALUES (2, 110);
INSERT INTO public.nodes (id, value)
VALUES (3, 111);
INSERT INTO public.nodes (id, value)
VALUES (4, 112);
INSERT INTO public.nodes (id, value)
VALUES (5, 120);
INSERT INTO public.nodes (id, value)
VALUES (6, 121);
INSERT INTO public.nodes (id, value)
VALUES (7, 122);
INSERT INTO public.nodes (id, value)
VALUES (8, 130);


INSERT INTO public.connections (id, child_id, parent_id)
VALUES (1, 2, 1);
INSERT INTO public.connections (id, child_id, parent_id)
VALUES (2, 3, 2);
INSERT INTO public.connections (id, child_id, parent_id)
VALUES (3, 4, 2);
INSERT INTO public.connections (id, child_id, parent_id)
VALUES (4, 5, 1);
INSERT INTO public.connections (id, child_id, parent_id)
VALUES (5, 6, 5);
INSERT INTO public.connections (id, child_id, parent_id)
VALUES (6, 7, 6);
INSERT INTO public.connections (id, child_id, parent_id)
VALUES (7, 8, 1);

alter table nodes
    add constraint pk
        primary key (id);
alter table nodes
    alter column id add GENERATED ALWAYS AS IDENTITY (START WITH 100 increment by 1);

alter table connections
    alter column id add GENERATED ALWAYS AS IDENTITY (START WITH 100 increment by 1);

alter table connections
    add
        constraint child_unique unique (child_id),
    add
        constraint fk_node_child
            foreign key (child_id)
                references nodes
                on delete cascade,
    add
        constraint fk_node_parent
            foreign key (parent_id)
                references nodes
                on delete cascade,
    add
        constraint pk_child_parent primary key (child_id, parent_id);

