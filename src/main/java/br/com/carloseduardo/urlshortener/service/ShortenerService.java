package br.com.carloseduardo.urlshortener.service;

import br.com.carloseduardo.urlshortener.model.Shortener;
import br.com.carloseduardo.urlshortener.repository.ShortenerRepository;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ShortenerService {

    private final ShortenerRepository shortenerRepository;

    ShortenerService(ShortenerRepository shortenerRepository) {
        this.shortenerRepository = shortenerRepository;
    }

    public Shortener findByShortUrl(String shortUrl) {
        Optional<Shortener> result = shortenerRepository.findShortenerByShortUrl(shortUrl);
        return result.orElse(null);
    }

    public Shortener save(Shortener shortener) {
        Optional<Shortener> result = shortenerRepository.findShortenerByShortUrl(shortener.getShortUrl());
        if (result.isPresent()) {
            return shortener;
        }
        return shortenerRepository.save(shortener);
    }

    public Shortener update(Shortener shortener) {
        Optional<Shortener> result = shortenerRepository.findShortenerByEncodedUrl(shortener.getEncodedUrl());
        if (result.isPresent()) {
            return shortenerRepository.save(shortener);
        }
        return null;
    }

    public URL validateURL(String urlOriginal) {
        URL url = null;
        try {
            Pattern regex = Pattern.compile("(?i)^(?:(?:https?|ftp)://)(?:\\S+(?::\\S*)?@)?(?:(?!(?:10|127)(?:\\.\\d{1,3}){3})(?!(?:169\\.254|192\\.168)(?:\\.\\d{1,3}){2})(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)(?:\\.(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)*(?:\\.(?:[a-z\\u00a1-\\uffff]{2,}))\\.?)(?::\\d{2,5})?(?:[/?#]\\S*)?$");
            Matcher matcher = regex.matcher(urlOriginal);
            if(!matcher.find()) {
                throw new URISyntaxException(urlOriginal, "URL inválida");
            }
            url = new URL(urlOriginal);
            url.toURI();
        } catch (MalformedURLException | URISyntaxException e) {
            System.out.println("URL inválida" + e.getMessage());
        }
        return url;
    }
}
