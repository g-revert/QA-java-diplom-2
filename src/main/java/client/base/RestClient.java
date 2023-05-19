package client.base;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class RestClient {
    protected static final String BASE_URI = "https://stellarburgers.nomoreparties.site/api/";
    protected RequestSpecification getBaseReqSpec() {// этот метод заменяет ".header("Content-type", "application/json")" во всех тестах
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri(BASE_URI)
                .build();
    }
}
