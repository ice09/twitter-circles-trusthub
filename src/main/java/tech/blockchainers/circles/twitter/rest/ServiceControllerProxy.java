package tech.blockchainers.circles.twitter.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import tech.blockchainers.circles.twitter.persistence.RegistrationRepository;
import tech.blockchainers.circles.twitter.rest.dto.RegistrationDto;

import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@RestController
public class ServiceControllerProxy {

    @Value("${tcb.id}")
    private String tcbId;

    private final RegistrationRepository registrationRepository;

    public ServiceControllerProxy(RegistrationRepository registrationRepository) {
        this.registrationRepository = registrationRepository;
    }

    @GetMapping("/registrations")
    public List<RegistrationDto> registrations(@RequestHeader("TCB-ID") String tcbId) throws Exception {
        if (!this.tcbId.equals(tcbId)) {
            throw new IllegalArgumentException("Cannot retrieve registrations.");
        }
        ConcurrentMap<String, String> allRegistrations = registrationRepository.getAllRegistrations();
        return allRegistrations.entrySet().stream().map(entry -> RegistrationDto.builder().twitterId(entry.getKey()).ethereumAddress(entry.getValue()).build()).collect(Collectors.toList());
    }

}

