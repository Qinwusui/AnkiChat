CREATE SCHEMA `chat` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_mysql500_ci ;

use chat;
-- 创建用户表
create table users
(
    `index`          int unique auto_increment,
    user_id          varchar(42) not null,
    name             varchar(32) null,
    pwd              varchar(64) not null,
    icon_url         text        null,
    last_online_time long        null,
    constraint users_pk
        primary key (user_id)
);
-- 群组表
create table `groups`
(
    `index`     int unique auto_increment,
    group_id    varchar(42) not null,
    name        text        null,
    creator_id  varchar(42) not null,
    foreign key (creator_id) references users (user_id),
    owner_id    varchar(42) not null,
    foreign key (owner_id) references users (user_id),
    create_time long        null,
    constraint groups_pk
        primary key (group_id)
);
-- 群组成员表
create table group_members
(
    `index`  int auto_increment,
    group_id varchar(42) not null,

    foreign key (group_id) references `groups` (group_id),
    user_id  varchar(42) not null,

    foreign key (user_id) references users (user_id),
    constraint group_members_pk
        primary key (`index`)
);

-- 群组管理员表
create table group_admins
(
    `index`  int auto_increment,
    group_id varchar(42) not null,
    foreign key (group_id) references `groups` (group_id),
    user_id  varchar(42) not null,
    foreign key (user_id) references users (user_id),
    constraint group_admins_pk
        primary key (`index`)
);

-- 消息表
create table messages
(
    `index`      int unique auto_increment,
    message_id   varchar(42) not null,
    from_id      varchar(42) not null,
    foreign key (from_id) references users (user_id),
    to_id        varchar(42) null,
    foreign key (to_id) references users (user_id),
    to_group_id     varchar(42) null,
    foreign key (to_group_id) references `groups` (group_id),
    message_type text        not null,
    content      text        not null,
    send_time    long        not null,
    constraint messages_pk
        primary key (message_id)
);

-- 好友表
create table friends
(
    user_id   varchar(42) not null,
    foreign key (user_id) references users (user_id),
    friend_id varchar(42) not null,
    foreign key (friend_id) references users (user_id),
    `index`   int auto_increment,
    constraint friends_pk
        primary key (`index`)
);

-- 好友申请表
create table friends_applies
(
    `index`       int unique auto_increment,
    apply_id      varchar(42) not null,
    send_id       varchar(42) not null,
    receive_id    varchar(42) not null,
    apply_message text        not null,
    send_time     long        null,
    constraint friends_applies_pk
        primary key (apply_id)
);

