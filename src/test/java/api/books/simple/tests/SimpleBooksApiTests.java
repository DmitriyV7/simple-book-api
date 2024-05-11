package api.books.simple.tests;

import api.books.simple.pojo.*;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static io.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.notNullValue;

public class SimpleBooksApiTests {

    private static final String BASE_URL = "https://simple-books-api.glitch.me";
    private static final String ACCESS_TOKEN = generateToken();

    @BeforeClass
    public static void setUp() {
        //setting up the base url for rest assured before it makes a
        //request to any end point
        baseURI = BASE_URL;
    }

    @Test
    public void getApiStatusTest() {
        given()
                .when()
                .get("/status")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", equalTo("OK"));
    }

    @Test
    public void getListOfBooksTest() {
        given()
                .when()
                .get("/books")
                .then()
                .statusCode(200)
                .body("", Matchers.instanceOf(List.class))
                .contentType(ContentType.JSON)
                .body("size()", equalTo(6));
    }

    @Test
    public void getListOfBooksVerifyEachTest() {
        Response response = given()
                .when()
                .get("/books")
                .then()
                .statusCode(200)
                .extract()
                .response();
        BookResponse[] bookObjects = response.as(BookResponse[].class);
        for(BookResponse bookResponse : bookObjects){
            Assert.assertTrue(bookResponse.getId() != null);
        }
    }

    @Test
    public void getListOfBooksWithTypeQueryParamTest() {
        given()
                .queryParam("type", "fiction")
                .when()
                .get("/books")
                .then()
                .statusCode(200)
                .body("", Matchers.instanceOf(List.class))
                .contentType(ContentType.JSON)
                .body("size()", equalTo(4));
    }

    @Test
    public void getListOfBooksWithLimitQueryParamTest() {
        given()
                .queryParam("limit", 3)
                .when()
                .get("/books")
                .then()
                .statusCode(200)
                .body("", Matchers.instanceOf(List.class))
                .contentType(ContentType.JSON)
                .body("size()", equalTo(3));
    }

