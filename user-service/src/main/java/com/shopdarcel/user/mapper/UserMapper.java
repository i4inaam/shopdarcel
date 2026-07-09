package com.shopdarcel.user.mapper;

import com.shopdarcel.user.dto.UserResponse;
import com.shopdarcel.user.entity.User;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for converting {@link User} entities into {@link UserResponse}
 * DTOs for API responses.
 * <p>
 * Deliberately one-directional: {@code User} construction from a
 * {@code RegisterRequest} involves business logic (password hashing,
 * server-stamped timestamps, default role assignment) that doesn't belong
 * in a mechanical field-mapping layer, so that direction is handled
 * explicitly in the service layer instead via {@link User.UserBuilder}.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);
}