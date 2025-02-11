package com.landlordpro.dto.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import com.landlordpro.dto.TenantDto;
import java.time.LocalDate;

public class LeasePeriodValidator implements ConstraintValidator<ValidLeasePeriod, TenantDto> {

    @Override
    public boolean isValid(TenantDto tenantDto, ConstraintValidatorContext context) {
        if (tenantDto == null || tenantDto.getLeaseEndDate() == null) {
            return true; // Valid when leaseEndDate is optional
        }

        LocalDate startDate = tenantDto.getLeaseStartDate();
        LocalDate endDate = tenantDto.getLeaseEndDate();

        if (startDate != null && endDate.isBefore(startDate)) {
            // Disable the default message at the object level
            context.disableDefaultConstraintViolation();

            // Associate the error message with the leaseEndDate field
            context.buildConstraintViolationWithTemplate("Lease end date must be after lease start date")
                .addPropertyNode("leaseEndDate")
                .addConstraintViolation();

            return false;
        }

        return true;
    }
}


