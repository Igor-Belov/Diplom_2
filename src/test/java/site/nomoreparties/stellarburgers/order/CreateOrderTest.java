//Создание заказа:
//с авторизацией,
//без авторизации,
//с ингредиентами,
//без ингредиентов,
//с неверным хешем ингредиентов.

package site.nomoreparties.stellarburgers.order;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import site.nomoreparties.stellarburgers.user.User;
import site.nomoreparties.stellarburgers.user.UserClient;
import site.nomoreparties.stellarburgers.user.UserCredentials;

import java.util.List;

import static java.net.HttpURLConnection.HTTP_OK;

@DisplayName("Тесты создания заказа")
public class CreateOrderTest {
    private List<String> ingredients;
    private UserClient userClient;
    private User user;
    private UserCredentials userCredentials;
    private ValidatableResponse responseCreateUser;
    private ValidatableResponse logIn;
    private OrderClient orderClient;
    private OrderChecks check;
    private Order order;
    private String accessToken;

    @Before
    public void set() {
        user = User.createRandomUser();
        userCredentials = UserCredentials.extractUserCredentials(user);
        userClient = new UserClient();
        orderClient = new OrderClient();
        responseCreateUser = userClient.createUser(user);
        Assume.assumeTrue(responseCreateUser.extract().statusCode() == HTTP_OK); //200
        logIn = userClient.logIn(userCredentials);
        Assume.assumeTrue(logIn.extract().statusCode() == HTTP_OK); //200
        accessToken = logIn.extract().path("accessToken").toString().replace("Bearer ", "");
        check = new OrderChecks();
    }

    @After
    public void cleanUp() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Тест - заказ может быть создан;")
    public void authorizationUserCreateOrderHttpOk() {
        ingredients = List.of(Product.BUN.getHash(), Product.MAIN.getHash(), Product.SAUCE.getHash());
        order = Order.createOrder(ingredients, accessToken);
        ValidatableResponse createOrderResponse  = orderClient.CreateOrderResponse(order);
        check.checkCreateOrderHttpOk(createOrderResponse);
    }

    @Test
    @DisplayName("Тест - ошибка при невалидном хэше продукта;")
    public void authorizationUserCreateOrderNoValidHashHttpError() {
        ingredients = List.of(Product.BUN.getHash(), Product.MAIN.getHash(), Product.NO_VALID_HASH_FOOD.getHash());
        order = Order.createOrder(ingredients, accessToken);
        ValidatableResponse createOrderResponse  = orderClient.CreateOrderResponse(order);
        check.checkCreateOrderBadHashHttpError(createOrderResponse);
    }

    //Не описана реакция системы на неизвестный хэш. По факту 200 и игнорирование неизвестного продукта. Это явно баг. Ожидаю 500 или отдельную обработку (Например 400 и "One or more ids provided are incorrect")
    @Test
    @DisplayName("Тест - ошибка при неизвестном хэше продукта;")
    public void authorizationUserCreateOrderUnknownHashHttpError() {
        ingredients = List.of(Product.BUN.getHash(), Product.MAIN.getHash(), Product.UNKNOWN_HASH_FOOD.getHash());
        order = Order.createOrder(ingredients, accessToken);
        ValidatableResponse createOrderResponse  = orderClient.CreateOrderResponse(order);
        check.checkCreateOrderBadHashHttpError(createOrderResponse);
    }

    //не уверен, что это не баг. Бургер только из хлеба.
    @Test
    @DisplayName("Тест - бургер только из хлеба;")
    public void authorizationUserCreateOrderOnlyBreadHttpOk() {
        ingredients = List.of(Product.BUN.getHash());
        order = Order.createOrder(ingredients, accessToken);
        ValidatableResponse createOrderResponse  = orderClient.CreateOrderResponse(order);
        check.checkCreateOrderHttpOk(createOrderResponse);
    }

