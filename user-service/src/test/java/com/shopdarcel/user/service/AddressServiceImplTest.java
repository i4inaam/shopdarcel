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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link AddressServiceImpl}, covering upsert (create and
 * update paths), fetch, and delete for a user's single saved address.
 */
@ExtendWith(MockitoExtension.class)
class AddressServiceImplTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AddressMapper addressMapper;

    @Mock
    private UserIdHeaderResolver userIdHeaderResolver;

    @InjectMocks
    private AddressServiceImpl addressService;

    private User createUser() {
        User user = User.builder()
                .firstName("Inaam")
                .lastName("Haq")
                .build();
        user.setId(1L);
        return user;
    }

    private AddressRequest createAddressRequest() {
        return AddressRequest.builder()
                .line1("123 Main St")
                .city("Chicago")
                .state("IL")
                .postalCode("60601")
                .country("USA")
                .build();
    }

    @Test
    void upsertAddress_whenNoneExists_createsNewAddress() {
        // Arrange
        User user = createUser();
        AddressRequest request = createAddressRequest();

        when(userIdHeaderResolver.resolve("1")).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(addressRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(addressMapper.toResponse(any(Address.class))).thenReturn(AddressResponse.builder()
                .id(1L)
                .build());

        // Act
        AddressResponse result = addressService.upsertAddress("1", request);

        // Assert
        assertThat(result).isNotNull();
        verify(addressRepository).save(any(Address.class));
    }

    @Test
    void upsertAddress_whenOneExists_updatesExistingAddress() {
        // Arrange
        User user = createUser();
        AddressRequest request = createAddressRequest();

        Address existing = Address.builder()
                .user(user)
                .line1("Old Address")
                .build();
        existing.setId(5L);

        when(userIdHeaderResolver.resolve("1")).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(addressRepository.findByUserId(1L)).thenReturn(Optional.of(existing));
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(addressMapper.toResponse(any(Address.class))).thenReturn(AddressResponse.builder()
                .id(5L)
                .build());

        // Act
        AddressResponse result = addressService.upsertAddress("1", request);

        // Assert
        assertThat(existing.getLine1()).isEqualTo("123 Main St");
        assertThat(existing.getRecipientName()).isEqualTo("Inaam Haq");
        verify(addressRepository).save(existing);
    }

    @Test
    void getAddress_whenExists_returnsAddress() {
        // Arrange
        Address address = Address.builder()
                .line1("123 Main St")
                .build();
        address.setId(1L);

        when(userIdHeaderResolver.resolve("1")).thenReturn(1L);
        when(addressRepository.findByUserId(1L)).thenReturn(Optional.of(address));
        when(addressMapper.toResponse(address)).thenReturn(AddressResponse.builder()
                .id(1L)
                .build());

        // Act
        AddressResponse result = addressService.getAddress("1");

        // Assert
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getAddress_whenNoneExists_throwsResourceNotFound() {
        // Arrange
        when(userIdHeaderResolver.resolve("1")).thenReturn(1L);
        when(addressRepository.findByUserId(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> addressService.getAddress("1")).isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(AuthMessages.ADDRESS_NOT_FOUND);
    }

    @Test
    void deleteAddress_whenExists_deletesIt() {
        // Arrange
        Address address = Address.builder()
                .build();
        address.setId(1L);

        when(userIdHeaderResolver.resolve("1")).thenReturn(1L);
        when(addressRepository.findByUserId(1L)).thenReturn(Optional.of(address));

        // Act
        addressService.deleteAddress("1");

        // Assert
        verify(addressRepository).delete(address);
    }

    @Test
    void deleteAddress_whenNoneExists_throwsResourceNotFound() {
        // Arrange
        when(userIdHeaderResolver.resolve("1")).thenReturn(1L);
        when(addressRepository.findByUserId(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> addressService.deleteAddress("1")).isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(AuthMessages.ADDRESS_NOT_FOUND);
    }
}