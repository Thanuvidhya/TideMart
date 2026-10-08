package com.tidemart.address;

import com.tidemart.address.dto.AddressRequest;
import com.tidemart.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {
    private final AddressService service;

    public AddressController(AddressService service) { this.service = service; }

    @GetMapping
    public ApiResponse<List<Address>> list(@AuthenticationPrincipal Long uid) { return ApiResponse.ok(service.list(uid)); }

    @PostMapping
    public ApiResponse<Address> add(@AuthenticationPrincipal Long uid, @Valid @RequestBody AddressRequest r) { return ApiResponse.ok(service.save(uid, null, r)); }

    @PutMapping("/{id}")
    public ApiResponse<Address> update(@AuthenticationPrincipal Long uid, @PathVariable Long id, @Valid @RequestBody AddressRequest r) { return ApiResponse.ok(service.save(uid, id, r)); }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@AuthenticationPrincipal Long uid, @PathVariable Long id) { service.delete(uid, id); return ApiResponse.ok(null); }
}
