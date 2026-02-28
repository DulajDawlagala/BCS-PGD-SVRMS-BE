// ==========================================
// FILE: util/SlugGenerator.java
// ==========================================
package com.svrmslk.tenant.util;

import org.springframework.stereotype.Component;
import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

@Component
public class SlugGenerator {

    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern MULTIPLE_HYPHENS = Pattern.compile("-{2,}");

    public String generateSlug(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String slug = normalized.toLowerCase(Locale.ENGLISH);
        slug = WHITESPACE.matcher(slug).replaceAll("-");
        slug = NON_LATIN.matcher(slug).replaceAll("");
        slug = MULTIPLE_HYPHENS.matcher(slug).replaceAll("-");
        slug = slug.replaceAll("^-|-$", "");

        return slug;
    }
}
