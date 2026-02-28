package com.svrmslk.company.shared.exception;

import java.util.UUID;

public class CompanyNotFoundException extends DomainException {
    public CompanyNotFoundException(UUID companyId) {
        super("Company not found: " + companyId);
    }
}