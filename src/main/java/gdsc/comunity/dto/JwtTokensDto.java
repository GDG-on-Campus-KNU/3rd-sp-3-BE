package gdsc.comunity.dto;

import lombok.Builder;

@Builder
public record JwtTokensDto(
        String accessToken,
        String refreshToken
) {
}
