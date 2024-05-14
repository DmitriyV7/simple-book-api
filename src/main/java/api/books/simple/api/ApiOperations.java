package api.books.simple.api;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class ApiOperations {

    private ApiOperations(){}

    public static final String ACCESS_TOKEN = "0963ea11f5a92351b7ff9faae3c87507baa1c050d391c6afe2dbb5c953b0c9dc";
    public static final String INVALID_TOKEN = "INVALID1f5a92351b7ff9faae3c87507baa1c050d391c6afe2dbb5c953b0c9dc";

    public static Response performGetRequest(String endpoint, boolean requiresAuth){
        RequestSpecification requestSpecification = given();
        if (requiresAuth){
            requestSpecification = requestSpecification.header("Authorization", ACCESS_TOKEN);
        }
        return requestSpecification
                .when()
                .get(endpoint);
    }

    public static Response performGetRequestQueryParam(String endpoint, String paramKey, String paramValue, boolean requiresAuth){
        RequestSpecification requestSpecification = given();
        if (requiresAuth){
            requestSpecification = requestSpecification.header("Authorization", ACCESS_TOKEN);
        }
        return  requestSpecification
                .queryParam(paramKey, paramValue)
                .when()
                .get(endpoint);
    }

    public static Response performGetRequestPathParam(String endpoint, String paramKey, String paramValue, boolean requiresAuth){
        RequestSpecification requestSpecification = given();
        if (requiresAuth){
            requestSpecification = requestSpecification.header("Authorization", ACCESS_TOKEN);
        }
        return   requestSpecification
                .pathParams(paramKey, paramValue)
                .when()
                .get(endpoint);
    }
    public static Response performPostRequest(String endpoint, Object payload, boolean requiresAuth){
        RequestSpecification requestSpecification = given();
        if (requiresAuth){
            requestSpecification = requestSpecification.header("Authorization", ACCESS_TOKEN);
        }
        return   requestSpecification
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post(endpoint);
    }
    public static Response performPostRequestInvalidToken(String endpoint, Object payload, boolean requiresAuth){
        RequestSpecification requestSpecification = given();
        if (requiresAuth){
            requestSpecification = requestSpecification.header("Authorization", INVALID_TOKEN);
        }
        return   requestSpecification
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post(endpoint);
    }


    public static Response performPatchRequest(String endpoint, String paramKey, String paramValue, Object payload){
        return   given()
                .contentType(ContentType.JSON)
                .header("Authorization", ACCESS_TOKEN)
                .body(payload)
                .pathParams(paramKey, paramValue)
                .when()
                .patch(endpoint);
    }

    public static Response performDeleteRequest(String endpoint, String paramKey, String paramValue,boolean requiresAuth) {
        RequestSpecification requestSpecification = given();
        if (requiresAuth){
            requestSpecification = requestSpecification.header("Authorization", ACCESS_TOKEN);
        }
        return requestSpecification
                .pathParams(paramKey, paramValue)
                .when()
                .delete(endpoint);
    }


}
