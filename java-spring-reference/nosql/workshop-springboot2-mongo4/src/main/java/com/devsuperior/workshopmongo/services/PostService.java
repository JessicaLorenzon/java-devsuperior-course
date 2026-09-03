package com.devsuperior.workshopmongo.services;

import com.devsuperior.workshopmongo.models.dto.PostDTO;
import com.devsuperior.workshopmongo.models.entities.Post;
import com.devsuperior.workshopmongo.repositories.PostRepository;
import com.devsuperior.workshopmongo.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository repository;

    public PostDTO findById(String id) {
        Optional<Post> result = repository.findById(id);
        Post entity = result.orElseThrow(() -> new ResourceNotFoundException("Objeto não encontrado"));
        return new PostDTO(entity);
    }

    public List<PostDTO> findByTitle(String text) {
        List<Post> list = repository.findByTitleContainingIgnoreCase(text);
        return list.stream().map(x -> new PostDTO(x)).collect(Collectors.toList());
    }
}
