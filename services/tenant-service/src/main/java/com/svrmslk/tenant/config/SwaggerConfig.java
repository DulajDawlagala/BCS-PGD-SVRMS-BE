// ==========================================
// FILE: config/SwaggerConfig.java
// ==========================================
package com.svrmslk.tenant.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI tenantServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Tenant Service API")
                        .description("Multi-tenant management service for SVRMS")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("SVRMS Team")
                                .email("support@svrms.com")));
    }
}
