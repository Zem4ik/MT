import java.util.ArrayList;
import java.util.List;

public class Element {
    final String name;
    Attribute returnAttribute;
    List<Attribute> arguments = new ArrayList<>();

    protected Element(String name) {
        this.name = name;
        this.returnAttribute = new Attribute();
    }

    void setReturnAttr(String type, String name) {
        this.returnAttribute.type = type;
        this.returnAttribute.name = name;
    }

    void addArgument(String type, String name) {
        this.arguments.add(new Attribute(type, name));
    }

    String returnType() {
        return returnAttribute.type;
    }

    class Attribute {
        String type, name;

        private Attribute() {
            type = "void";
            name = "";
        }

        Attribute(String type, String name) {
            this.type = type;
            this.name = name;
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
