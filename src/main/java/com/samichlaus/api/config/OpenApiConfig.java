package com.samichlaus.api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "nba",
                        email = "nba@famba.me",
                        url = "https://www.samichlaushergiswil.ch"
                ),
                description = "OpenApi documentation for Spring Security",
                title = "OpenApi specification - nba",
                version = "0.0.1"
        ),
        servers = {
                @Server(
                        description = "Local ENV",
                        url = "http://127.0.0.1:8080"
                ),
                @Server(
                        description = "PROD ENV",
                        url = "https://api.samichlaus.famba.me"
                )
        },
        security = {
                @SecurityRequirement(
                        name = "bearerAuth"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
