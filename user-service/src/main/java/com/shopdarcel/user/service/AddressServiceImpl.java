package com.shopdarcel.user.service;

import com.shopdarcel.common.exception.ResourceNotFoundException;
import com.shopdarcel.user.constants.AuthMessages;
import com.shopdarcel.user.dto.address.AddressRequest;
import com.shopdarcel.user.dto.address.AddressResponse;
import com.shopdarcel.user.entity.Address;
import com.shopdarcel.user.entity.User;
import com.shopdarcel.user.mapper.AddressMapper;
import com.shopdarcel.user.repository.AddressRepository;
import com.shopdarcel.user.repository.UserRepository;
import com.shopdarcel.user.util.UserIdHeaderResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;
    private final UserIdHeaderResolver userIdHeaderResolver;

    @Override
    @Transactional
    public AddressResponse upsertAddress(String userIdHeader, AddressRequest request) {
        Long userId = userIdHeaderResolver.resolve(userIdHeader);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(AuthMessages.USER_NOT_FOUND));

        Address address = addressRepository.findByUserId(userId)
                .orElseGet(() -> Address.builder()
                        .user(user)
                        .build());

        address.setRecipientName(user.getFullName());
        address.setLine1(request.getLine1());
        address.setLine2(request.getLine2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPostalCode(request.getPostalCode());
        address.setCountry(request.getCountry());

        Address saved = addressRepository.save(address);
        return addressMapper.toResponse(saved);
    }

    @Override
    public AddressResponse getAddress(String userIdHeader) {
        Long userId = userIdHeaderResolver.resolve(userIdHeader);

        Address address = addressRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(AuthMessages.ADDRESS_NOT_FOUND));

        return addressMapper.toResponse(address);
    }

    @Override
    @Transactional
    public void deleteAddress(String userIdHeader) {
        Long userId = userIdHeaderResolver.resolve(userIdHeader);

        Address address = addressRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(AuthMessages.ADDRESS_NOT_FOUND));

        addressRepository.delete(address);
    }
}