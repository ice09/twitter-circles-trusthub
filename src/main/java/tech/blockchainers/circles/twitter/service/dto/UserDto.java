package tech.blockchainers.circles.twitter.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Builder
public class UserDto {

    private UserContentDto data;
}
