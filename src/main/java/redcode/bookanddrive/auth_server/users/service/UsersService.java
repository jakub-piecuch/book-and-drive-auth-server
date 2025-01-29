package redcode.bookanddrive.auth_server.users.service;

import static redcode.bookanddrive.auth_server.exceptions.ResourceNotFoundException.RESOURCE_NOT_FOUND;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import redcode.bookanddrive.auth_server.exceptions.ResourceNotFoundException;
import redcode.bookanddrive.auth_server.users.domain.UserEntity;
import redcode.bookanddrive.auth_server.users.model.User;
import redcode.bookanddrive.auth_server.users.repository.UsersRepository;
import redcode.bookanddrive.auth_server.users.utils.PasswordGenerator;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public User create(User user) {
        String encryptedPassword = passwordEncoder.encode(PasswordGenerator.generatePassword(12));
        User userWithEncryptedPassword = user.toBuilder()
            .password(encryptedPassword)
            .build();
        UserEntity userEntity = UserEntity.from(userWithEncryptedPassword);
        UserEntity savedUser = usersRepository.save(userEntity);

        return User.from(savedUser);
    }

    public User updatePassword(String email, String newPassword) {
        UserEntity existingUser = usersRepository.findByEmail(email)
            .orElseThrow(() -> ResourceNotFoundException.of(RESOURCE_NOT_FOUND));

        String encryptedPassword = passwordEncoder.encode(newPassword);
        UserEntity userWithUpdatedEncryptedPassword = existingUser.toBuilder()
            .password(encryptedPassword)
            .build();
        usersRepository.save(userWithUpdatedEncryptedPassword);

        return User.from(userWithUpdatedEncryptedPassword);
    }

    public List<User> getUsers() {
        return usersRepository.findAll().stream()
            .map(User::from)
            .toList();
    }

    public User findById(UUID id) {
        return usersRepository.findById(id)
            .map(User::from)
            .orElse(null);
    }

    public User updateById(UUID id, User user) {
        return usersRepository.findById(id)
            .map(userEntity -> {
                UserEntity updatedUserEntity = UserEntity.update(userEntity, user);
                UserEntity savedUser = usersRepository.save(updatedUserEntity);
                return User.from(savedUser);
            })
            .orElseThrow(() -> ResourceNotFoundException.of(RESOURCE_NOT_FOUND));
    }

    public void deleteById(UUID id) {
        usersRepository.findById(id)
            .ifPresentOrElse(
                userEntity -> usersRepository.deleteById(id), () -> {
                    throw ResourceNotFoundException.of(RESOURCE_NOT_FOUND);
                }
            );
    }

    public boolean existsById(UUID id) {
        return usersRepository.existsById(id);
    }
}
