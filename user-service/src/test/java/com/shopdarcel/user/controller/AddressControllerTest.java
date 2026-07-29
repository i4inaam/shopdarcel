package com.shopdarcel.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopdarcel.common.exception.ResourceNotFoundException;
import com.shopdarcel.user.config.SecurityFilterConfig;
import com.shopdarcel.user.dto.address.AddressRequest;
import com.shopdarcel.user.dto.address.AddressResponse;
import com.shopdarcel.user.service.AddressService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AddressController.class)
@Import(SecurityFilterConfig.class)
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AddressService addressService;

    @Test
    void upsertAddress_withValidRequest_returns200() throws Exception {
        // Arrange
        AddressRequest request = AddressRequest.builder()
                .line1("123 Main St")
                .city("Chicago")
                .state("IL")
                .postalCode("60601")
                .country("USA")
                .build();

        AddressResponse response = AddressResponse.builder()
                .id(1L)
                .line1("123 Main St")
                .build();
        when(addressService.upsertAddress(any(), any(AddressRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/api/users/me/address").header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.line1").value("123 Main St"));
    }

    @Test
    void getAddress_whenExists_returns200() throws Exception {
        // Arrange
        AddressResponse response = AddressResponse.builder()
                .id(1L)
                .line1("123 Main St")
                .build();
        when(addressService.getAddress(any())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/users/me/address").header("X-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.line1").value("123 Main St"));
    }

    @Test
    void getAddress_whenNoneExists_returns404() throws Exception {
        // Arrange
        when(addressService.getAddress(any())).thenThrow(new ResourceNotFoundException("No address found"));

        // Act & Assert
        mockMvc.perform(get("/api/users/me/address").header("X-User-Id", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteAddress_withValidRequest_returns204() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/users/me/address").header("X-User-Id", "1"))
                .andExpect(status().isNoContent());
    }
}