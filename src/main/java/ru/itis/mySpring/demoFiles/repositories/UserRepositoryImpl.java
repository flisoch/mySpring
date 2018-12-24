package ru.itis.mySpring.demoFiles.repositories;

import lombok.NoArgsConstructor;

import javax.sql.DataSource;

@NoArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public UserRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }
}
