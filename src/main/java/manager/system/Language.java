package manager.system;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Language {
    CH("ch"),
    EN("en"),
    JA("ja"),
    ;
    public final String name;

    Language(String name) {
        this.name = name;
    }
}
