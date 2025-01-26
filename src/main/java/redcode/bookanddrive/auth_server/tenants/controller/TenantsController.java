package redcode.bookanddrive.auth_server.tenants.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redcode.bookanddrive.auth_server.tenants.context.TenantContext;
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
    public ResponseEntity<TenantResponse> addSchemaForTenant(@Valid @RequestBody CreateTenantRequest request) {
        log.info("Creating schema for tenant: {}", request.name());
        Tenant tenant = Tenant.from(request);
        Tenant createdTenant = schemaService.createTenant(tenant);
        TenantResponse response = TenantResponse.from(createdTenant);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<String> getTenant() {
        return ResponseEntity.ok(TenantContext.getTenantId());
    }
}
