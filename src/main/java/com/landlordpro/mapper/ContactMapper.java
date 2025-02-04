package com.landlordpro.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.landlordpro.domain.Contact;
import com.landlordpro.dto.ContactDto;

@Mapper(componentModel = "spring")
public interface ContactMapper {
    ContactDto toDTO(Contact contact);

    Contact toEntity(ContactDto contactDto);

    // Mapping for lists
    List<ContactDto> toDTOList(List<Contact> contacts);
    List<Contact> toEntityList(List<ContactDto> contactDTOs);

    void updateEntityFromDto(ContactDto contactDto, @MappingTarget Contact contact);
}

