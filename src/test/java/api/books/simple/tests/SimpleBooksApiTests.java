package api.books.simple.tests;

import static api.books.simple.api.ApiOperations.*;
import static api.books.simple.api_constants.ApiStatus.*;
import static io.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.*;
import static api.books.simple.api_constants.ApiEndPoints.*;


import api.books.simple.pojo.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

public class SimpleBooksApiTests {



    @BeforeClass
    public static void setUp() {
        baseURI = BASE_URI;
    }

    @Test
    public void getApiStatusTest() {
        performGetRequest(GET_STATUS_ENDPOINT,false)
                .then()
                .statusCode(OK)
                .contentType(ContentType.JSON)
                .body("status",equalTo("OK"));
    }

    @Test
    public void getListOfBooksTest() {

        performGetRequest(GET_ALL_BOOKS_ENDPOINT,false)
                .then()
                .statusCode(OK)
                .body("",Matchers.instanceOf(List.class))
                .contentType(ContentType.JSON)
                .body("size()",equalTo(6));


    }

    @Test
    public void getListOfBooksVerifyEachTest() {
        Response response = performGetRequest(GET_ALL_BOOKS_ENDPOINT,false)
                .then()
                .statusCode(OK)
                .extract()
                .response();
        BookResponse[] bookObjects = response.as(BookResponse[].class);
        for(BookResponse bookResponse : bookObjects){
            Assert.assertTrue(bookResponse.getId() != null);
        }
    }

    @Test
    public void getListOfBooksWithTypeQueryParamTest() {
        performGetRequestQueryParam(GET_ALL_BOOKS_ENDPOINT,"type", "fiction",false)
                .then()
                .statusCode(OK)
                .body("",Matchers.instanceOf(List.class))
                .contentType(ContentType.JSON)
                .body("size()",equalTo(4));
    }

    @Test
    public void getListOfBooksWithLimitQueryParamTest() {
       performGetRequestQueryParam(GET_ALL_BOOKS_ENDPOINT,"limit","3",false)
               .then()
               .statusCode(OK)
               .body("", Matchers.instanceOf(List.class))
               .contentType(ContentType.JSON)
               .body("size()", equalTo(3));
    }

    @Test
    public void getSingleBookByIdTest() {
        performGetRequestPathParam(GET_ONE_BOOK_ENDPOINT,"bookId","3",false)
                .then()
                .statusCode(OK)
                .contentType(ContentType.JSON)
                .body("id",equalTo(3));
    }

    @Test
    public void getSingleBookByIdValidateAllFieldsJSONTest() {

        Response response = performGetRequestPathParam(GET_ONE_BOOK_ENDPOINT,"bookId","1",false)
                .then()
                .statusCode(OK)
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
        Response response = performGetRequestPathParam(GET_ONE_BOOK_ENDPOINT,"bookId","1",false)
                .then()
                .statusCode(OK)
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
        performGetRequestPathParam(GET_ONE_BOOK_ENDPOINT,"bookId","20",false)
                .then()
                .statusCode(NOT_FOUND)
                .contentType(ContentType.JSON)
                .body("error", equalTo("No book with id 20"))
                .body("name", equalTo(null));
    }

    @Test
    public void postSubmitBookOrderTest(){
        SubmitOrderRequest requestPayload = new SubmitOrderRequest(1, "Tom Smith");
        performPostRequest(POST_ORDERS_ENDPOINT,requestPayload,true)
                .then()
                .statusCode(CREATED)
                .contentType(ContentType.JSON)
                .body("created",equalTo(true))
                .body("orderId",notNullValue());
    }

    @Test
    public void postSubmitBookOrderBadTest(){
        performPostRequest(POST_ORDERS_ENDPOINT,"{\n" +
                "  \"bookId\": 1,\n" +
                "  \"customerName\": \"Tom Smith\"\n" +
                "  \n" +
                "}",true)
                .then()
                .statusCode(CREATED)
                .contentType(ContentType.JSON)
                .body("created",equalTo(true))
                .body("orderId",notNullValue());
    }

    @Test
    public void postSubmitBookOrderWithNoAccessTokenTest(){
        performPostRequest(POST_ORDERS_ENDPOINT,"{\n" +
                "  \"bookId\": 1,\n" +
                "  \"customerName\": \"Tom Smith\"\n" +
                "  \n" +
                "}",false)
                .then()
                .statusCode(UNAUTHORIZED)
                .contentType(ContentType.JSON)
                .body("error",equalTo("Missing Authorization header."));
    }

    @Test
    public void postSubmitBookOrderWithInvalidTokenTest(){
        performPostRequestInvalidToken(POST_ORDERS_ENDPOINT,"{\n" +
                "  \"bookId\": 1,\n" +
                "  \"customerName\": \"Tom Smith\"\n" +
                "  \n" +
                "}",true)
                .then()
                .statusCode(UNAUTHORIZED)
                .contentType(ContentType.JSON)
                .body("error",equalTo("Invalid bearer token."));
    }

    @Test
    public void getAllOrdersTest(){
        performGetRequest(GET_All_ORDERS_ENDPOINT,true)
                .then()
                .statusCode(OK)
                .contentType(ContentType.JSON)
                .body("",Matchers.instanceOf(List.class));
    }

    @Test
    public void getSingleOrderTest(){
        String orderId = placeOrderAndGetId();
        performGetRequestPathParam(GET_ONE_ORDER_ENDPOINT,"orderId",orderId,true)
                .then()
                .statusCode(OK)
                .contentType(ContentType.JSON)
                .body("id",equalTo(orderId));
    }

    @Test
    public void deleteOrderTest(){
        String orderId = placeOrderAndGetId();
        performDeleteRequest(DELETE_ONE_ORDER_ENDPOINT,"orderId",orderId,true)
                .then()
                .statusCode(NO_CONTENT);
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
                .patch(PATCH_ONE_ORDER_ENDPOINT)
                .then()
                .statusCode(NO_CONTENT);

        Response response = given()
                .pathParam("orderId", orderId)
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .get(GET_ONE_ORDER_ENDPOINT)
                .then()
                .statusCode(OK)
                .contentType(ContentType.JSON)
                .extract()
                .response();
        OrderDetailsResponse orderDetailsResponse = response.as(OrderDetailsResponse.class);
        System.out.println(orderDetailsResponse);
        Assert.assertEquals(orderId,orderDetailsResponse.getId());
        Assert.assertEquals(1,orderDetailsResponse.getBookId());
        Assert.assertEquals(updateCustomerName,orderDetailsResponse.getCustomerName());
        Assert.assertEquals(1,orderDetailsResponse.getQuantity());




    }


    private String placeOrderAndGetId(){
        return given()
                .contentType(ContentType.JSON)
                .header("Authorization","Bearer " + ACCESS_TOKEN)
                .body(new SubmitOrderRequest(1,"Tom Smith"))
                .post(POST_ORDERS_ENDPOINT)
                .then()
                .statusCode(CREATED)
                .contentType(ContentType.JSON)
                .extract()
                .response()
                .jsonPath()
                .getString("orderId");


    }

    public static String generateToken(){

        ClientRequestBody requestBody = new ClientRequestBody("Tom Smith","TomSmith@gmail.com");

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(POST_REGISTER_CLIENT_ENDPOINT)
                .then()
                .statusCode(CREATED)
                .extract()
                .response();
        String accessToken = response.as(ApiClientResponseBody.class).getAccessToken();
        return accessToken;
    }

//    public static void main(String[] args) {
//        System.out.println(generateToken());
//    }

}
