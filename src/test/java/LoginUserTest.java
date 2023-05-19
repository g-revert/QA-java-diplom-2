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
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(Parameterized.class)
public class LoginUserTest {
    private User userTest;
    private UserCredentials userCredentialsTest;
    private int statusCode;
    private String loginMessage;

    public LoginUserTest(User userTest, UserCredentials userCredentialsTest, int statusCode, String loginMessage) {
        this.userTest = userTest;
        this.userCredentialsTest = userCredentialsTest;
        this.statusCode = statusCode;
        this.loginMessage = loginMessage;
    }

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





    @Parameterized.Parameters
    public static Object[][] getCredentials() {
        return new Object[][] {
                {new User("1<03f29jfn>mc@yandex.ru", "12345", "nameTest"), new UserCredentials("1<03f29jfn>mc@yandex.ru", "12345"), HTTP_OK, "true"},
                {new User("1<029}{jfmc@yandex.ru", "12345", "nameTest"), new UserCredentials("1<029f}{jfmc@yandex.ru", "1234f5"), HTTP_UNAUTHORIZED, "false"},
        };
    }
    @Test
    @DisplayName("Login user via existing&nonexisting account test")
    public void loginUserParamTest() {
    User user = userTest;
    ValidatableResponse createUserResponse = userSteps.create(user);
    ValidatableResponse loginUserResponse = userSteps.login(userCredentialsTest);
    token = userSteps.login(UserCredentials.from(user)).extract().path("accessToken");


    int authStatusCode = createUserResponse.extract().statusCode();
    int statusCodeExpected = loginUserResponse.extract().statusCode();
    String loginMessageExpected = loginUserResponse.extract().path("success").toString();
    assertThat(authStatusCode, is(HTTP_OK));
    assertThat(statusCodeExpected, is(statusCode));
    assertThat(loginMessageExpected, is(loginMessage));
    }

}
