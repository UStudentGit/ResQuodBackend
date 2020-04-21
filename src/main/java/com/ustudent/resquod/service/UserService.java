package com.ustudent.resquod.service;

import com.ustudent.resquod.exception.EmailExistException;
import com.ustudent.resquod.exception.InvalidInputException;
import com.ustudent.resquod.exception.InvalidPasswordException;
import com.ustudent.resquod.model.User;
import com.ustudent.resquod.model.dao.LoginUserData;
import com.ustudent.resquod.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Configurable(preConstruction = true, autowire = Autowire.BY_NAME)
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public void validateLoginData(LoginUserData userInput) {
        if (userInput.getEmail() == null || userInput.getPassword() == null ||
                userInput.getEmail().isEmpty() || userInput.getPassword().isEmpty())
            throw new InvalidInputException();
    }

    public LoginUserData getUserDataIfExist(String email) throws EmailExistException {
        return userRepository.findUserPassword(email).orElseThrow(EmailExistException::new);
    }

    public void checkIfMailExist(String email) throws EmailExistException {
        if (userRepository.findByEmail(email).isPresent())
            throw new EmailExistException();
    }

    public void validateRegistrationData(User inputData) throws InvalidInputException {
        if (inputData.getEmail() == null || inputData.getEmail().length() < 2 || !inputData.getEmail().contains("@") ||
                inputData.getName() == null || inputData.getName().length() < 2 ||
                inputData.getSurname() == null || inputData.getSurname().length() < 2 ||
                inputData.getPassword() == null || inputData.getPassword().length() < 6)
            throw new InvalidInputException();
    }

    public void addUser(User inputData) throws RuntimeException {
        userRepository.save(new User(inputData.getName(),
                inputData.getSurname(),
                inputData.getEmail(),
                hashPassword(inputData.getPassword())));
    }

    private String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    public Boolean verifyPassword(String password, String hash) throws InvalidPasswordException {
        if (!passwordEncoder.matches(password, hash)) throw new InvalidPasswordException();
        return true;
    }

    public void validateUserData(User userInput) throws InvalidInputException {
        if (userInput.getEmail() == null || userInput.getEmail().length() < 2 ||
                !userInput.getEmail().contains("@") ||
                userInput.getPassword() == null ||
                userRepository.findByEmail(userInput.getEmail()).isEmpty() ||
                userInput.getName() == null || userInput.getName().length() < 2 ||
                userInput.getSurname() == null || userInput.getSurname().length() < 2)
            throw new InvalidInputException();
    }

    public void updateUserData(User userInput) throws InvalidPasswordException {
        User user = userRepository.findByEmail(userInput.getEmail()).get();
        verifyPassword(userInput.getPassword(), user.getPassword());
        user.setName(userInput.getName());
        user.setSurname(userInput.getSurname());
        user.setEmail(userInput.getEmail());
        userRepository.save(user);
    }
}
