package boets.bts.backend.web.admin;

import boets.bts.backend.domain.AdminKeys;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class AdminDto {

    private AdminKeys adminKey;
    private String value;
    private LocalDateTime date;

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
