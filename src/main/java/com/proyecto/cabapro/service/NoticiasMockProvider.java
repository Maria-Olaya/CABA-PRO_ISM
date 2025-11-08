package com.proyecto.cabapro.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.proyecto.cabapro.model.NewsArticle;

@Service
public class NoticiasMockProvider implements NoticiasProvider {

    @Override
    public List<NewsArticle> obtenerNoticias() {
        List<NewsArticle> mock = new ArrayList<>();
        mock.add(new NewsArticle("Noticia de prueba 1", "Descripción 1", "http://link1.com", "", "MockSource"));
        mock.add(new NewsArticle("Noticia de prueba 2", "Descripción 2", "http://link2.com", "", "MockSource"));
        return mock;
    }
}
