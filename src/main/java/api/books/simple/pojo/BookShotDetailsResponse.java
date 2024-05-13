package api.books.simple.pojo;

import java.util.Objects;

public class BookShotDetailsResponse {

    private String id;
    private  int bookId;
    private String customerName;
    private int createdBy;
    private int quantity;
    private int timestamp;

    public BookShotDetailsResponse() {
    }

    public BookShotDetailsResponse(String id, int bookId, String customerName, int createdBy, int quantity, int timestamp) {
        this.id = id;
        this.bookId = bookId;
        this.customerName = customerName;
        this.createdBy = createdBy;
        this.quantity = quantity;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookShotDetailsResponse that = (BookShotDetailsResponse) o;
        return bookId == that.bookId && quantity == that.quantity && timestamp == that.timestamp && Objects.equals(id, that.id) && Objects.equals(customerName, that.customerName) && Objects.equals(createdBy, that.createdBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, bookId, customerName, createdBy, quantity, timestamp);
    }
}
