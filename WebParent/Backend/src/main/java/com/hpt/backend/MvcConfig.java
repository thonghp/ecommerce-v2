package com.hpt.backend;

import com.hpt.backend.paging.PagingAndSortingArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // User
        exposeDirectory("user-photos", registry);

        // Category
        exposeDirectory("category-photos", registry);

        // Brand
        exposeDirectory("brand-photos", registry);

        // Product
        exposeDirectory("product-photos", registry);

        // Logo
        exposeDirectory("site-logo", registry);
    }

    private void exposeDirectory(String logicalPath, ResourceHandlerRegistry registry) {
        Path path = Paths.get(logicalPath);
        String absolutePath = path.toFile().getAbsolutePath();

        registry.addResourceHandler("/" + logicalPath + "/**").addResourceLocations("file:/" + absolutePath + "/");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new PagingAndSortingArgumentResolver());
    }
}
