package redcode.bookanddrive.auth_server.tenants.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redcode.bookanddrive.auth_server.tenants.controller.dto.CreateTenantRequest;
import redcode.bookanddrive.auth_server.tenants.controller.dto.TenantResponse;
import redcode.bookanddrive.auth_server.tenants.model.Tenant;
import redcode.bookanddrive.auth_server.tenants.service.TenantsService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tenants")
public class TenantsController {

    private final TenantsService schemaService;

    @PostMapping
    @PreAuthorize("hasAuthority('super:admin)")
    public ResponseEntity<TenantResponse> createTenant(@Valid @RequestBody CreateTenantRequest request) {
        log.info("Adding tenant: {}", request.name());
        Tenant tenant = Tenant.from(request);
        Tenant createdTenant = schemaService.createTenant(tenant);
        TenantResponse response = TenantResponse.from(createdTenant);

        return ResponseEntity.ok(response);
    }

//    @GetMapping
//    public ResponseEntity<String> getTenant() {
//        return ResponseEntity.ok(TenantContext.getTenantId());
//    }
}
