package redcode.bookanddrive.auth_server.users.service;

import static redcode.bookanddrive.auth_server.exceptions.ResourceNotFoundException.RESOURCE_NOT_FOUND;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import redcode.bookanddrive.auth_server.exceptions.ResourceNotFoundException;
import redcode.bookanddrive.auth_server.users.domain.UserEntity;
import redcode.bookanddrive.auth_server.users.model.User;
import redcode.bookanddrive.auth_server.users.repository.UsersRepository;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;

    public User save(User user) {
        UserEntity userEntity = UserEntity.from(user);
        UserEntity savedUser = usersRepository.save(userEntity);

        return User.from(savedUser);
    }

    public User updatePassword(User user, String encodedPassword) {
        String userName = user.getUsername();
        UUID tenant = user.getTenantId();
        UserEntity existingUserWithUpdatedPassword = usersRepository.findByEmailAndTenantId(userName, tenant)
            .map(entity -> entity.toBuilder().password(encodedPassword).build())
            .orElseThrow(() -> ResourceNotFoundException.of(RESOURCE_NOT_FOUND));

        UserEntity updatedUser = usersRepository.save(existingUserWithUpdatedPassword);

        return User.from(updatedUser);
    }

//    public List<User> getUsers() {
//        return usersRepository.findAll().stream()
//            .map(User::from)
//            .toList();
//    }

    public User findById(UUID id) {
        return usersRepository.findById(id)
            .map(User::from)
            .orElseThrow(() -> ResourceNotFoundException.of(RESOURCE_NOT_FOUND));
    }

//    public User findByEmail(String email) {
//        return usersRepository.findByEmail(email)
//            .map(User::from)
//            .orElseThrow(() -> ResourceNotFoundException.of(RESOURCE_NOT_FOUND));
//    }

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

    public User findByUsernameAndTenantId(String userName, UUID tenantId) {
        return usersRepository.findByEmailAndTenantId(userName, tenantId)
            .map(User::from)
            .orElseThrow(() -> ResourceNotFoundException.of(RESOURCE_NOT_FOUND));
    }
}
