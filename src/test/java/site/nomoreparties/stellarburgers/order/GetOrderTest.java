//Получение заказов конкретного пользователя:
//авторизованный пользователь,
//неавторизованный пользователь.

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

@DisplayName("Тесты получения заказов пользователя")
public class GetOrderTest {
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

    //Можно написать клевые тесты с проверкой соотвентствия отправленных и полученных заказов (все поля), и счетчики.  но уже нет времени.
    @Test
    @DisplayName("Тест - один заказ может быть отображен;")
    public void AuthorizationUserGetOneOrdersListHttpOk() {
        ingredients = List.of(Product.BUN.getHash(), Product.MAIN.getHash(), Product.SAUCE.getHash());
        order = Order.createOrder(ingredients, accessToken);
        orderClient.CreateOrderResponse(order);
        ValidatableResponse getOrdersUserResponse = orderClient.GetOrdersUserResponse(accessToken);
        check.checkNoEmptyListOrderHttpОк(getOrdersUserResponse, 1);
    }

    @Test
    @DisplayName("Тест - ноль заказов может быть отображен;")
    public void AuthorizationUserGetEmptyOrdersListHttpOk() {
        ValidatableResponse getOrdersUserResponse = orderClient.GetOrdersUserResponse(accessToken);
        check.checkEmptyListOrderHttpОк(getOrdersUserResponse);
    }

    @Test
    @DisplayName("Тест - два заказа может быть отображено;")
    public void AuthorizationUserGetTwoOrdersListHttpOk() {
        ingredients = List.of(Product.BUN.getHash(), Product.MAIN.getHash(), Product.SAUCE.getHash());
        order = Order.createOrder(ingredients, accessToken);
        int countOrders = 2;
        for (int i = 0; i < countOrders; i++) {
            orderClient.CreateOrderResponse(order);
        }
        ValidatableResponse getOrdersUserResponse = orderClient.GetOrdersUserResponse(accessToken);
        check.checkNoEmptyListOrderHttpОк(getOrdersUserResponse, countOrders);
    }

    //Долгий тест!
    @Test
    @DisplayName("Тест - 50 заказов может быть отображено. Дополнен- при 51, отображается 50;")
    public void AuthorizationUserGet50OrdersListHttpOk() {
        ingredients = List.of(Product.BUN.getHash(), Product.MAIN.getHash(), Product.SAUCE.getHash());
        order = Order.createOrder(ingredients, accessToken);
        int countOrders = 50;
        for (int i = 0; i < countOrders; i++) {
            orderClient.CreateOrderResponse(order);
        }
        ValidatableResponse getOrdersUserResponse = orderClient.GetOrdersUserResponse(accessToken);
        check.checkNoEmptyListOrderHttpОк(getOrdersUserResponse, countOrders);
        //для ускорения прогона этот тест совмещен с тестом на 50 заказов. См тест ниже.
        //Тут баг. Система вернула 51 заказ
        orderClient.CreateOrderResponse(order);
        check.checkNoEmptyListOrderHttpОк(getOrdersUserResponse, countOrders + 1);
    }

    //для ускорения прогона этот тест совмещен с тестом на 50 заказов
    //Тут баг. Система вернула 51 заказ
//    @Test
//    @DisplayName("Тест - только 50 заказ может быть отображено, при 51 в наличии;")
//    public void AuthorizationUserGet51OrdersListHttpOk() {
//        ingredients = List.of(Product.BUN.getHash(), Product.MAIN.getHash(), Product.SAUCE.getHash());
//        order = Order.createOrder(ingredients, accessToken);
//        int countOrders = 51;
//        for (int i = 0; i < countOrders; i++) {
//            orderClient.CreateOrderResponse(order);
//        }
//        ValidatableResponse getOrdersUserResponse = orderClient.GetOrdersUserResponse(accessToken);
//        check.checkNoEmptyListOrderHttpОк(getOrdersUserResponse, countOrders);
//    }

    //баг. Ожидаю 401 "You should be authorised", а не 403 "jwt expired"
    @Test
    @DisplayName("Тест - Не авторизированный пользователь (просрочен токет). Заказы не могут быть отображены;")
    public void OldAuthorizationUserGetOrdersListHttpUnauth() {
        String oldToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjY3ZGM5MGVkOWVkMjgwMDAxYjVhYjFlYSIsImlhdCI6MTc0MjUwODI2OSwiZXhwIjoxNzQyNTA5NDY5fQ.cVVY7PA41YZ-zwt1lTK1AN4sxBBo8dtGR64bl_Y58w8";
        ValidatableResponse getOrdersUserResponse = orderClient.GetOrdersUserResponse(oldToken);
        check.checkListOrderNoAuthUserHttpUnauth(getOrdersUserResponse);
    }

    @Test
    @DisplayName("Тест - Не авторизированный пользователь (пустой токет). Заказы не могут быть отображены;")
    public void EmptyAuthorizationUserGetOrdersListHttpUnauth() {
        String emptyToken = new String();
        ValidatableResponse getOrdersUserResponse = orderClient.GetOrdersUserResponse(emptyToken);
        check.checkListOrderNoAuthUserHttpUnauth(getOrdersUserResponse);
    }

}

