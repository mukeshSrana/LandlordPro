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
        if (contactDto.getReference() == null) {
            throw new IllegalArgumentException("SaveContact: Contact ID and Reference must not be null.");
        }

        try {
            contactRepository.save(contactMapper.toEntity(contactDto));
        } catch (Exception e) {
            throw new RuntimeException("Failed to save contact", e);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<ContactDto> getAllContacts() {
        return contactMapper.toDTOList(contactRepository.findAll());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public boolean updateContact(ContactDto contactDto) {
        if (contactDto.getId() == null || contactDto.getReference() == null) {
            throw new IllegalArgumentException("UpdateContact: Contact ID and Reference must not be null.");
        }

        Contact existingContact = contactRepository.findById(contactDto.getId())
            .orElseThrow(() -> new RuntimeException("Contact not found with ID: " + contactDto.getId() + " and Reference: " + contactDto.getReference()));

        contactMapper.updateEntityFromDto(contactDto, existingContact);
        try {
            contactRepository.save(existingContact);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while updating contact", e);
        }
    }
}

