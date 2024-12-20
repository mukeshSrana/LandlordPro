package com.landlordpro.service;

import org.springframework.stereotype.Service;

import com.landlordpro.domain.Contact;
import com.landlordpro.dto.ContactDto;
import com.landlordpro.repository.ContactRepository;

@Service
public class ContactService {
    private final ContactRepository contactRepository;

    public ContactService(ContactRepository contactRepository) { this.contactRepository = contactRepository; }

    public void saveContact(ContactDto contactDto) {
        Contact contact = new Contact();
        contact.setName(contactDto.getName());
        contact.setEmail(contactDto.getEmail());
        contact.setMessage(contactDto.getMessage());

        contactRepository.save(contact);
    }
}

