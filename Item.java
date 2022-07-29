public class Item {
    public enum types {
        STRING,
        INT
    }

    types type;
    String value;
    String name;
    Item(types type, String value, String name) {
        this.type = type;
        this.value = value;
        this.name = name;
    }
}
