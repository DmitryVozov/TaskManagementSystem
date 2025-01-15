delete from "user"
    where id not in (
        '2bc86005-4208-45da-a289-99c9e8c5d432',
        '8f7985de-a578-4419-b93f-ff9d29969b11'
    );

delete from task;