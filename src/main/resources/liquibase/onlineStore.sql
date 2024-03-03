-- liquibase formatted sql
--changeset alexander:create_user
DROP TABLE IF EXISTS ad_comment;
DROP TABLE IF EXISTS ad;
DROP TABLE IF EXISTS public.user;
CREATE TABLE public.user
(

    id         SERIAL PRIMARY KEY,
    username   VARCHAR(32)  NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    first_name VARCHAR(16)  NOT NULL,
    last_name  VARCHAR(16),
    phone      VARCHAR(16),
    role       VARCHAR(16)  NOT NULL,
    image      OID
);


--changeset alexander:create_ad
CREATE TABLE ad
(
    id          SERIAL PRIMARY KEY,
    user_id     INTEGER     NOT NULL,
    title       VARCHAR(32) NOT NULL,
    price       INTEGER     NOT NULL,
    description VARCHAR(64) NOT NULL,
    image       OID,
    FOREIGN KEY (user_id) REFERENCES public.user (id)
);

--changeset alexander:create_ad_comment
CREATE TABLE ad_comment
(
    id         SERIAL PRIMARY KEY,
    text       VARCHAR(64) NOT NULL,
    created_at TIMESTAMP   NOT NULL,
    ad_id      INTEGER     NOT NULL,
    user_id    INTEGER     NOT NULL,
    FOREIGN KEY (ad_id) REFERENCES ad (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES public.user (id)
);
