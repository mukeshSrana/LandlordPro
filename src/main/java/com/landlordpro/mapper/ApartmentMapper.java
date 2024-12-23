package com.landlordpro.mapper;

import com.landlordpro.domain.Apartment;
import com.landlordpro.dto.ApartmentDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ApartmentMapper {

    // Mapping a single Apartment entity to ApartmentDto
    ApartmentDto toDTO(Apartment apartment);

    // Mapping a single ApartmentDto to Apartment entity
    Apartment toEntity(ApartmentDto apartmentDto);

    // Mapping a list of Apartment entities to a list of ApartmentDto
    List<ApartmentDto> toDTOList(List<Apartment> apartments);

    // Mapping a list of ApartmentDto to a list of Apartment entities
    List<Apartment> toEntityList(List<ApartmentDto> apartmentDTOs);
}
