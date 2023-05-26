import client.UserSteps;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.ValidatableResponse;
import model.User;
import model.UserCredentials;
import model.UserGenerator;
import org.junit.*;

import static java.net.HttpURLConnection.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class CreateUserTest {
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
    @DisplayName("Create a new user")
    public void createNewUserTest() {
        User user = UserGenerator.getRandom();
        ValidatableResponse createResponse = userSteps.create(user);
        token = userSteps.login(UserCredentials.from(user)).extract().path("accessToken");

        int authStatusCode = createResponse.extract().statusCode();
        String authSuccessMessage = createResponse.extract().path("success").toString();
        assertThat(authStatusCode, is(HTTP_OK));
        assertThat(authSuccessMessage, is("true"));
    }

    @Test
    @DisplayName("Create the registered user")
    public void createTheRegisteredUserTest() {
        User user = UserGenerator.getRandom();
        ValidatableResponse createResponse = userSteps.create(user);
        ValidatableResponse createResponse1 = userSteps.create(user);
        token = userSteps.login(UserCredentials.from(user)).extract().path("accessToken");

        int authStatusCode = createResponse.extract().statusCode();
        int authStatusCode1 = createResponse1.extract().statusCode();
        String authMessage = createResponse1.extract().path("message").toString();
        assertThat(authStatusCode, is(HTTP_OK));
        assertThat(authStatusCode1, is(HTTP_FORBIDDEN));
        assertThat(authMessage, is("User already exists"));
    }

    @Test
    @DisplayName("Create a user with missing email string")
    public void createMissingEmailTest() {
        User user = new User(null, "wefwefwewef", "TestNam");
        ValidatableResponse createResponse = userSteps.createWithoutHeader(user);
        token = "null";

        int authStatusCode = createResponse.extract().statusCode();
        String authMessage = createResponse.extract().path("message").toString();
        assertThat(authStatusCode, is(HTTP_FORBIDDEN));
        assertThat(authMessage, is("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Create a user with missing password string")
    public void createMissingPasswordTest() {
        User user = new User("ibeciwec@yandex.ru", null, "TestNam");
        ValidatableResponse createResponse = userSteps.createWithoutHeader(user);
        token = "null";

        int authStatusCode = createResponse.extract().statusCode();
        String authMessage = createResponse.extract().path("message").toString();
        assertThat(authStatusCode, is(HTTP_FORBIDDEN));
        assertThat(authMessage, is("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Create a user with missing name string")
    public void createMissingNameTest() {
        User user = new User("ibeciwec@yandex.ru", "everververesvd", null);
        ValidatableResponse createResponse = userSteps.createWithoutHeader(user);
        token = "null";

        int authStatusCode = createResponse.extract().statusCode();
        String authMessage = createResponse.extract().path("message").toString();
        assertThat(authStatusCode, is(HTTP_FORBIDDEN));
        assertThat(authMessage, is("Email, password and name are required fields"));
    }
}
