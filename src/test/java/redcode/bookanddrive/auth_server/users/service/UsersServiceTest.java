package redcode.bookanddrive.auth_server.users.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static redcode.bookanddrive.auth_server.data_generator.UsersGenerator.generateUser;
import static redcode.bookanddrive.auth_server.data_generator.UsersGenerator.generateUserEntity;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import redcode.bookanddrive.auth_server.exceptions.ResourceNotFoundException;
import redcode.bookanddrive.auth_server.users.domain.UserEntity;
import redcode.bookanddrive.auth_server.users.model.User;
import redcode.bookanddrive.auth_server.users.repository.UsersRepository;

@ExtendWith(MockitoExtension.class)
class UsersServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private UsersService usersService;

    @Test
    void testSave() {
        // Arrange
        User user = generateUser();
        UserEntity savedUserEntity = UserEntity.from(user);

        when(usersRepository.save(any(UserEntity.class))).thenReturn(savedUserEntity);

        // Act
        User savedUser = usersService.save(user);

        // Assert
        verify(usersRepository).save(any(UserEntity.class));
        assertNotNull(savedUser);
    }

    @Test
    void testUpdatePassword() {
        // Arrange
        String email = "test@example.com";
        String newPassword = "newPassword";
        UserEntity existingUser = generateUserEntity();
        User mockUser = generateUser();

        when(usersRepository.findByEmailAndTenantName(any(), any())).thenReturn(Optional.of(existingUser));
        when(usersRepository.save(any(UserEntity.class))).thenReturn(existingUser);

        // Act
        User updatedUser = usersService.updatePassword(mockUser, newPassword);

        // Assert
        verify(usersRepository).save(any(UserEntity.class));
        assertNotNull(updatedUser);
    }

    @Test
    void testUpdatePassword_UserNotFound() {
        // Arrange
        String email = "nonexistent@example.com";
        String newPassword = "newPassword";
        User mockUser = generateUser();

        when(usersRepository.findByEmailAndTenantName(any(), any())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
            () -> usersService.updatePassword(mockUser, newPassword));
    }

//    @Test
//    void testGetUsers() {
//        // Arrange
//        List<UserEntity> userEntities = List.of(
//            generateUserEntity(),
//            generateUserEntity().toBuilder().email("test2@gmail.com").build()
//        );
//
//        when(usersRepository.findAll()).thenReturn(userEntities);
//
//        // Act
//        List<User> users = usersService.getUsers();
//
//        // Assert
//        assertEquals(userEntities.size(), users.size());
//    }

    @Test
    void testFindById() {
        UserEntity userEntity = generateUserEntity();

        when(usersRepository.findById(any())).thenReturn(Optional.of(userEntity));

        // Act
        User user = usersService.findById(userEntity.getId());

        // Assert
        assertNotNull(user);
    }

    @Test
    void testFindById_UserNotFound() {
        // Arrange
        UUID id = UUID.randomUUID();

        when(usersRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
            () -> usersService.findById(id));
    }

    @Test
    void testFindByEmailAndTenantName() {
        // Arrange
        String email = "test@example.com";
        UserEntity userEntity = generateUserEntity();

        when(usersRepository.findByEmailAndTenantName(any(), any())).thenReturn(Optional.of(userEntity));

        // Act
        User user = usersService.findByUsernameAndTenantName(email, userEntity.getTenant().getName());

        // Assert
        assertNotNull(user);
    }

    @Test
    void testFindByEmail_UserNotFound() {
        // Arrange
        String email = "nonexistent@example.com";

        when(usersRepository.findByEmailAndTenantName(any(), any())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
            () -> usersService.findByUsernameAndTenantName(email, "tenant"));
    }

    @Test
    void testUpdateById() {
        // Arrange
        UUID id = UUID.randomUUID();
        UserEntity existingUserEntity = generateUserEntity();
        User updateUser = generateUser();

        when(usersRepository.findById(id)).thenReturn(Optional.of(existingUserEntity));
        when(usersRepository.save(any(UserEntity.class))).thenReturn(existingUserEntity);

        // Act
        User updatedUser = usersService.updateById(id, updateUser);

        // Assert
        assertNotNull(updatedUser);
        verify(usersRepository).save(any(UserEntity.class));
    }

    @Test
    void testUpdateById_UserNotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        User updateUser = User.builder().build();

        when(usersRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
            () -> usersService.updateById(id, updateUser));
    }

    @Test
    void testDeleteById() {
        // Arrange
        UUID id = UUID.randomUUID();
        UserEntity userEntity = mock(UserEntity.class);

        when(usersRepository.findById(id)).thenReturn(Optional.of(userEntity));

        // Act
        usersService.deleteById(id);

        // Assert
        verify(usersRepository).deleteById(id);
    }

    @Test
    void testDeleteById_UserNotFound() {
        // Arrange
        UUID id = UUID.randomUUID();

        when(usersRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
            () -> usersService.deleteById(id));
    }

    @Test
    void testExistsById() {
        // Arrange
        UUID id = UUID.randomUUID();

        when(usersRepository.existsById(id)).thenReturn(true);

        // Act
        boolean exists = usersService.existsById(id);

        // Assert
        assertTrue(exists);
    }
}
