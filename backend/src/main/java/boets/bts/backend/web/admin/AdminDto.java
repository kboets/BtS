package boets.bts.backend.web.admin;

import boets.bts.backend.domain.AdminKeys;

import java.util.Date;

public class AdminDto {

    private AdminKeys adminKey;
    private String value;
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
