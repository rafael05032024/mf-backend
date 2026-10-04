ALTER TABLE user_post
    RENAME COLUMN link TO content;

ALTER TABLE user_post
    ALTER COLUMN content TYPE VARCHAR(255);

ALTER TABLE user_post
    ADD COLUMN description VARCHAR(255);
