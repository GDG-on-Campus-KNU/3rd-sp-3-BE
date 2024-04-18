package gdsc.comunity.dto;

import lombok.Builder;

@Builder
public record RefreshTokenDto(
        String refreshToken
) {
}
