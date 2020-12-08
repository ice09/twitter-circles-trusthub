package tech.blockchainers.circles.twitter.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignatureDto {

    private String message;
    private String signature;
    private String tweet;

}
