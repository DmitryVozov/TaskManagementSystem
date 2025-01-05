insert into "user" (id, email, password, username)
select  '2bc86005-4208-45da-a289-99c9e8c5d432',
        'admin@gmail.com',
        '$2a$10$6FHJ4ZXaiTwCbyhz4TfxWe8fVcM1W5zACYJkYDCe2phlqFCOFv7.6',
        'admin'
    where not exists (
        select 1
            from "user"
            where username = 'admin'
    );

insert into role (id, name)
select  'dd0c4d6b-e7b0-48e4-995b-7022f43d733d',
        'ROLE_ADMIN'
    where not exists (
        select 1
            from role
            where name = 'ROLE_ADMIN'
    );


insert into role (name)
select 'ROLE_USER'
    where not exists (
        select 1
            from role
            where name = 'ROLE_USER'
    );


insert into user_role (user_id, role_id)
select  '2bc86005-4208-45da-a289-99c9e8c5d432',
        'dd0c4d6b-e7b0-48e4-995b-7022f43d733d'
    where not exists (
        select 1
            from user_role
            where user_id = '2bc86005-4208-45da-a289-99c9e8c5d432'
                and role_id = 'dd0c4d6b-e7b0-48e4-995b-7022f43d733d'
    );

