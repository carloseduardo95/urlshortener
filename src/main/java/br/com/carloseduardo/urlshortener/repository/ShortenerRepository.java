package br.com.carloseduardo.urlshortener.repository;

import br.com.carloseduardo.urlshortener.model.Shortener;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShortenerRepository extends JpaRepository<Shortener, Long> {
    Optional<Shortener> findShortenerByShortUrl(String shortUrl);
    Optional<Shortener> findShortenerByEncodedUrl(String encodedUrl);
}
