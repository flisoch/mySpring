package ru.itis.mySpring.demoFiles.repositories;

import javax.sql.DataSource;

public interface UserRepository extends Repository {
    DataSource getDataSource();
}
