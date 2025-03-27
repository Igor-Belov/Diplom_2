package site.nomoreparties.stellarburgers.order;

public enum Product {
    BUN("61c0c5a71d1f82001bdaaa6d", "Флюоресцентная булка R2-D3"),
    MAIN("61c0c5a71d1f82001bdaaa6f", "Мясо бессмертных моллюсков Protostomia"),
    SAUCE("61c0c5a71d1f82001bdaaa72", "Соус Spicy-X"),
    NO_VALID_HASH_FOOD("61c0c5a71d1f82001bdaaa", "Не валидная маска хэша"),
    UNKNOWN_HASH_FOOD("61c0c5a71d1f82001bdaaaaa", "Неизвестный серверу валидный хэш");

    private final String hash;
    private final String name;

    Product(String hash, String name) {
        this.hash = hash;
        this.name = name;
    }

    public String getHash() {
        return hash;
    }
}