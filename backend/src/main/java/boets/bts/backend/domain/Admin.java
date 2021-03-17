package boets.bts.backend.domain;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "ADMIN")
public class Admin {

    @Id
    @Column(name = "admin_key", nullable = false)
    @Enumerated(EnumType.STRING)
    private AdminKeys adminKey;
    @Column
    private String value;
    @Column(nullable = false)
    private LocalDateTime date;


    public Admin(AdminKeys adminKey) {
        this.adminKey = adminKey;
    }

    public Admin() {
    }

    public AdminKeys getAdminKey() {
        return adminKey;
    }

    public void setAdminKey(AdminKeys adminKey) {
        this.adminKey = adminKey;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
