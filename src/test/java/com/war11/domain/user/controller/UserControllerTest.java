package com.war11.domain.user.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

import com.war11.domain.user.dto.response.UserResponse;
import com.war11.domain.user.entity.User;
import com.war11.domain.user.repository.UserRepository;
import com.war11.domain.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @MockitoBean
    private UserService userService;

    @Test
    void 단일회원_조회성공() {
        //given
        long userId = 1L;
        User user = new User("jjy","password","주양");

        //when
        when(userService.findById(userId)).thenReturn(UserResponse.toDto(user));
        UserResponse foundUser = userService.findById(userId);

        //then
        assertThat(foundUser.getId()).isEqualTo(user.getId());
        assertThat(foundUser.getLoginId()).isEqualTo(user.getLoginId());
        assertThat(foundUser.getName()).isEqualTo(user.getName());
    }
}
