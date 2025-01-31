package manager.system;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Arrays;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Language {
    UNKNOWN(""),
    ARABIC("arabic"),
    BENGALI("bengali"),
    BRAZILIAN_PORTUGUESE("brazilian_portuguese"),
    BULGARIAN("bulgarian"),
    CATALAN("catalan"),
    CHINESE("chinese"),
    TRADITIONAL_CHINESE("traditional_chinese"),
    CZECH("czech"),
    DANISH("danish"),
    DUTCH("dutch"),
    ESTONIAN("estonian"),
    FINNISH("finnish"),
    FRENCH("french"),
    GALICIAN("galician"),
    GERMAN("german"),
    GREEK("greek"),
    HINDI("hindi"),
    INDONESIAN("indonesian"),
    ITALIAN("italian"),
    IRISH("irish"),
    JAPANESE("japanese"),
    KOREAN("korean"),
    ENGLISH("english"),
    LATVIAN("latvian"),
    NORWEGIAN("norwegian"),
    PERSIAN("persian"),
    POLISH("polish"),
    PORTUGUESE("portuguese"),
    ROMANIAN("romanian"),
    RUSSIAN("russian"),
    SCANDINAVIAN("scandinavian"),
    SERBIAN("serbian"),
    SPANISH("spanish"),
    SWEDISH("swedish"),
    THAI("thai"),
    TURKISH("turkish"),
//    UKRAINIAN("ukrainian");
    ;
    public final String name;

    Language(String name) {
        this.name = name;
    }

    public static Language get(String name){
        return Arrays.stream(values()).filter(one->one.name.equals(name)).findAny().orElse(UNKNOWN);
    }
}
