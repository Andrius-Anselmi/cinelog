package com.cinelog.controller;

import com.cinelog.controller.request.StreamingRequest;
import com.cinelog.controller.response.StreamingResponse;
import com.cinelog.entity.Streaming;
import com.cinelog.mapper.StreamingMapper;
import com.cinelog.service.StreamingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cinelog/streaming")
@RequiredArgsConstructor
public class StreamingController {

    private final StreamingService service;

    @GetMapping
    public ResponseEntity<List<StreamingResponse>> getAll(){
        return ResponseEntity.ok().body(service.findAll().stream().map(StreamingMapper::toStreamingResponse).toList());
    }

    @PostMapping()
    public ResponseEntity<StreamingResponse> save(@RequestBody StreamingRequest streaming){
        Streaming savedStreaming = service.save(StreamingMapper.toStreaming(streaming));
        return ResponseEntity.status(HttpStatus.CREATED).body(
                StreamingMapper.toStreamingResponse(savedStreaming));
    }

    @GetMapping("/{id}")
     public ResponseEntity<StreamingResponse> getById(@PathVariable Long id){
        return service.findById(id).
                map((streaming -> ResponseEntity.ok(StreamingMapper.toStreamingResponse(streaming)))).
        orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("{id}")
    public ResponseEntity<StreamingResponse> update(@PathVariable Long id, @RequestBody StreamingRequest request){
        return service.updateById(id,StreamingMapper.toStreaming(request)).
                map(streaming -> ResponseEntity.ok(StreamingMapper.toStreamingResponse(streaming))).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id){
        Optional<Streaming> optionalStreaming = service.findById(id);
        if(optionalStreaming.isPresent()){
            service.deleteById(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }


}
