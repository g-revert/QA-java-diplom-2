import client.UserSteps;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.ValidatableResponse;
import model.Ingredients;
import model.User;
import model.UserGenerator;
import model.pojo.Orders;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class GetOrderTest {
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
    @DisplayName("Get order for authorized user")
    public void createOrderForAuthorizedUserTest() {
        User user = UserGenerator.getRandom();
        ValidatableResponse createResponse = userSteps.create(user);
        Orders orders;
        token = createResponse.extract().path("accessToken");
        ArrayList<String> ingredientsUser = new ArrayList<>();
        ingredientsUser.add("61c0c5a71d1f82001bdaaa6d");
        Ingredients ingredients = new Ingredients(ingredientsUser);
        ValidatableResponse createOrderResponse = userSteps.createOrderWithAuthorization(ingredients,token);
        ValidatableResponse getOrdersResponse = userSteps.getOrdersForUser(user, token);

        int authStatusCode = createResponse.extract().statusCode();
        int createOrderStatusCode = createOrderResponse.extract().statusCode();
        int getOrderStatusCode = getOrdersResponse.extract().statusCode();
        orders = getOrdersResponse.extract().as(Orders.class);
        assertThat(authStatusCode, is(HTTP_OK));
        assertThat(createOrderStatusCode, is(HTTP_OK));
        assertThat(getOrderStatusCode, is(HTTP_OK));
        assertThat(orders, notNullValue());
    }

    @Test
    @DisplayName("Get order for unauthorized user")
    public void createOrderForUnauthorizedUserTest() {
        token = "null";
        ArrayList<String> ingredientsUser = new ArrayList<>();
        ingredientsUser.add("61c0c5a71d1f82001bdaaa6d");
        Ingredients ingredients = new Ingredients(ingredientsUser);
        ValidatableResponse createOrderResponse = userSteps.createOrderWithAuthorization(ingredients,token);
        ValidatableResponse getOrdersResponse = userSteps.getOrdersForUnauthorizedUser();

        int createOrderStatusCode = createOrderResponse.extract().statusCode();
        int getOrderStatusCode = getOrdersResponse.extract().statusCode();
        String getOrderMessage = getOrdersResponse.extract().path("message");
        assertThat(createOrderStatusCode, is(HTTP_OK));
        assertThat(getOrderStatusCode, is(HTTP_UNAUTHORIZED));
        assertThat(getOrderMessage, is("You should be authorised"));
    }


}
