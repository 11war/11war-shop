package com.war11.domain.coupon.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.war11.domain.coupon.dto.request.CouponTemplateRequest;
import com.war11.domain.coupon.dto.request.CouponTemplateUpdateRequest;
import com.war11.domain.coupon.dto.response.CouponResponse;
import com.war11.domain.coupon.dto.response.CouponTemplateResponse;
import com.war11.domain.coupon.entity.Coupon;
import com.war11.domain.coupon.entity.CouponTemplate;
import com.war11.domain.coupon.entity.enums.CouponStatus;
import com.war11.domain.coupon.repository.CouponRepository;
import com.war11.domain.coupon.repository.CouponTemplateRepository;
import com.war11.domain.order.entity.Order;
import com.war11.domain.user.entity.User;
import com.war11.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {
  @Mock
  private CouponRepository couponRepository;
  @Mock
  private CouponTemplateRepository couponTemplateRepository;
  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private CouponService couponService;

  @Nested
  @DisplayName("쿠폰 템플릿 테스트")
  class CouponTemplateTest {

    @Nested
    @DisplayName("쿠폰 템플릿 생성")
    class CreateCouponTemplate {

      @Test
      @DisplayName("쿠폰 발급")
      void 쿠폰_발급_성공() {
        // given
        CouponTemplateRequest couponTemplateRequest = new CouponTemplateRequest(
            "9시 쿠폰",
            10000,
            100,
            CouponStatus.AVAILABLE,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(7));
        CouponTemplate couponTemplate = couponTemplateRequest.toEntity();
        when(couponTemplateRepository.save(any(CouponTemplate.class)))
            .thenReturn(couponTemplate);
        // when
        CouponTemplateResponse response = couponService
            .generateCouponTemplate(couponTemplateRequest);
        // then
        assertEquals(couponTemplate.getName(), response.name());
        assertEquals(couponTemplate.getValue(), response.value());
        verify(couponTemplateRepository, times(1))
            .save(any(CouponTemplate.class));
      }
    }

    @Nested
    @DisplayName("쿠폰 템플릿 조회")
    class GetCouponTemplate {

      @Test
      @DisplayName("설명")
      void 쿠폰_템플릿_모두_조회() {
        // given
        List<CouponTemplate> couponTemplates = Arrays.asList(
            CouponTemplate.builder()
                .name("9시 쿠폰")
                .value(1000)
                .quantity(100)
                .status(CouponStatus.AVAILABLE)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .build(),
            CouponTemplate.builder()
                .name("신규가입 쿠폰")
                .value(5000)
                .quantity(200)
                .status(CouponStatus.AVAILABLE)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(7))
                .build()
        );
        when(couponTemplateRepository.findAll()).thenReturn(couponTemplates);
        // when
        List<CouponTemplateResponse> results = couponService.findCouponTemplates();
        // then
        assertEquals(2, results.size());
        assertEquals("9시 쿠폰", results.get(0).name());
        assertEquals(1000, results.get(0).value());
        assertEquals(100, results.get(0).quantity());
        assertEquals("신규가입 쿠폰", results.get(1).name());
        verify(couponTemplateRepository, times(1)).findAll();
      }

      @Test
      @DisplayName("설명")
      void 특정_쿠폰_템플릿_조회() {
        // given
        Long templateId1 = 1L;
        Long templateId2 = 2L;
        List<CouponTemplate> couponTemplates = Arrays.asList(
            CouponTemplate.builder()
                .name("9시 쿠폰")
                .value(1000)
                .quantity(100)
                .status(CouponStatus.AVAILABLE)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .build(),
            CouponTemplate.builder()
                .name("신규가입 쿠폰")
                .value(5000)
                .quantity(200)
                .status(CouponStatus.AVAILABLE)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(7))
                .build()
        );
        // when
        CouponTemplate coupontemplate1 = couponTemplates.get(0);
        CouponTemplate coupontemplate2 = couponTemplates.get(1);
        when(couponTemplateRepository.findById(templateId1))
            .thenReturn(Optional.of(coupontemplate1));
        when(couponTemplateRepository.findById(templateId2))
            .thenReturn(Optional.of(coupontemplate2));
        CouponTemplateResponse result1 = couponService.findCouponTemplate(templateId1);
        CouponTemplateResponse result2 = couponService.findCouponTemplate(templateId2);
        // then
        assertEquals(coupontemplate1.getName(), result1.name());
        assertEquals(coupontemplate1.getValue(), result1.value());
        assertEquals(coupontemplate2.getName(), result2.name());
        assertEquals(coupontemplate2.getValue(), result2.value());
        verify(couponTemplateRepository, times(1)).findById(templateId1);
        verify(couponTemplateRepository, times(1)).findById(templateId2);
      }
    }

    @Nested
    @DisplayName("쿠폰 템플릿 수정")
    class UpdateCouponTemplate {

      @Test
      @DisplayName("쿠폰 수정")
      void 쿠폰_수정_성공() {
        // given
        Long templateId1 = 1L;
        List<CouponTemplate> couponTemplates = Arrays.asList(
            CouponTemplate.builder()
                .name("9시 쿠폰")
                .value(1000)
                .quantity(100)
                .status(CouponStatus.AVAILABLE)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .build(),
            CouponTemplate.builder()
                .name("신규가입 쿠폰")
                .value(5000)
                .quantity(200)
                .status(CouponStatus.AVAILABLE)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(7))
                .build()
        );
        CouponTemplateUpdateRequest updateRequest = new CouponTemplateUpdateRequest(
            "수정된 9시 쿠폰",
            2000,
            200,
            CouponStatus.AVAILABLE,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(2)
        );
        when(couponTemplateRepository.findById(templateId1))
            .thenReturn(Optional.of(couponTemplates.get(0)));
        CouponTemplate couponTemplate = couponTemplates.get(0);
        // when
        couponService.editCouponTemplate(templateId1, updateRequest);
        // then
        assertEquals(updateRequest.name(), couponTemplate.getName());
        assertEquals(updateRequest.value(), couponTemplate.getValue());
        assertEquals(updateRequest.quantity(), couponTemplate.getQuantity());
        verify(couponTemplateRepository, times(1)).findById(templateId1);
        verify(couponTemplateRepository, never()).save(any());
      }

      @Test
      @DisplayName("쿠폰 템플릿 수정 실패 - 존재하지 않는 쿠폰")
      void 쿠폰_템플릿_수정_실패() {
        // given
        Long invalidCouponTemplateId = 65L;
        List<CouponTemplate> couponTemplates = Arrays.asList(
            CouponTemplate.builder()
                .name("9시 쿠폰")
                .value(1000)
                .quantity(100)
                .status(CouponStatus.AVAILABLE)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .build(),
            CouponTemplate.builder()
                .name("신규가입 쿠폰")
                .value(5000)
                .quantity(200)
                .status(CouponStatus.AVAILABLE)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(7))
                .build()
        );
        CouponTemplateUpdateRequest updateRequest = new CouponTemplateUpdateRequest(
            "수정된 9시 쿠폰",
            2000,
            200,
            CouponStatus.AVAILABLE,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(2)
        );
        when(couponTemplateRepository.findById(invalidCouponTemplateId))
            .thenReturn(Optional.empty());
        // when
        assertThrows(NoSuchElementException.class,
            () -> couponService.editCouponTemplate(invalidCouponTemplateId, updateRequest));
        // then
        verify(couponTemplateRepository, times(1)).findById(invalidCouponTemplateId);
        verify(couponTemplateRepository, never()).save(any());
      }
    }

    @Nested
    @DisplayName("쿠폰 템플릿 삭제")
    class DeleteCouponTemplate {

      @Test
      @DisplayName("쿠폰 템플릿 삭제")
      void 쿠폰_템플릿_삭제_성공() {
        // given
        Long deleteId = 1L;
        List<CouponTemplate> couponTemplates = new ArrayList<>(Arrays.asList(
            CouponTemplate.builder()
                .name("9시 쿠폰")
                .value(1000)
                .quantity(100)
                .status(CouponStatus.AVAILABLE)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .build(),
            CouponTemplate.builder()
                .name("신규가입 쿠폰")
                .value(5000)
                .quantity(200)
                .status(CouponStatus.AVAILABLE)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(7))
                .build()
        ));
        // when
        couponService.removeCouponTemplate(deleteId);
        couponTemplates.remove(0);
        // then
        assertEquals(1, couponTemplates.size());
        assertEquals("신규가입 쿠폰", couponTemplates.get(0).getName());
        verify(couponTemplateRepository, times(1)).deleteById(deleteId);
      }

      @Test
      @DisplayName("쿠폰 템플릿 삭제 실패 - 존재하지 않는 쿠폰")
      void 쿠폰_템플릿_삭제_실패() {
        // given
        Long invalidCouponTemplateId = 65L;
        doThrow(NoSuchElementException.class)
            .when(couponTemplateRepository)
            .deleteById(invalidCouponTemplateId);
        // when
        assertThrows(NoSuchElementException.class,
            () -> couponService.removeCouponTemplate(invalidCouponTemplateId));
        // then
        verify(couponTemplateRepository, times(1)).deleteById(invalidCouponTemplateId);
      }
    }

    @Nested
    @DisplayName("쿠폰 발급")
    class IssueCoupon {

      @Test
      @DisplayName("설명")
      void 쿠폰_발급_성공() {
//        // given
//        Long userId = 1L;
//        Long couponTemplateId = 1L;
//        User user = new User();
//        Order order = new Order();
//        List<CouponTemplate> couponTemplates = Arrays.asList(
//            CouponTemplate.builder()
//                .name("9시 쿠폰")
//                .value(1000)
//                .quantity(100)
//                .status(CouponStatus.AVAILABLE)
//                .startDate(LocalDateTime.now())
//                .endDate(LocalDateTime.now().plusDays(1))
//                .build(),
//            CouponTemplate.builder()
//                .name("신규가입 쿠폰")
//                .value(5000)
//                .quantity(200)
//                .status(CouponStatus.AVAILABLE)
//                .startDate(LocalDateTime.now())
//                .endDate(LocalDateTime.now().plusDays(7))
//                .build()
//        );
//        when(userRepository.findById(userId))
//            .thenReturn(Optional.of(user));
//        when(couponTemplateRepository.findById(couponTemplateId))
//            .thenReturn(Optional.of(couponTemplates.get(0)));
//        CouponTemplate couponTemplate = couponTemplates.get(0);
//        Coupon coupon = new Coupon(
//            couponTemplate.getId(),
//            couponTemplate,
//            user,
//            order,
//            couponTemplate.getStatus(),
//            couponTemplate.getEndDate(),
//            null
//        );
////        when(couponTemplate.issueCoupon(user)).thenReturn(coupon);
//        when(couponRepository.save(coupon)).thenReturn(coupon);
//        // when
//        CouponResponse couponResponse = couponService.issueCoupon(couponTemplateId, userId);
//        // then
//        assertEquals(couponResponse.couponName(),couponTemplate.getName());
//        assertEquals(couponResponse.value(), couponTemplate.getValue());
//        verify(couponTemplateRepository,times(1)).findById(couponTemplateId);
//        verify(userRepository,times(1)).findById(userId);
//        verify(couponRepository,times(1)).save(coupon);
      }
    }
  }




  



}