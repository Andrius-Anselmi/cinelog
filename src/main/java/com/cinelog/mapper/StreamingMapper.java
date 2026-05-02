package com.cinelog.mapper;

import com.cinelog.controller.request.StreamingRequest;
import com.cinelog.controller.response.StreamingResponse;
import com.cinelog.entity.Streaming;
import lombok.experimental.UtilityClass;

@UtilityClass
public class StreamingMapper {

    public static Streaming toStreaming(StreamingRequest request){
        return Streaming.builder()
                .name(request.name())
                .build();

    }

    public static StreamingResponse toStreamingResponse(Streaming streaming){
        return StreamingResponse.builder()
                .id(streaming.getId())
                .name(streaming.getName())
                .build();
    }
}
