package com.blueisfresh.bootguard.mapper;

import com.blueisfresh.bootguard.dto.UserDto;
import com.blueisfresh.bootguard.entity.Role;
import com.blueisfresh.bootguard.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    // User -> UserDto
    @Mapping(target = "roles", expression = "java(mapRolesToStrings(user.getRoles()))")
    UserDto toDto(User user);

    // UserDto -> User
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "refreshTokens", ignore = true)
    @Mapping(target = "roles", expression = "java(mapStringsToRoles(dto.getRoles()))")
    User toEntity(UserDto dto);

    // Helper methods for role conversion
    default Set<String> mapRolesToStrings(Set<Role> roles) {
        return roles.stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
    }

    default Set<Role> mapStringsToRoles(Set<String> roleNames) {
        return roleNames.stream()
                .map(name -> {
                    Role role = new Role();
                    role.setName(Enum.valueOf(com.blueisfresh.bootguard.user.RoleName.class, name));
                    return role;
                })
                .collect(Collectors.toSet());
    }
}
