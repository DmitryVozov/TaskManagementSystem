create table if not exists "user" (
    id UUID default gen_random_uuid() primary key,
    email varchar(255) unique not null,
    password varchar(255) not null,
    username varchar(30) not null
);

create table if not exists role (
    id UUID default gen_random_uuid() primary key,
    name varchar(15) unique not null
);

create table if not exists user_role (
    user_id UUID references "user"(id) on delete cascade,
    role_id UUID references role(id),
    primary key (user_id, role_id)
);

create table if not exists task (
    id UUID default gen_random_uuid() primary key,
    title varchar(127) not null,
    description text not null,
    priority varchar(10) not null check (priority in ('LOW','MEDIUM','HIGH')),
    status varchar(15) not null check (status in ('TODO','IN_PROGRESS','DONE')),
    author_id UUID references "user"(id) on delete set null,
    executor_id UUID references "user"(id) on delete set null
);

create table if not exists comment (
    id UUID default gen_random_uuid() primary key,
    text text not null,
    created_at timestamp not null,
    task_id UUID references task(id) on delete cascade,
    commentator_id UUID references "user"(id) on delete set null
);