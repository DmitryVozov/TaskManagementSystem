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
        'ROLE_ADMIN';


insert into role (id, name)
select  '1ad52bd4-c168-4e1f-aaeb-9c6265669836',
        'ROLE_USER';

insert into user_role (user_id, role_id)
select  '2bc86005-4208-45da-a289-99c9e8c5d432',
        'dd0c4d6b-e7b0-48e4-995b-7022f43d733d';

insert into user_role (user_id, role_id)
select  '8f7985de-a578-4419-b93f-ff9d29969b11',
        '1ad52bd4-c168-4e1f-aaeb-9c6265669836';


