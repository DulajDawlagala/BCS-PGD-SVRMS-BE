// ==========================================
// FILE: validator/TenantValidator.java
// ==========================================
package com.svrmslk.tenant.validator;

import com.svrmslk.tenant.presentation.dto.TenantRequest;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class TenantValidator {

    private static final Pattern SLUG_PATTERN = Pattern.compile("^[a-z0-9-]+$");
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public List<String> validate(TenantRequest request) {
        List<String> errors = new ArrayList<>();

        if (request.getSlug() != null && !SLUG_PATTERN.matcher(request.getSlug()).matches()) {
            errors.add("Slug must contain only lowercase letters, numbers, and hyphens");
        }

        if (request.getCompanyEmail() != null &&
                !EMAIL_PATTERN.matcher(request.getCompanyEmail()).matches()) {
            errors.add("Invalid email format");
        }

        if (request.getMaxUsers() != null && request.getMaxUsers() <= 0) {
            errors.add("Max users must be greater than 0");
        }

        return errors;
    }
}