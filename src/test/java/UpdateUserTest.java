import client.UserSteps;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.ValidatableResponse;
import model.User;
import model.UserCredentials;
import model.UserGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static java.net.HttpURLConnection.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class UpdateUserTest {
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
    @DisplayName("Update with authorization")
    public void updateWithAuthorizationTest() {
        User user = UserGenerator.getRandom();
        ValidatableResponse createResponse = userSteps.create(user);
        ValidatableResponse loginResponse = userSteps.login(UserCredentials.from(user));
        token = createResponse.extract().path("accessToken");
        ValidatableResponse updateUser = userSteps.update(UserGenerator.getRandom(), token);

        int authStatusCode = createResponse.extract().statusCode();
        int loginStatusCode = loginResponse.extract().statusCode();
        int updateStatusCode = updateUser.extract().statusCode();
        assertThat(authStatusCode, is(HTTP_OK));
        assertThat(loginStatusCode, is(HTTP_OK));
        assertThat(updateStatusCode, is(HTTP_OK));
    }

    @Test
    @DisplayName("Update without authorization")
    public void updateWithoutAuthorizationTest() {
        User user = UserGenerator.getRandom();
        ValidatableResponse createResponse = userSteps.create(user);
        token = createResponse.extract().path("accessToken");
        ValidatableResponse updateResponse = userSteps.updateUnauthorizedUser(user);

        int authStatusCode = createResponse.extract().statusCode();
        int updateStatusCode = updateResponse.extract().statusCode();
        String updateMessage = updateResponse.extract().path("message").toString();
        assertThat(authStatusCode, is(HTTP_OK));
        assertThat(updateStatusCode, is(HTTP_UNAUTHORIZED));
        assertThat(updateMessage, is("You should be authorised"));


    }
}
