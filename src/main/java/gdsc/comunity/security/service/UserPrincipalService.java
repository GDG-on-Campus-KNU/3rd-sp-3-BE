package gdsc.comunity.security.service;

import gdsc.comunity.entity.user.User;
import gdsc.comunity.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserPrincipalService implements UserDetailsService {
    private final UserRepository userRepository;

    /**
     * OAuth2 로그인만 구현해놓은 상태에서 해당 로직을 사용하지 않음.
     * 따라서 임시로 null을 반환한다.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
