package com.landlordpro.dto.validator;

import com.landlordpro.dto.TenantDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class LeaseEndDateValidator implements ConstraintValidator<ValidLeaseEndDate, LocalDate> {

    private TenantDto tenantDto;

    @Override
    public void initialize(ValidLeaseEndDate constraintAnnotation) {
        // Nothing to initialize
    }

    @Override
    public boolean isValid(LocalDate leaseEndDate, ConstraintValidatorContext context) {
        // If leaseEndDate is null, it's optional and valid
        if (leaseEndDate == null) {
            return true;
        }

        // Get leaseStartDate from the TenantDto object (should be validated)
        LocalDate leaseStartDate = tenantDto.getLeaseStartDate();

        // If leaseStartDate is null, the leaseEndDate cannot be valid
        if (leaseStartDate == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Lease start date  is mandatory and can't be null");
            return false;
        }

        // Lease end date must be after lease start date
        return leaseEndDate.isAfter(leaseStartDate);
    }

    // To access TenantDto when validating the property
    public void setTenantDto(TenantDto tenantDto) {
        this.tenantDto = tenantDto;
    }
}

