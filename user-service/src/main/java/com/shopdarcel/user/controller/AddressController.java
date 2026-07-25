package com.shopdarcel.user.controller;

import com.shopdarcel.user.dto.address.AddressRequest;
import com.shopdarcel.user.dto.address.AddressResponse;
import com.shopdarcel.user.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST endpoints for managing the currently authenticated user's single
 * saved shipping address.
 */
@RestController
@RequestMapping("/api/users/me/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    /**
     * Creates or replaces the current user's address.
     *
     * @param userId  the raw {@code X-User-Id} header value
     * @param request the address details
     * @return the saved address, with HTTP 200 OK
     */
    @PutMapping
    public ResponseEntity<AddressResponse> upsertAddress(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody AddressRequest request) {
        AddressResponse response = addressService.upsertAddress(userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Fetches the current user's address.
     *
     * @param userId the raw {@code X-User-Id} header value
     * @return the user's address, with HTTP 200 OK
     */
    @GetMapping
    public ResponseEntity<AddressResponse> getAddress(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        AddressResponse response = addressService.getAddress(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes the current user's address.
     *
     * @param userId the raw {@code X-User-Id} header value
     * @return HTTP 204 No Content on success
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteAddress(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        addressService.deleteAddress(userId);
        return ResponseEntity.noContent()
                .build();
    }
}