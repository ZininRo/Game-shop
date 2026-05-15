package boardgames_shop.dto.admin;
public class EmployeeResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String position;
    private String role;
    private int    activeOrdersCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getActiveOrdersCount() { return activeOrdersCount; }
    public void setActiveOrdersCount(int activeOrdersCount) { this.activeOrdersCount = activeOrdersCount; }
}