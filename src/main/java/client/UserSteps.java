package client;

import client.base.RestClient;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import model.User;
import model.UserCredentials;
import model.Ingredients;

import java.util.ArrayList;

import static io.restassured.RestAssured.given;

public class UserSteps extends RestClient {
    private static final String USER_URI = BASE_URI + "auth/";
    private static final String ORDER_URI = BASE_URI + "orders/";

    @Step("Create user {createUser}")
    public ValidatableResponse create(User user){
        return given()
                .spec(getBaseReqSpec())
                .body(user)
                .when()
                .post(USER_URI + "register/")
                .then();
    }

    @Step("Login user {loginUser}")
    public ValidatableResponse login(UserCredentials userCredentials){
        return given()
                .spec(getBaseReqSpec())
                .body(userCredentials)
                .when()
                .post(USER_URI + "login/")
                .then();
    }

    @Step("Delete user {deleteUser}")
    public ValidatableResponse delete(String token) {
       return given()
                .spec(getBaseReqSpec())
                .header("Authorization", token)
                .when()
                .delete(USER_URI + "user")
                .then();
    }

    @Step("Update user {updateUser}")
    public ValidatableResponse update(User user, String token) {
        return given()
                .spec(getBaseReqSpec())
                .header("Authorization", token)
                .body(user)
                .when()
                .patch(USER_URI + "user")
                .then();

    }

    @Step("Update updateUnauthorized user {updateUnauthorizedUser}")
    public ValidatableResponse updateUnauthorizedUser(User user) {
        return given()
                .spec(getBaseReqSpec())
                .body(user)
                .when()
                .patch(USER_URI + "user")
                .then();
    }

    @Step("Create order {createOrder}")
    public ValidatableResponse createOrder(Ingredients ingredients) {
        return given()
                .spec(getBaseReqSpec())
                .body(ingredients)
                .when()
                .post(ORDER_URI)
                .then();
    }
    @Step("Create order with authorization {createOrder}")
    public ValidatableResponse createOrderWithAuthorization (Ingredients ingredients, String token) {
        return given()
                .spec(getBaseReqSpec())
                .header("Authorization", token)
                .body(ingredients)
                .when()
                .post(ORDER_URI)
                .then();
    }
    @Step("Get ingredients {getIngredients}")
    public ValidatableResponse getIngredients() {
        return given()
                .spec(getBaseReqSpec())
                .when()
                .get(BASE_URI + "ingredients")
                .then();
    }
    @Step("Get ingredients hash {getIngredientHash}")
    public ArrayList<String> getIngredientsHash() {
        return getIngredients().extract().path("data._id");
    }

    @Step("Get orders for user {getOrderForUser}")
    public ValidatableResponse getOrdersForUser(User user, String token) {
        return given()
                .spec(getBaseReqSpec())
                .header("Authorization", token)
                .body(user)
                .when()
                .get(ORDER_URI)
                .then();
    }
    @Step("Get orders for unauthorized {getOrderForUnauthorizedUser}")
    public ValidatableResponse getOrdersForUnauthorizedUser() {
        return given()
                .spec(getBaseReqSpec())
                .when()
                .get(ORDER_URI)
                .then();
    }
}
