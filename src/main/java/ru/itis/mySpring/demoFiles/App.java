package ru.itis.mySpring.demoFiles;

import ru.itis.mySpring.context.ApplicationContext;
import ru.itis.mySpring.context.ApplicationContextImpl;
import ru.itis.mySpring.demoFiles.repositories.UserRepository;
import ru.itis.mySpring.demoFiles.services.AService;
import ru.itis.mySpring.demoFiles.services.UserService;

public class App {
    public static void main(String[] args) {
        ApplicationContext context = ApplicationContextImpl.getContext();


        AService aService = context.getComponent(AService.class);
        UserService userService = context.getComponent(UserService.class);
        UserRepository userRepository = context.getComponent(UserRepository.class);

        //debug to see kishki
    }
}
