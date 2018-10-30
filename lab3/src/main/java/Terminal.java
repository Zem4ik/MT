import java.util.ArrayList;
import java.util.List;

public class Terminal extends Element {
    List<String> strings = new ArrayList<>();
    List<String> regex = new ArrayList<>();

    Terminal(String name) {
        super(name);
    }

    void addString(String string) {
        strings.add(string);
    }

    void addStrings(List<String> strings) {
        this.strings.addAll(strings);
    }

    void addRegex(String string) {
        regex.add(string);
    }

    void addAllRegex(List<String> strings) {
        regex.addAll(strings);
    }
}
