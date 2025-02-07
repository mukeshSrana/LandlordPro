package com.landlordpro.dto.validator;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;


@Component
public class MultipartFileToByteArrayConverter implements Converter<MultipartFile, byte[]> {

    @Override
    public byte[] convert(MultipartFile source) {
        if (source == null || source.isEmpty()) {
            return null;
        }
        try {
            return source.getBytes();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to convert MultipartFile to byte[]", e);
        }
    }
}
