INSERT INTO users (id, email, provider, provider_id)
VALUES
    (1, 'test1@gmail.com', 'GOOGLE', 'test1@gmail.com');


INSERT INTO channel (id, manager_id)
VALUES
    (1, 1);


INSERT INTO user_channel (id, nickname, user_id, channel_id)
VALUES
    (1, 'nickname1', 1, 1);
