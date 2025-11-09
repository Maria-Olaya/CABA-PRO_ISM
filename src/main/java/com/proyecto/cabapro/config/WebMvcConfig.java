package com.proyecto.cabapro.config;

import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  @Value("${app.uploads.dir}")   // dentro del proyecto ./uploads
  private String uploadsDir;

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    String location = "file:" + Paths.get(uploadsDir).toAbsolutePath().toString() + "/";
    registry.addResourceHandler("/uploads/**")
            .addResourceLocations(location)
            .resourceChain(true)
            .addResolver(new PathResourceResolver());
  }
  @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
          .allowedOriginPatterns("*")  // cualquier origen
          .allowedMethods("*")         // cualquier método
          .allowedHeaders("*")
          .allowCredentials(false)
          .maxAge(3600);

  }
}
