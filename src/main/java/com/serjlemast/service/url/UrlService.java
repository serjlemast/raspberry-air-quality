package com.serjlemast.service.url;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.springframework.stereotype.Service;

@Service
public class UrlService {

  private final Set<String> urlStore = new CopyOnWriteArraySet<>();

  public Set<String> addUrls(Set<String> urls) {
    urlStore.addAll(urls);
    return urls;
  }

  public Set<String> getUrls() {
    return urlStore;
  }

  public void clearUrls() {
    urlStore.clear();
  }

  public boolean isEmpty() {
    return urlStore.isEmpty();
  }
}
