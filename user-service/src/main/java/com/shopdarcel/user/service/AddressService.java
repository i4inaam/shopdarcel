package com.shopdarcel.user.service;

import com.shopdarcel.user.dto.address.AddressRequest;
import com.shopdarcel.user.dto.address.AddressResponse;

/**
 * Business logic for managing a user's single saved shipping address.
 */
public interface AddressService {

    /**
     * Creates or replaces the currently authenticated user's address.
     *
     * @param userIdHeader the raw {@code X-User-Id} header value
     * @param request      the address details
     * @return the saved address
     */
    AddressResponse upsertAddress(String userIdHeader, AddressRequest request);

    /**
     * Fetches the currently authenticated user's address.
     *
     * @param userIdHeader the raw {@code X-User-Id} header value
     * @return the user's address
     * @throws com.shopdarcel.common.exception.ResourceNotFoundException if no address exists yet
     */
    AddressResponse getAddress(String userIdHeader);

    /**
     * Deletes the currently authenticated user's address.
     *
     * @param userIdHeader the raw {@code X-User-Id} header value
     * @throws com.shopdarcel.common.exception.ResourceNotFoundException if no address exists yet
     */
    void deleteAddress(String userIdHeader);
}