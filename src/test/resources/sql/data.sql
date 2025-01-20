insert into "user" (id, email, password, username)
select  '2bc86005-4208-45da-a289-99c9e8c5d432',
        'admin@gmail.com',
        '$2a$10$6FHJ4ZXaiTwCbyhz4TfxWe8fVcM1W5zACYJkYDCe2phlqFCOFv7.6',
        'admin';

insert into "user" (id, email, password, username)
select  '8f7985de-a578-4419-b93f-ff9d29969b11',
        'test@gmail.com',
        '$2a$10$lt.bXMK2hwSjtPGLkSAQPOIWMFK6l.LJ8crNZVj/pGQdGLayrVfKa',
        'test';

insert into role (id, name)
select  'dd0c4d6b-e7b0-48e4-995b-7022f43d733d',
        'ROLE_ADMIN'
    where not exists (
        select 1
            from role
            where id = 'dd0c4d6b-e7b0-48e4-995b-7022f43d733d'
    );

insert into role (id, name)
select  '1ad52bd4-c168-4e1f-aaeb-9c6265669836',
        'ROLE_USER'
    where not exists (
        select 1
            from role
            where id = '1ad52bd4-c168-4e1f-aaeb-9c6265669836'
    );

insert into user_role (user_id, role_id)
select  '2bc86005-4208-45da-a289-99c9e8c5d432',
        'dd0c4d6b-e7b0-48e4-995b-7022f43d733d';

insert into user_role (user_id, role_id)
select  '8f7985de-a578-4419-b93f-ff9d29969b11',
        '1ad52bd4-c168-4e1f-aaeb-9c6265669836';

insert into task (id, title, description, priority, status, author_id, executor_id)
select  'ea8efca6-8625-4686-8bf8-7c4153d9666e',
        'test',
        'test',
        'HIGH',
        'IN_PROGRESS',
        '2bc86005-4208-45da-a289-99c9e8c5d432',
        '8f7985de-a578-4419-b93f-ff9d29969b11';

insert into task (id, title, description, priority, status, author_id, executor_id)
select  'b2f1c5b0-31b1-4a15-9ce0-d20300965218',
        'title',
        'desc',
        'MEDIUM',
        'TODO',
        '2bc86005-4208-45da-a289-99c9e8c5d432',
        null;

insert into comment (id, text, created_at, task_id, commentator_id)
select  'd061c985-a0f6-420f-b49f-a85971fb27e4',
        'I am almost done',
        CURRENT_TIMESTAMP(),
        'ea8efca6-8625-4686-8bf8-7c4153d9666e',
        '8f7985de-a578-4419-b93f-ff9d29969b11';

insert into comment (id, text, created_at, task_id, commentator_id)
select  '8ee7714a-f747-4a39-b589-9bcffc6db573',
        'We need to finish this task faster.',
        CURRENT_TIMESTAMP(),
        'ea8efca6-8625-4686-8bf8-7c4153d9666e',
        '2bc86005-4208-45da-a289-99c9e8c5d432';


