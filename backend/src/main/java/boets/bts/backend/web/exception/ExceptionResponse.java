package boets.bts.backend.web.exception;

import java.time.LocalDate;

public class ExceptionResponse {

    private LocalDate date;
    private String message;
    private String detail;

    public ExceptionResponse(LocalDate date, String message, String detail) {
        this.date = date;
        this.message = message;
        this.detail = detail;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
