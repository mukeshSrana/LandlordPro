package com.landlordpro.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.landlordpro.domain.Contact;
import com.landlordpro.dto.ContactDto;
import com.landlordpro.mapper.ContactMapper;
import com.landlordpro.repository.ContactRepository;

@Service
public class ContactService {
    private final ContactRepository contactRepository;
    private final ContactMapper contactMapper;

    public ContactService(ContactRepository contactRepository, ContactMapper contactMapper) {
        this.contactRepository = contactRepository;
        this.contactMapper = contactMapper;
    }

    public void saveContact(ContactDto contactDto) {
        contactRepository.save(contactMapper.toEntity(contactDto));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<ContactDto> getAllContacts() {
        return contactMapper.toDTOList(contactRepository.findAll());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public boolean updateContact(ContactDto contactDto) {
        Contact existingContact = contactRepository.findById(contactDto.getId())
            .orElseThrow(() -> new RuntimeException("Contact not found with ID: " + contactDto.getId()));
        contactMapper.updateEntityFromDto(contactDto, existingContact);
        try {
            contactRepository.save(existingContact);
        } catch (Exception ex) {
            throw new RuntimeException("Unexpected error while updating contact", ex);
        }
        return true;
    }
}