    @Test
    public void getSingleBookByIdTest() {
        given()
                .pathParam("bookId", 3)
                .when()
                .get("/books/{bookId}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(3));
    }

    @Test
    public void getSingleBookByIdValidateAllFieldsJSONTest() {
        Response response = given()
                .pathParam("bookId", 1)
                .when()
                .get("/books/{bookId}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .response();
        int id = response.jsonPath().getInt("id");
        String name = response.jsonPath().getString("name");
        String author = response.jsonPath().getString("author");
        int isbn = response.jsonPath().getInt("isbn");
        String type = response.jsonPath().getString("type");
        double price = response.jsonPath().getDouble("price");
        int currentStock = response.jsonPath().getInt("current-stock");
        boolean available = response.jsonPath().getBoolean("available");

        Assert.assertEquals(1,id);
        Assert.assertEquals("The Russian",name);
        Assert.assertEquals("James Patterson and James O. Born", author);
        Assert.assertEquals(1780899475,isbn);
        Assert.assertEquals("fiction",type);
        Assert.assertEquals(12.98,price,0.01);
        Assert.assertEquals(12,currentStock);
        Assert.assertEquals(true,available);
        Assert.assertTrue(available);
    }

    @Test
    public void getSingleBookByIdValidateAllFieldsPojoTest() {
        Response response = given()
                .pathParam("bookId", 1)
                .when()
                .get("/books/{bookId}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .response();
        BookFullDetailsResponse bookObj = response.as(BookFullDetailsResponse.class);
        System.out.println(bookObj);
        Assert.assertEquals(1,bookObj.getId());
        Assert.assertEquals("The Russian",bookObj.getName());
        Assert.assertEquals("James Patterson and James O. Born", bookObj.getAuthor());
        Assert.assertEquals(1780899475,bookObj.getIsbn());
        Assert.assertEquals("fiction", bookObj.getType());
        Assert.assertEquals(12.98, bookObj.getPrice(),0.01);
        Assert.assertEquals(12,bookObj.getCurrentStock());
        Assert.assertEquals(true,bookObj.isAvailable());
    }

    @Test
    public void getSingleBookByIdNegativeTest() {
        given()
                .pathParam("bookId", 20)
                .when()
                .get("/books/{bookId}")
                .then()
                .statusCode(404)
                .contentType(ContentType.JSON)
                .body("error", equalTo("No book with id 20"))
                .body("name", equalTo(null));
    }

    @Test
    public void postSubmitBookOrderTest(){
        SubmitOrderRequest bookBody = new SubmitOrderRequest(1,"DmitriyV77");
        given()
                .contentType(ContentType.JSON)
                .header("Authorization","Bearer " + ACCESS_TOKEN)
                .body(bookBody)
                .post("/orders")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("created",equalTo(true))
                .body("orderId",notNullValue());
    }

    @Test
    public void postSubmitBookOrderBadTest(){
        SubmitOrderRequest bookBody = new SubmitOrderRequest(1,"DmitriyV77");
        given()
                .contentType(ContentType.JSON)
                .header("Authorization","Bearer " + ACCESS_TOKEN)
                .body("{\n" +
                        "  \"bookId\": 1,\n" +
                        "  \"customerName\": \"DmitriyV\"\n" +
                        "  \n" +
                        "}")
                .post("/orders")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("created",equalTo(true))
                .body("orderId",notNullValue());
    }

    @Test
    public void postSubmitBookOrderWithNoAccessTokenTest(){
        SubmitOrderRequest bookBody = new SubmitOrderRequest(1,"DmitriyV77");
        given()
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "  \"bookId\": 1,\n" +
                        "  \"customerName\": \"DmitriyV\"\n" +
                        "  \n" +
                        "}")
                .post("/orders")
                .then()
                .statusCode(401)
                .contentType(ContentType.JSON)
                .body("error",equalTo("Missing Authorization header."));
    }

    @Test
    public void postSubmitBookOrderWithInvalidTokenTest(){
        SubmitOrderRequest bookBody = new SubmitOrderRequest(1,"DmitriyV77");
        given()
                .header("Authorization","Bearer i" + ACCESS_TOKEN)
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "  \"bookId\": 1,\n" +
                        "  \"customerName\": \"DmitriyV\"\n" +
                        "  \n" +
                        "}")
                .post("/orders")
                .then()
                .statusCode(401)
                .contentType(ContentType.JSON)
                .body("error",equalTo("Invalid bearer token."));
    }

    @Test
    public void getAllOrdersTest(){
        given()
                .header("Authorization","Bearer " + ACCESS_TOKEN)
                .get("/orders")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("",Matchers.instanceOf(List.class));
    }

    @Test
    public void getSingleOrderTest(){

        String orderId = placeOrderAndGetId();
//        System.out.println(orderId);

        given()
                .pathParam("orderId", orderId)
                .header("Authorization","Bearer " + ACCESS_TOKEN)
                .get("/orders/{orderId}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id",equalTo(orderId));
    }

    @Test
    public void deleteOrderTest(){

        String orderId = placeOrderAndGetId();
        System.out.println(orderId);

        given()
                .pathParam("orderId", orderId)
                .header("Authorization","Bearer " + ACCESS_TOKEN)
                .delete("/orders/{orderId}")
                .then()
                .statusCode(204);
    }

    @Test
    public void patchOrderTest(){

        String orderId = placeOrderAndGetId();
        String updateCustomerName = "Sam Peretson";
        UpdateOrderRequest updateOrderRequest = new UpdateOrderRequest(updateCustomerName);
        System.out.println(orderId);

        given()
                .pathParam("orderId", orderId)
                .header("Authorization","Bearer " + ACCESS_TOKEN)
                .contentType(ContentType.JSON)
                .body(updateOrderRequest)
                .patch("/orders/{orderId}")
                .then()
                .statusCode(204);

        Response response = given()
                .pathParam("orderId", orderId)
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .get("/orders/{orderId}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .response();
        OrderDetailsResponse orderDetailsResponse = response.as(OrderDetailsResponse.class);
        System.out.println(orderDetailsResponse);
        Assert.assertEquals(orderId,orderDetailsResponse.getId());
        Assert.assertEquals(1,orderDetailsResponse.getBookId());
        Assert.assertEquals(updateCustomerName,orderDetailsResponse.getCustomerName());
        Assert.assertEquals(ACCESS_TOKEN,orderDetailsResponse.getCreatedBy());
        Assert.assertEquals(1,orderDetailsResponse.getQuantity());
//        Assert.assertEquals();



    }


    private String placeOrderAndGetId(){
        return given()
                .contentType(ContentType.JSON)
                .header("Authorization","Bearer " + ACCESS_TOKEN)
                .body(new SubmitOrderRequest(1,"DmitriyV77"))
                .post("/orders")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .extract()
                .response()
                .jsonPath()
                .getString("orderId");


    }

    public static String generateToken(){

        ClientRequestBody requestBody = new ClientRequestBody("Kevin Bee","KevinBee13@gmail.com");

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(BASE_URL + "/api-clients")
                .then()
                .statusCode(201)
                .extract()
                .response();
        String accessToken = response.as(ApiClientResponseBody.class).getAccessToken();
        return accessToken;
    }

    public static void main(String[] args) {
        System.out.println(generateToken());
    }

}
