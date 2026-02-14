package com.ibrakhim2906.makemyurlshort;

import com.ibrakhim2906.makemyurlshort.repositories.ShortUrlRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DbTestController {

    private ShortUrlRepository repo;

    public DbTestController(ShortUrlRepository shortUrlRepository) {
        this.repo = shortUrlRepository;
    }

    @GetMapping("/db-test")
    public Long count() {
        return repo.count();
    }

}
