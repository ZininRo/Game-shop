package boardgames_shop.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String fullName;

    private String phone;

    private LocalDate birthDate;

    public Client() {
    }

    public Client(User user, String fullName, String phone, LocalDate birthDate) {
        this.user = user;
        this.fullName = fullName;
        this.phone = phone;
        this.birthDate = birthDate;
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

    public LocalDate getBirthDate() {
        return birthDate;
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

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
}