package lt.fivethreads.services;

import lt.fivethreads.entities.Office;
import lt.fivethreads.entities.User;
import lt.fivethreads.entities.request.ChangePasswordForm;
import lt.fivethreads.entities.request.RegistrationForm;
import lt.fivethreads.entities.request.ExtendedUserDTO;
import lt.fivethreads.exception.file.EmailAlreadyExists;
import lt.fivethreads.exception.file.EmailNotExists;
import lt.fivethreads.exception.file.UserIDNotExists;
import lt.fivethreads.mapper.UserMapper;
import lt.fivethreads.repositories.UserRepository;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserServiceImplementation implements UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserMapper userMapper;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    MailService mailService;

    @Autowired
    UserCreationService userCreationService;

    @Autowired
    OfficeService officeService;

    @Override
    public User getUserByEmail(String email) throws UserIDNotExists
    {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserIDNotExists());
        return user;
    }

    @Override
    public List<ExtendedUserDTO> getAllUser() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(e -> userMapper.getUserDTO(e))
                .collect(Collectors.toList());
    }

    @Override
    public User getUserByID(Long id) throws UserIDNotExists {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserIDNotExists());
        return user;
    }

    public ExtendedUserDTO getUserDTOByID(Long id) throws UserIDNotExists {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserIDNotExists());
        return userMapper.getUserDTO(user);
    }

    @Override
    public User updateUser(ExtendedUserDTO userDTO) {
        User user = userRepository.findById(userDTO.getId())
                .orElseThrow(() -> new UserIDNotExists());
        if (this.checkIfEmailExists(userDTO.getEmail()) && !userDTO.getEmail().equals(user.getEmail())) {
            throw new EmailAlreadyExists();
        }
        user.setEmail(userDTO.getEmail());
        user.setFirstname(userDTO.getFirstname());
        user.setLastName(userDTO.getLastname());
        user.setId(userDTO.getId());
        user.setPhone(userDTO.getPhone());
        user.setRoles(userMapper.getRoles(userDTO.getRole()));
        if(!(userDTO.getOfficeId() == null)){
            user.setOffice(officeService.getOfficeById(userDTO.getOfficeId()));
        }

        return userRepository.saveAndFlush(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public boolean checkIfEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public void createUsers(List<ExtendedUserDTO> users) {
        for (ExtendedUserDTO user: users) {
            User userEntity = userMapper.getUser(user);
            userCreationService.createNewUser(userEntity);
        }
    }

    @Override
    public User createUser(RegistrationForm user) {
        User user_to_create = userMapper.convertRegistrationUserToUser(user);
        User created_user = userCreationService.createNewUser(user_to_create);
        return created_user;
    }

    @Override
    public void changePassword(ChangePasswordForm changePasswordForm, String email) throws EmailNotExists {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotExists());
        user.setPassword(encoder.encode(changePasswordForm.getPassword()));
        userRepository.save(user);
    }

    @Override
    public Boolean checkIfModified(Long userID, String version){
        User user = userRepository.findById(userID)
                .orElseThrow(() -> new UserIDNotExists());
        String current_version = user.getVersion().toString();
        return !version.equals(current_version);
    }

}
