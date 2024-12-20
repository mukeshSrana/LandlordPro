package com.landlordpro.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.landlordpro.domain.Contact;

public interface ContactRepository extends JpaRepository<Contact, Long> {
}
