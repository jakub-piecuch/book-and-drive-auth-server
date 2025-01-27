package redcode.bookanddrive.auth_server.roles.service;

import static redcode.bookanddrive.auth_server.exceptions.ResourceNotFoundException.RESOURECE_NOT_FOUND;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import redcode.bookanddrive.auth_server.exceptions.ResourceNotFoundException;
import redcode.bookanddrive.auth_server.roles.domain.RoleEntity;
import redcode.bookanddrive.auth_server.roles.model.Role;
import redcode.bookanddrive.auth_server.roles.repository.RolesRepository;

@Service
@RequiredArgsConstructor
public class RolesService {

    private final RolesRepository rolesRepository;

    public Role create(Role role) {
        RoleEntity roleEntity = RoleEntity.from(role);
        RoleEntity savedRole = rolesRepository.save(roleEntity);

        return Role.from(savedRole);
    }

    public List<Role> getRoles() {
        return rolesRepository.findAll().stream()
            .map(Role::from)
            .toList();
    }

    public Role findById(UUID id) {
        return rolesRepository.findById(id)
            .map(Role::from)
            .orElse(null);
    }

    public Role updateById(UUID id, Role role) {
        return rolesRepository.findById(id)
            .map(roleEntity -> {
                RoleEntity updatedRoleEntity = RoleEntity.update(roleEntity, role);
                RoleEntity savedRole = rolesRepository.save(updatedRoleEntity);
                return Role.from(savedRole);
            })
            .orElseThrow(() -> ResourceNotFoundException.of(RESOURECE_NOT_FOUND));
    }

    public void deleteById(UUID id) {
        rolesRepository.findById(id)
            .ifPresentOrElse(
                roleEntity -> rolesRepository.deleteById(id), () -> {
                    throw ResourceNotFoundException.of(RESOURECE_NOT_FOUND);
                }
            );
    }

    public boolean existsById(UUID id) {
        return rolesRepository.existsById(id);
    }
}
