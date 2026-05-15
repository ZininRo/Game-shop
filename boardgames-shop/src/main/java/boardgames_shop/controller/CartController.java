package boardgames_shop.controller;

import boardgames_shop.dto.cart.AddToCartRequest;
import boardgames_shop.dto.cart.CartResponse;
import boardgames_shop.dto.cart.UpdateCartItemRequest;
import boardgames_shop.service.CartService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public void addToCart(@RequestBody AddToCartRequest request) {
        cartService.addToCart(request);
    }

    @GetMapping
    public CartResponse getCart() {
        return cartService.getCart();
    }

    @PostMapping("/checkout")
    public void checkout() {
        cartService.checkout();
    }

    @PutMapping("/update")
    public void updateQuantity(
            @RequestBody UpdateCartItemRequest request
    ) {
        cartService.updateQuantity(request);
    }

    @DeleteMapping("/remove/{gameId}")
    public void removeItem(@PathVariable Long gameId) {
        cartService.removeItem(gameId);
    }
}