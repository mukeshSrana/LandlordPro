package com.landlordpro.report;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApartmentOccupancySummary {
    private String apartmentName;
    private String ownerName;
    private String tenantName;
    private LocalDate leaseStartDate;
    private LocalDate leaseEndDate;
    private String occupancyStatus;
}
