package com.ecommerce.site.admin.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;


@NoRepositoryBean
public interface CustomPagingAndSortingRepository<T, ID> extends JpaRepository<T, ID> {

    Page<T> findAll(String keyword, Pageable pageable);
}