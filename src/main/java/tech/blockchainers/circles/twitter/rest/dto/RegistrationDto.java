package tech.blockchainers.circles.twitter.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class RegistrationDto {

    private String twitterId;
    private String ethereumAddress;

}