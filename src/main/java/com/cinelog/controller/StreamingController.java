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

    @GetMapping("{id}")
     public ResponseEntity<StreamingResponse> getById(@PathVariable Long id){
        return service.findById(id).
                map((streaming -> ResponseEntity.ok(StreamingMapper.toStreamingResponse(streaming)))).
        orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id){
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }


}
