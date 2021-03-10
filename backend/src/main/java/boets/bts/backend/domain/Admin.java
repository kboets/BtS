package boets.bts.backend.domain;

import javax.persistence.*;
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
    private Date date;


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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
