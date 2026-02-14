package com.ibrakhim2906.makemyurlshort.repositories;

import com.ibrakhim2906.makemyurlshort.entities.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {

    Optional<ShortUrl> findByCode(String code);

    boolean existsByCode(String code);
}
