public class Item {
    public enum types {
        STRING,
        INT,
        FUNCTION
    }

    types type;
    String value;
    String name;
    String[] args;
    Item(types type, String value, String name) {
        this.type = type;
        this.value = value;
        this.name = name;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }
}
