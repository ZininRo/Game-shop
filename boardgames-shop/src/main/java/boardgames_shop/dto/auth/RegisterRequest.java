// src/main/java/boardgames_shop/dto/auth/RegisterRequest.java
package boardgames_shop.dto.auth;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class RegisterRequest {

    @NotBlank(message = "Имя обязательно")
    private String name;

    @NotBlank @Email(message = "Некорректный email")
    private String email;

    @NotBlank @Size(min = 6, message = "Пароль минимум 6 символов")
    private String password;

    private String phone;

    @Past(message = "Дата рождения должна быть в прошлом")
    private LocalDate birthDate;

    // getters/setters без изменений
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
}