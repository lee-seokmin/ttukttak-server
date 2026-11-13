-- ponytail: ddl-auto update가 못 바꾸는 기존 컬럼을 맞춰요. 몇 번 돌려도 결과가 같아요.
-- Flyway를 들이면 마이그레이션으로 옮기고 이 파일은 지워요.
alter table users alter column github_id drop not null;
alter table users
    alter column avatar_url type varchar(2048),
    alter column link_github type varchar(2048),
    alter column link_blog type varchar(2048);
