package boardgames_shop.service;

import boardgames_shop.dto.cart.AddToCartRequest;
import boardgames_shop.dto.cart.CartResponse;
import boardgames_shop.dto.cart.UpdateCartItemRequest;
import boardgames_shop.entity.*;
import boardgames_shop.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CartService {

    private final OrderRepository orderRepository;
    private final GameRepository gameRepository;
    private final ClientRepository clientRepository;

    public CartService(
            OrderRepository orderRepository,
            GameRepository gameRepository,
            ClientRepository clientRepository
    ) {
        this.orderRepository = orderRepository;
        this.gameRepository = gameRepository;
        this.clientRepository = clientRepository;
    }

    public void addToCart(AddToCartRequest request) {
        System.out.println(">>> addToCart called");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Principal class: " + auth.getPrincipal().getClass().getName());
        User user = (User) auth.getPrincipal();
        System.out.println("User ID from principal: " + user.getId());

        Client client = clientRepository.findByUserId(user.getId())
                .orElseThrow();

        Order cart = orderRepository
                .findByClientAndStatus(client, OrderStatus.NOT_CREATED)
                .orElseGet(() -> createCart(client));

        Game game = gameRepository.findById(request.getGameId())
                .orElseThrow();
        if (!game.isActive()) {
            throw new RuntimeException("Игра временно недоступна для заказа");
        }
        Optional<OrderItem> existing = cart.getItems()
                .stream()
                .filter(i -> i.getGame().getId().equals(game.getId()))
                .findFirst();

        if (existing.isPresent()) {
            existing.get().setQuantity(
                    existing.get().getQuantity() + request.getQuantity()
            );
        } else {
            OrderItem item = new OrderItem();
            item.setOrder(cart);
            item.setGame(game);
            item.setQuantity(request.getQuantity());
            item.setPriceAtPurchase(game.getPrice());

            cart.getItems().add(item);
        }

        orderRepository.save(cart);
    }

    public CartResponse getCart() {
        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Client client = clientRepository.findByUserId(user.getId())
                .orElseThrow();

        Order cart = orderRepository
                .findByClientAndStatus(client, OrderStatus.NOT_CREATED)
                .orElse(null);

        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            // Корзина пуста или её нет
            CartResponse emptyResponse = new CartResponse();
            emptyResponse.setItems(new ArrayList<>());
            emptyResponse.setTotal(BigDecimal.ZERO);
            return emptyResponse;
        }

        // Удаляем позиции, у которых игра стала неактивной
        boolean removed = cart.getItems().removeIf(item -> !item.getGame().isActive());
        if (removed) {
            orderRepository.save(cart); // сохраняем корзину после удаления "мёртвых" товаров
        }

        // Сортируем оставшиеся позиции
        List<OrderItem> sorted = cart.getItems()
                .stream()
                .sorted(Comparator.comparing(OrderItem::getId))
                .toList();

        CartResponse response = new CartResponse();
        List<CartResponse.CartItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItem item : sorted) {
            CartResponse.CartItem dto = new CartResponse.CartItem();
            dto.setGameId(item.getGame().getId());
            dto.setName(item.getGame().getName());
            dto.setQuantity(item.getQuantity());
            dto.setPrice(item.getPriceAtPurchase());

            total = total.add(
                    item.getPriceAtPurchase()
                            .multiply(BigDecimal.valueOf(item.getQuantity()))
            );

            items.add(dto);
        }

        response.setItems(items);
        response.setTotal(total);
        return response;
    }

    private Order createCart(Client client) {
        Order order = new Order();
        order.setClient(client);
        order.setStatus(OrderStatus.NOT_CREATED);
        order.setOrderDate(LocalDateTime.now());
        order.setItems(new ArrayList<>());
        return orderRepository.save(order);
    }

    public void checkout() {
        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Client client = clientRepository.findByUserId(user.getId())
                .orElseThrow();

        Order cart = orderRepository
                .findByClientAndStatus(client, OrderStatus.NOT_CREATED)
                .orElseThrow(() -> new RuntimeException("Корзина пуста"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Корзина пуста");
        }

        // Проверка: все товары должны быть активны (можно вызвать getCart(), который уже чистит, но лучше явно проверить)
        for (OrderItem item : cart.getItems()) {
            if (!item.getGame().isActive()) {
                throw new RuntimeException("Игра '" + item.getGame().getName() + "' больше недоступна для заказа. Удалите её из корзины.");
            }
        }

        cart.setStatus(OrderStatus.CREATED);
        cart.setOrderDate(LocalDateTime.now());
        cart.setTotalPrice(calculateTotal(cart));

        orderRepository.save(cart);
    }

    private BigDecimal calculateTotal(Order order) {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : order.getItems()) {
            total = total.add(
                    item.getPriceAtPurchase()
                            .multiply(BigDecimal.valueOf(item.getQuantity()))
            );
        }
        return total;
    }

    public void updateQuantity(UpdateCartItemRequest request) {
        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Client client = clientRepository
                .findByUserId(user.getId())
                .orElseThrow();

        Order cart = orderRepository
                .findByClientAndStatus(client, OrderStatus.NOT_CREATED)
                .orElseThrow();

        OrderItem target = null;
        for (OrderItem item : cart.getItems()) {
            if (item.getGame().getId().equals(request.getGameId())) {
                target = item;
                break;
            }
        }

        if (target == null) {
            throw new RuntimeException("Товар не найден в корзине");
        }

        if (request.getQuantity() <= 0) {
            cart.getItems().remove(target);
        } else {
            target.setQuantity(request.getQuantity());
        }

        orderRepository.save(cart);
    }

    public void removeItem(Long gameId) {
        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Client client = clientRepository
                .findByUserId(user.getId())
                .orElseThrow();

        Order cart = orderRepository
                .findByClientAndStatus(client, OrderStatus.NOT_CREATED)
                .orElseThrow();

        cart.getItems().removeIf(
                item -> item.getGame().getId().equals(gameId)
        );

        orderRepository.save(cart);
    }
}