package com.landlordpro.service;

import java.util.List;

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
        Contact contact = new Contact();
        contact.setName(contactDto.getName());
        contact.setEmail(contactDto.getEmail());
        contact.setMessage(contactDto.getMessage());

        contactRepository.save(contact);
    }

    public List<ContactDto> getAllContacts() {
        return contactMapper.toDTOList(contactRepository.findAll());
    }
}

