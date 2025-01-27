package redcode.bookanddrive.auth_server.users.service;

import static redcode.bookanddrive.auth_server.exceptions.ResourceNotFoundException.RESOURECE_NOT_FOUND;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import redcode.bookanddrive.auth_server.exceptions.ResourceNotFoundException;
import redcode.bookanddrive.auth_server.users.domain.UserEntity;
import redcode.bookanddrive.auth_server.users.model.User;
import redcode.bookanddrive.auth_server.users.repository.UsersRepository;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public User create(User user) {
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        User userWithEncryptedPassword = user.toBuilder()
            .password(encryptedPassword)
            .build();
        UserEntity userEntity = UserEntity.from(userWithEncryptedPassword);
        UserEntity savedUser = usersRepository.save(userEntity);

        return User.from(savedUser);
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
            .orElseThrow(() -> ResourceNotFoundException.of(RESOURECE_NOT_FOUND));
    }

    public void deleteById(UUID id) {
        usersRepository.findById(id)
            .ifPresentOrElse(
                userEntity -> usersRepository.deleteById(id), () -> {
                    throw ResourceNotFoundException.of(RESOURECE_NOT_FOUND);
                }
            );
    }

    public boolean existsById(UUID id) {
        return usersRepository.existsById(id);
    }
}