    //не уверен, что это не баг. Бургер только из соуса.
    @Test
    @DisplayName("Тест - бургер только из соуса;")
    public void authorizationUserCreateOrderOnlySauceHttpOk() {
        ingredients = List.of(Product.SAUCE.getHash());
        order = Order.createOrder(ingredients, accessToken);
        ValidatableResponse createOrderResponse  = orderClient.CreateOrderResponse(order);
        check.checkCreateOrderHttpOk(createOrderResponse);
    }

    //Не уверен, что это не баг. Бургер только из мяса.
    @Test
    @DisplayName("Тест - бургер только из мяса;")
    public void authorizationUserCreateOrderOnlyMainHttpOk() {
        ingredients = List.of(Product.MAIN.getHash());
        order = Order.createOrder(ingredients, accessToken);
        ValidatableResponse createOrderResponse  = orderClient.CreateOrderResponse(order);
        check.checkCreateOrderHttpOk(createOrderResponse);
    }

    @Test
    @DisplayName("Тест - бургер только из мяса;")
    public void authorizationUserCreateOrderEmptyHttpError() {
        ingredients = List.of();
        order = Order.createOrder(ingredients, accessToken);
        ValidatableResponse createOrderResponse  = orderClient.CreateOrderResponse(order);
        check.checkCreateOrderEmptyListIngridientsHttpBad(createOrderResponse);
    }

    //Интересная реакция. Ожидал ответ как у authorizationUserCreateOrderUnknownHashHttpError, так как authorizationUserCreateOrderUnknownHashHttpError просто игнорит такие продукты. Очевидно что где то баг.
    //По факту 400 "One or more ids provided are incorrect"
    @Test
    @DisplayName("Тест - бургер только из неизвестного ингридиента;")
    public void authorizationUserCreateOrderOnlyUnknownHashHttpError() {
        ingredients = List.of(Product.UNKNOWN_HASH_FOOD.getHash());
        order = Order.createOrder(ingredients, accessToken);
        ValidatableResponse createOrderResponse  = orderClient.CreateOrderResponse(order);
        check.checkCreateOrderEmptyListIngridientsHttpBad(createOrderResponse);
    }

    //По факту 400 "One or more ids provided are incorrect". Возможно баг
    @Test
    @DisplayName("Тест - ошибка при невалидном хэше продукта. Других продуктов нет;")
    public void authorizationUserCreateOrderOnlyOneNoValidHashHttpError() {
        ingredients = List.of(Product.NO_VALID_HASH_FOOD.getHash());
        order = Order.createOrder(ingredients, accessToken);
        ValidatableResponse createOrderResponse  = orderClient.CreateOrderResponse(order);
        check.checkCreateOrderBadHashHttpError(createOrderResponse);
    }

    //баг
    @Test
    @DisplayName("Тест - Пользователь не авторизован (нет токена). Заказ не может быть создан;")
    public void noAuthorizationUserCreateOrderHttpNoAuth() {
        ingredients = List.of(Product.BUN.getHash(), Product.MAIN.getHash(), Product.SAUCE.getHash());
        order = Order.createOrder(ingredients, new String());
        ValidatableResponse createOrderResponse  = orderClient.CreateOrderResponse(order);
        check.checkCreateOrderNoAuthHttp(createOrderResponse);
    }

    @Test
    @DisplayName("Тест - Пользователь не авторизован (просрочен токен). Заказ не может быть создан;")
    public void oldTokenAuthorizationUserCreateOrderHttpNoAuth() {
        ingredients = List.of(Product.BUN.getHash(), Product.MAIN.getHash(), Product.SAUCE.getHash());
        String oldToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjY3ZGM5MGVkOWVkMjgwMDAxYjVhYjFlYSIsImlhdCI6MTc0MjUwODI2OSwiZXhwIjoxNzQyNTA5NDY5fQ.cVVY7PA41YZ-zwt1lTK1AN4sxBBo8dtGR64bl_Y58w8";
        order = Order.createOrder(ingredients, oldToken);
        ValidatableResponse createOrderResponse  = orderClient.CreateOrderResponse(order);
        check.checkCreateOrderNoAuthHttp(createOrderResponse);
    }

    //Eще тесты на дубли ингридиентов и так далее. Но не в рамках учебного проекта)))
}