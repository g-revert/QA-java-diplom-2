import client.UserSteps;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.ValidatableResponse;
import model.User;
import model.UserGenerator;
import model.Ingredients;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

import static java.net.HttpURLConnection.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class CreateOrderTest {

    private UserSteps userSteps;
    private String token;

    @BeforeClass
    public static void globalSetUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Before
    public void setUp() {
        userSteps = new UserSteps();
    }

    @After
    public void clearData() {
        userSteps.delete(token);
    }

    @Test
    @DisplayName("Create an order with authorization")
    public void createOrderWithAuthorizationTest() {
        User user = UserGenerator.getRandom();
        ValidatableResponse createResponse = userSteps.create(user);
        token = createResponse.extract().path("accessToken");
        Ingredients ingredients = new Ingredients(userSteps.getIngredientsHash());
        ValidatableResponse createOrderResponse = userSteps.createOrderWithAuthorization(ingredients, token);

        int authStatusCode = createResponse.extract().statusCode();
        int createOrderStatusCode = createOrderResponse.extract().statusCode();
        String getOrderBody = createOrderResponse.extract().path("order.ingredients._id").toString();
        assertThat(authStatusCode, is(HTTP_OK));
        assertThat(createOrderStatusCode, is(HTTP_OK));
        assertThat(getOrderBody, notNullValue());
    }

    @Test
    @DisplayName("Create an order without authorization")
    public void createOrderWithoutAuthorizationTest() {
        User user = UserGenerator.getRandom();
        ValidatableResponse createUserResponse = userSteps.create(user);
        String tokenNull = "null";
        Ingredients ingredients = new Ingredients(userSteps.getIngredientsHash());
        ValidatableResponse createOrderResponse = userSteps.createOrderWithAuthorization(ingredients, tokenNull);
        token = createUserResponse.extract().path("accessToken");

        int createOrderStatusCode = createOrderResponse.extract().statusCode();
        String getOrderBody = createOrderResponse.extract().path("success").toString();
        assertThat(createOrderStatusCode, is(HTTP_OK));
        assertThat(getOrderBody, is("true"));
    }

    @Test
    @DisplayName("Create an order with ingredients")
    public void createOrderWithIngredientsTest() {
        ArrayList<String> newIngredients = new ArrayList<>();
        newIngredients.add("61c0c5a71d1f82001bdaaa72");
        newIngredients.add("609646e4dc916e00276b2870");
        Ingredients ingredients = new Ingredients(newIngredients);
        ValidatableResponse createOrderResponse = userSteps.createOrder(ingredients);
        token = "null";

        int createOrderStatusCode = createOrderResponse.extract().statusCode();
        String getOrderMessage = createOrderResponse.extract().path("success").toString();
        int getOrderNumber = createOrderResponse.extract().path("order.number");
        assertThat(getOrderMessage, is("true"));
        assertThat(getOrderNumber, notNullValue());
        assertThat(createOrderStatusCode, is(HTTP_OK));
    }

    @Test
    @DisplayName("Create an order without ingredients")
    public void createOrderWithoutIngredientsTest() {
        User user = UserGenerator.getRandom();
        ValidatableResponse createResponse = userSteps.create(user);
        Ingredients ingredients = new Ingredients(null);
        ValidatableResponse createOrderResponse = userSteps.createOrder(ingredients);
        token = createResponse.extract().path("accessToken");

        int authStatusCode = createResponse.extract().statusCode();
        int createOrderStatusCode = createOrderResponse.extract().statusCode();
        String getOrderMessage = createOrderResponse.extract().path("message").toString();
        assertThat(authStatusCode, is(HTTP_OK));
        assertThat(createOrderStatusCode, is(HTTP_BAD_REQUEST));
        assertThat(getOrderMessage, is("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Create an order with incorrect ingredients hash")
    public void createOrderWithIncorrectIngredientsHashTest() {
        User user = UserGenerator.getRandom();
        ValidatableResponse createResponse = userSteps.create(user);
        token = createResponse.extract().path("accessToken");
        ArrayList<String> ingredientsIncorrect = new ArrayList<>();
        ingredientsIncorrect.add("tydyttytydtytytty1111111");
        ingredientsIncorrect.add("wieyviv3uv3u3443v2222222");
        Ingredients ingredients = new Ingredients(ingredientsIncorrect);
        ValidatableResponse createOrderResponse = userSteps.createOrderWithAuthorization(ingredients, token);

        int authStatusCode = createResponse.extract().statusCode();
        int createOrderStatusCode = createOrderResponse.extract().statusCode();
        assertThat(authStatusCode, is(HTTP_OK));
        assertThat(createOrderStatusCode, is(HTTP_INTERNAL_ERROR));
    }

}
