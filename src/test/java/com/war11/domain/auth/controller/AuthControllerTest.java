package com.war11.domain.auth.controller;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

import com.war11.domain.auth.dto.request.SigninRequest;
import com.war11.domain.auth.dto.request.SignupRequest;
import com.war11.domain.auth.dto.response.SigninResponse;
import com.war11.domain.auth.dto.response.SignupResponse;
import com.war11.domain.auth.service.AuthService;
import com.war11.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
public class AuthControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private AuthService authService;


    @Test
    void 회원_가입성공() {
        //given
        SignupRequest request = new SignupRequest("jjy","주양","password");

        //when 회원가입 로직
        when(userRepository.existsByLoginId("jjy")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("암호화 됨");
        when(authService.signup(request)).thenReturn(new SignupResponse("회원가입이 완료되었습니다."));

        SignupResponse savedUser = authService.signup(request);

        //then findbyid
        assertThat(savedUser.getMessage()).isEqualTo("회원가입이 완료되었습니다.");
    }

    @Test
    void 회원_로그인성공() {
        //given
        String pw = passwordEncoder.encode("password");
        String token = "1234567890";
        SigninRequest request = new SigninRequest("jjy", "password");

        //when
        when(userRepository.existsByLoginId("jjy")).thenReturn(true);
        when(passwordEncoder.matches(request.getPassword(),pw)).thenReturn(true);
        when(authService.signin(request)).thenReturn(new SigninResponse(token,"로그인에 성공했습니다."));

        SigninResponse loginUser = authService.signin(request);

        //then
        assertThat(loginUser.getToken()).isEqualTo(token);
        assertThat(loginUser.getMessage()).isEqualTo("로그인에 성공했습니다.");
    }

}
