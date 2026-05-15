package boardgames_shop.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String fullName;

    private String phone;

    private String position;

    public Employee() {
    }

    public Employee(User user, String fullName, String phone, String position) {
        this.user = user;
        this.fullName = fullName;
        this.phone = phone;
        this.position = position;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public String getPosition() {
        return position;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}