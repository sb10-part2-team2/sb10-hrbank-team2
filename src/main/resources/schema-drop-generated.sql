
    set client_min_messages = WARNING;

    alter table if exists backups 
       drop constraint if exists FK7r1n4nfd1itl1odv2i9gkqa4d;

    alter table if exists change_log_diffs 
       drop constraint if exists FKbe7jjmt4xaxdjx2sj1k1xn837;

    alter table if exists employees 
       drop constraint if exists FK8if1byloc650qvkaxabyjveap;

    alter table if exists employees 
       drop constraint if exists FKkvdfh20m1ec8g52af6vcxnt8r;

    drop table if exists backups cascade;

    drop table if exists change_log_diffs cascade;

    drop table if exists change_logs cascade;

    drop table if exists department cascade;

    drop table if exists employees cascade;

    drop table if exists files cascade;
