package com.shopdarcel.user.mapper;

import com.shopdarcel.user.dto.address.AddressResponse;
import com.shopdarcel.user.entity.Address;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    AddressResponse toResponse(Address address);
}