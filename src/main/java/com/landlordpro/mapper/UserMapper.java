package com.landlordpro.mapper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.landlordpro.domain.User;
import com.landlordpro.dto.UserDto;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "roles", expression = "java(rolesToString(user.getRoles()))")
    UserDto toDTO(User user);

    @Mapping(target = "roles", expression = "java(stringToRoles(userDTO.getRoles()))")
    User toEntity(UserDto userDTO);

    default String rolesToString(Set<String> roles) {
        return String.join(",", roles);
    }

    default Set<String> stringToRoles(String roles) {
        return roles == null || roles.isEmpty() ? new HashSet<>() : new HashSet<>(Arrays.asList(roles.split(",")));
    }

    // Mapping for lists
    List<UserDto> toDTOList(List<User> users);
    List<User> toEntityList(List<UserDto> userDTOs);
}
