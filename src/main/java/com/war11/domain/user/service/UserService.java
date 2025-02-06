package com.war11.domain.user.service;

import com.war11.domain.auth.service.AuthService;
import com.war11.domain.auth.service.CustomUserDetailsService;
import com.war11.domain.user.dto.request.UserRequest;
import com.war11.domain.user.dto.response.UserResponse;
import com.war11.domain.user.entity.User;
import com.war11.domain.user.repository.UserRepository;
import com.war11.global.config.CustomUserDetails;
import com.war11.global.exception.BusinessException;
import com.war11.global.exception.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final CustomUserDetailsService userDetailsService;
    private final AuthService authService;

    public UserResponse findById(Long id) {
        User foundUser = userRepository.findById(id).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_ID)
        );
        return new UserResponse(foundUser);
    }

    public Page<UserResponse> findAllUsers(CustomUserDetails userDetails, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<UserResponse> AllUsers = userRepository.findAllUsers(userDetails.getId(), pageable);

        return AllUsers.map(User -> new UserResponse(
                userDetails.getId(),
                userDetails.getLoginId(),
                userDetails.getUsername()
        ));
    }

    @Transactional
    public UserResponse updateUsers(CustomUserDetails userDetails, UserRequest request) {
        User user = userRepository.findById(userDetails.getId()).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_ID));

        if (!bCryptPasswordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST_PW);
        }

        String newPassword = bCryptPasswordEncoder.encode(request.getNewPassword());

        /*
        빌더 패턴을 사용하여 toBuilder를 이용하려 했으나 save를 사용하면 기존 객체가 아닌
        새로운 객체로 인식하여 객체 식별자가 동일하다는 에러가 뜸
        그로 인해 기존 영속성 유지하면서 저장하는 saveAndFlush를 사용하려 했으나
        user는 Id를 식별자로 사용하면서 자동으로 +1 해주는 기능,  @GeneratedValue(strategy = GenerationType.IDENTITY)를
        사용중이므로 빌드에서 Id를 설정할 수가 없음, 따라서 saveAndFlush도 사용 못함
        그래서 최종적으로 set을 사용하여 user 정보를 업데이트 하였음
         */
        user.updateName(request.getName());
        user.updatePassword(newPassword);

        User updateUser = userRepository.save(user);

        /*
        정보수정에 오류가 발생하여 기존 SpringSecurity에 저장된 SecurityContext 또한 수정을 해야한다는 것을 알게되어
        기존 정보 호출
         */
        CustomUserDetails updatedUserDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(updateUser.getLoginId());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        /*
        호출된 값을 setAuthentication를 사용하여 새 값으로 변경하고 최종 setContext하여 업데이트 처리
         */
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(updateAuthentication(authentication, updatedUserDetails));
        SecurityContextHolder.setContext(context);
        return new UserResponse(updateUser);
    }

    /*
    업데이트 인증처리에 대한 추가적인 메서드를 작성하여 기존 인증값과 현재 업데이트 된 값을 가져와서 업데이트 처리
     */
    protected Authentication updateAuthentication(Authentication authentication, CustomUserDetails userDetails) {
        UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                userDetails, authentication.getCredentials(), userDetails.getAuthorities());
        newAuth.setDetails(authentication.getDetails());

        return newAuth;
    }
}
