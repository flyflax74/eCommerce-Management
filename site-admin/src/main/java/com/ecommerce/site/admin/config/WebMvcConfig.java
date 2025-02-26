package com.ecommerce.site.admin.config;

import com.ecommerce.site.admin.resolver.PagingAndSortingArgumentResolver;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(@NotNull ResourceHandlerRegistry registry) {
        exposeDirectory("../user-photos", registry);
        exposeDirectory("../category-images", registry);
        exposeDirectory("../brand-logos", registry);
        exposeDirectory("../product-images", registry);
        exposeDirectory("../site-photo", registry);
    }

    @Override
    public void addArgumentResolvers(@NotNull List<HandlerMethodArgumentResolver> resolver) {
        resolver.add(new PagingAndSortingArgumentResolver());
    }

    private void exposeDirectory(String pathPattern, @NotNull ResourceHandlerRegistry registry) {
        Path path = Paths.get(pathPattern);
        String absolutePath = path.toFile().getAbsolutePath();
        String logicalPath = pathPattern.replace("../", "") + "/**";

        registry.addResourceHandler(logicalPath).addResourceLocations("file:/" + absolutePath + "/");
    }
}
