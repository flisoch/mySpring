package ru.itis.mySpring.demoFiles.services;

import lombok.NoArgsConstructor;
import ru.itis.mySpring.demoFiles.repositories.UserRepository;

@NoArgsConstructor
public class AServiceImpl implements AService {
    UserRepository userRepository;

    public AServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
