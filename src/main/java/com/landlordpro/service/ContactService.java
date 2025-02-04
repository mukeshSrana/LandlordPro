package com.landlordpro.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

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
}

