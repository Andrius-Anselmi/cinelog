package com.cinelog.service;

import com.cinelog.entity.Streaming;
import com.cinelog.repository.StreamingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StreamingService {

    private final StreamingRepository repository;

    public Streaming save(Streaming streaming){
        return repository.save(streaming);
    }

    public List<Streaming> findAll(){
        return repository.findAll();
    }

    public Optional<Streaming> findById(Long id){
        return repository.findById(id);
    }

    public Optional<Streaming> updateById(Long id, Streaming request){
        Optional<Streaming> optionalStreaming = repository.findById(id);
        if(optionalStreaming.isPresent()){
            Streaming updateStreaming = optionalStreaming.get();
            updateStreaming.setName(request.getName());

            return Optional.of(repository.save(updateStreaming));
        }

        return Optional.empty();
    }

    public void deleteById(Long id){
        repository.deleteById(id);
    }

}
