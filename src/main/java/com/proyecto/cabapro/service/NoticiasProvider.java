package com.proyecto.cabapro.service;

import java.util.List;

import com.proyecto.cabapro.model.NewsArticle;

public interface NoticiasProvider {
    List<NewsArticle> obtenerNoticias();
}
