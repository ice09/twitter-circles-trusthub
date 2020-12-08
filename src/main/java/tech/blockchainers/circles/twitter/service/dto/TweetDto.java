package tech.blockchainers.circles.twitter.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
public class TweetDto {

    private List<TweetContentDto> data;

}
