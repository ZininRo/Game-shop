package boardgames_shop.dto.auth;

public class LoginResponse {

    private Long userId;
    private String role;
    private String token;

    public LoginResponse() {
    }

    public LoginResponse(Long userId, String role, String token) {
        this.userId = userId;
        this.role = role;
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }

    public String getToken() {
        return token;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setToken(String token) {
        this.token = token;
    }
}