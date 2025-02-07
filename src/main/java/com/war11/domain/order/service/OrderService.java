package com.war11.domain.order.service;

import com.war11.domain.cart.entity.Cart;
import com.war11.domain.cart.entity.CartProduct;
import com.war11.domain.cart.repository.CartProductRepository;
import com.war11.domain.cart.repository.CartRepository;
import com.war11.domain.coupon.annotation.Lock;
import com.war11.domain.coupon.repository.CouponRepository;
import com.war11.domain.lock.service.LockService;
import com.war11.domain.order.dto.request.ChangeOrderStatusRequest;
import com.war11.domain.order.dto.request.OrderRequest;
import com.war11.domain.order.dto.response.CancelOrderResponse;
import com.war11.domain.order.dto.response.GetAllOrdersResponse;
import com.war11.domain.order.dto.response.OrderProductResponse;
import com.war11.domain.order.dto.response.OrderResponse;
import com.war11.domain.order.dto.response.UpdateOrderResponse;
import com.war11.domain.order.entity.Order;
import com.war11.domain.order.entity.OrderProduct;
import com.war11.domain.order.entity.enums.OrderStatus;
import com.war11.domain.order.repository.OrderProductRepository;
import com.war11.domain.order.repository.OrderRepository;
import com.war11.domain.product.entity.Product;
import com.war11.domain.product.repository.ProductRepository;
import com.war11.domain.user.entity.User;
import com.war11.domain.user.repository.UserRepository;
import com.war11.global.exception.base.InvalidRequestException;
import com.war11.global.exception.base.NotFoundException;
import com.war11.global.exception.enums.ErrorCode;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

  private final CartRepository cartRepository;
  private final CartProductRepository cartProductRepository;
  private final UserRepository userRepository;
  private final OrderRepository orderRepository;
  private final OrderProductRepository orderProductRepository;
  private final ProductRepository productRepository;
  private final LockService lockService;
  private final CouponRepository couponRepository;
  private final EntityManager em;

  /**
   * 주문 생성 로직 <br>
   * 1. {@code userId}, {@code discountPrice} 입력받음. <br>
   * 2. {@code userId}로 유저와 카트 객체 조회. <br>
   * 3. 조회된 장바구니의 상품 중 {@code isChecked}가 true인 상품들로 리스트 생성. <br>
   * 4. 주문 생성 시점에 주문 수량만큼 각 상품 재고 감소시킴 <br>
   * 5. 주문 생성 <br>
   * 6. {@code cartProduct}리스트에서 스트림으로 {@code orderProduct}로 변환 <br>
   * 7. {@code orderProducts}모두 리포지토리에 저장, dto로 변환해서 리스트 생성 <br>
   * 8. {@code orderProductResponse}리스트와 {@code discountPrice}입력해서 Order객체 완성 <br>
   * 9. 저장된 order객체 {@code responseDto}로 변환, 장바구니에서 주문한 상품들 삭제 <br>
   * 10. 장바구니가 비었을 경우 {@code Cart}객체 삭제하고 {@code responseDto} 반환 <br>
   */
  @Transactional
  public OrderResponse createOrder(Long userId, OrderRequest request) {
    User foundUser = findEntity(userRepository, userId, ErrorCode.USER_NOT_FOUND);
    Long discountPrice = request.discountPrice();
    //주문 생성
    Order order = orderRepository.save(new Order(foundUser));

    try {
      Cart foundCart = cartRepository.findCartByUserId(userId)
          .orElseThrow(() -> new NotFoundException(ErrorCode.CART_IS_EMPTY));

      List<CartProduct> cartProducts = cartProductRepository.findCartProductByCartIdAndIsChecked(
          foundCart.getId(), true);

      List<OrderProduct> orderProducts = cartProducts.stream()
          .map(cartProduct -> dcereateProductQuantityAndCreateOrderProduct(cartProduct, order))
          .toList();

      order.updateOrderDetails(discountPrice, orderProducts);
      if (order.getDiscountedPrice() > order.getTotalPrice()) {
        log.warn("할인가격({})가 총 가격({})보다 커서 조정됨", discountPrice, order.getTotalPrice());

        discountPrice = order.getTotalPrice();
        order.updateOrderDetails(discountPrice, orderProducts);
      }

      orderProductRepository.saveAll(orderProducts);

      // 카트 비우기
      cartProductRepository.deleteAll(cartProducts);

      return order.toDto(
          orderProducts.stream()
              .map(OrderProduct::toDto)
              .toList()
      );
    } catch (Exception e) {
      order.updateOrderStatus(OrderStatus.CANCELLED);
      throw e;
    }
  }

  private OrderProduct dcereateProductQuantityAndCreateOrderProduct(CartProduct cartProduct,
      Order order) {
    lockService.lock(String.valueOf(cartProduct.getId()));
    Product product = cartProduct.getProduct();

    if (product.getQuantity() < cartProduct.getQuantity()) {
      throw new InvalidRequestException(ErrorCode.INSUFFICIENT_STOCK);
    }
    product.downToQuantity(cartProduct.getQuantity());

    return new OrderProduct(order, product.getId(), product.getName(), product.getPrice(),
        cartProduct.getQuantity());
  }

  /**
   * 모든 주문내역 조회 <br> 1. {@code userId}입력받음 <br> 2. 입력받은 아이디로 {@code order}객체 전체 조회후 리스트에 담음. <br> 3.
   * {@code orders}를 스트림으로 돌면서 각 주문마다 {@code orderId}, {@code productNames}, {@code totalPrice},
   * {@code orderStatus}가 담긴 dto로 변환 <br> 4. {@code getAllOrdersResponse} 반환
   */
  public List<GetAllOrdersResponse> getAllOrder(Long userId) {
    List<Order> orders = orderRepository.findByUserId(userId);

    return orders.stream()
        .map(order -> {
          List<OrderProduct> orderProducts = orderProductRepository.findByOrderId(order.getId());
          return GetAllOrdersResponse.builder()
              .orderId(order.getId())
              .productNames(orderProducts.stream()
                  .map(OrderProduct::getProductName)
                  .toList())
              .totalPrice(order.getTotalPrice())
              .orderStatus(order.getStatus())
              .build();
        })
        .toList();
  }

  public OrderResponse getOrder(Long orderId) {
    Order order = findEntity(orderRepository, orderId, ErrorCode.ORDER_NOT_FOUND);
    List<OrderProduct> orderProducts = orderProductRepository.findByOrderId(orderId);

    List<OrderProductResponse> orderProductResponses = orderProducts.stream()
        .map(OrderProduct::toDto)
        .toList();

    return order.toDto(orderProductResponses);
  }

  @Transactional
  public UpdateOrderResponse updateOrder(Long orderId, ChangeOrderStatusRequest request) {
    Order order = findEntity(orderRepository, orderId, ErrorCode.ORDER_NOT_FOUND);
    order.updateOrderStatus(request.orderStatus());

    return new UpdateOrderResponse(orderId, "배송 상태가 변경되었습니다.", order.getStatus());
  }

  /**
   * 주문 취소하기 로직 <br> 1. {@code orderId} 입력받음. <br> 2. 주문 취소 후 주문에 있던 상품들 재고 반환 <br> 3.
   * {@code orderId}, {@code message}, {@code orderStatus} 담아서 responseDto로 반환
   */
  @Lock
  @Transactional
  public CancelOrderResponse cancelOrder(Long orderId) {
    Order order = findEntity(orderRepository, orderId, ErrorCode.ORDER_NOT_FOUND);
    if (order.getStatus() == OrderStatus.CANCELLED) {
      throw new InvalidRequestException(ErrorCode.ALREADY_CANCELED);
    }
    if (order.getStatus() != OrderStatus.PAID) {
      throw new InvalidRequestException(ErrorCode.CANNOT_CANCEL_ORDER);
    }

    List<OrderProduct> orderProducts = orderProductRepository.findByOrderId(orderId);
    orderProducts.forEach(orderProduct -> {
      Product foundProduct = findEntity(productRepository, orderProduct.getId(),
          ErrorCode.PRODUCT_NOT_FOUND);
      foundProduct.upToQuantity(orderProduct.getQuantity());
    });

    order.cancelThisOrder();

    return new CancelOrderResponse(orderId, "주문이 취소되었습니다.", order.getStatus());
  }

  /**
   * 제네릭타입 find 메서드 <br> 타입 별로 {@code repository}, {@code id}, {@code errorCode}대입받음. <br> 각 레포지토리에서
   * {@code id}기준으로 탐색, 없을 시 예외 발생. <br>
   */
  private <T> T findEntity(JpaRepository<T, Long> repository, Long id, ErrorCode errorCode) {
    return repository.findById(id).orElseThrow(
        () -> new NotFoundException(errorCode)
    );
  }
}
