package tech.blockchainers.circles.twitter.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class TweetContentDto {

    private String text;
    private String author_id;

}
