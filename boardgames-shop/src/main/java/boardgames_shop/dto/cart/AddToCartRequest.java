package boardgames_shop.dto.cart;

public class AddToCartRequest {

    private Long gameId;
    private Integer quantity;

    public AddToCartRequest() {
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}