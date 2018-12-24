package ru.itis.mySpring.demoFiles.services;

import lombok.NoArgsConstructor;
import ru.itis.mySpring.demoFiles.repositories.UserRepository;


@NoArgsConstructor
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    @Override
    public void insert() {

    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
