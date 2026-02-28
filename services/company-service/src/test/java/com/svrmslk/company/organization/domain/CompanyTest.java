package com.svrmslk.company.organization.domain;

import com.svrmslk.company.shared.domain.CompanyId;
import com.svrmslk.company.shared.domain.CompanyType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompanyTest {

    @Test
    void validateIndividualLimits_shouldSetMaxVehiclesTo5() {
        Company company = Company.builder()
                .id(CompanyId.generate())
                .type(CompanyType.INDIVIDUAL)
                .build();

        company.validateIndividualLimits();

        assertEquals(5, company.getMaxVehicles());
    }

    @Test
    void isIndividual_shouldReturnTrue_forIndividualType() {
        Company company = Company.builder()
                .type(CompanyType.INDIVIDUAL)
                .build();

        assertTrue(company.isIndividual());
    }

    @Test
    void isIndividual_shouldReturnFalse_forCompanyType() {
        Company company = Company.builder()
                .type(CompanyType.COMPANY)
                .build();

        assertFalse(company.isIndividual());
    }
}