package com.ecommerce.site.admin.repository;

import com.ecommerce.site.admin.model.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface BrandRepository extends CustomPagingAndSortingRepository<Brand, Integer> {

    Long countById(Integer id);

    Brand findByName(String name);

    @Query("SELECT b FROM Brand b WHERE b.name LIKE %?1%")
    Page<Brand> findAll(String keyword, Pageable pageable);

    @Query("SELECT NEW Brand (b.id, b.name) FROM Brand b ORDER BY b.name ASC")
    List<Brand> findAll();
}