package vn.com.vndirect.report.response;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class ErrorResponse {
    
    private final int ID_LENGTH = 5;
    private final int charA =  'a';
    private final int charZPlusOne =  'z' + 1;

    private String id;
    private String error;
    private String message;
    private String description;

    public ErrorResponse(String code, String message, Throwable ex) {
        this.id = generateId();
        this.error = code;
        this.message = message;
        this.description = Objects.isNull(ex) ? "" : ex.getMessage();
    }

    private String generateId() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ID_LENGTH; i++) {
            int ascii = ThreadLocalRandom.current().nextInt(charA, charZPlusOne);
            sb.append((char) ascii);
        }
        return sb.toString();
    }

    public String getMessage() { return message; }

    public String getError() { return error; }

    public String getDescription() { return description; }

    public String getId() { return id; }

    @Override
    public String toString() {
        return "ErrorResponse [id=" + id + ", error=" + error + ", message=" + message + ", description=" + description
                + "]";
    }

}
