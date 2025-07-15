package manager.solr.books;

import manager.entity.SMSolrDoc;
import org.apache.solr.client.solrj.beans.Field;

import java.util.List;

public class SharingLink extends SMSolrDoc {

    private Float score;


    /*
     * 1.SinglePage 2.FullBook 3.PageNode
     */
    @Field
    private String type;

    /*
     * userId + bookId 定位到一个book
     */
    @Field
    private Long userId;

    @Field
    private String bookId;

    @Field
    private String perms;

    @Field
    private String settings;

    @Field
    private String extra;

    @Field
    private Integer status;

    @Field
    private String defaultLang;

    @Field
    private List<String> tags;

    @Field private String desc_editorState_arabic;
    @Field private String desc_editorState_bengali;
    @Field private String desc_editorState_brazilian_portuguese;
    @Field private String desc_editorState_bulgarian;
    @Field private String desc_editorState_catalan;
    @Field private String desc_editorState_chinese;
    @Field private String desc_editorState_traditional_chinese;
    @Field private String desc_editorState_czech;
    @Field private String desc_editorState_danish;
    @Field private String desc_editorState_dutch;
    @Field private String desc_editorState_estonian;
    @Field private String desc_editorState_finnish;
    @Field private String desc_editorState_french;
    @Field private String desc_editorState_galician;
    @Field private String desc_editorState_german;
    @Field private String desc_editorState_greek;
    @Field private String desc_editorState_hindi;
    @Field private String desc_editorState_indonesian;
    @Field private String desc_editorState_italian;
    @Field private String desc_editorState_irish;
    @Field private String desc_editorState_japanese;
    @Field private String desc_editorState_korean;
    @Field private String desc_editorState_english;
    @Field private String desc_editorState_latvian;
    @Field private String desc_editorState_norwegian;
    @Field private String desc_editorState_persian;
    @Field private String desc_editorState_polish;
    @Field private String desc_editorState_portuguese;
    @Field private String desc_editorState_romanian;
    @Field private String desc_editorState_russian;
    @Field private String desc_editorState_scandinavian;
    @Field private String desc_editorState_serbian;
    @Field private String desc_editorState_spanish;
    @Field private String desc_editorState_swedish;
    @Field private String desc_editorState_thai;
    @Field private String desc_editorState_turkish;
    @Field private String desc_editorState_ukrainian;

    @Field private String name_arabic;
    @Field private String desc_arabic;

    @Field private String name_bengali;
    @Field private String desc_bengali;

    @Field private String name_brazilian_portuguese;
    @Field private String desc_brazilian_portuguese;

    @Field private String name_bulgarian;
    @Field private String desc_bulgarian;

    @Field private String name_catalan;
    @Field private String desc_catalan;

    @Field private String name_chinese;
    @Field private String desc_chinese;

    @Field private String name_traditional_chinese;
    @Field private String desc_traditional_chinese;

    @Field private String name_czech;
    @Field private String desc_czech;

    @Field private String name_danish;
    @Field private String desc_danish;

    @Field private String name_dutch;
    @Field private String desc_dutch;

    @Field private String name_estonian;
    @Field private String desc_estonian;

    @Field private String name_finnish;
    @Field private String desc_finnish;

    @Field private String name_french;
    @Field private String desc_french;

    @Field private String name_galician;
    @Field private String desc_galician;

    @Field private String name_german;
    @Field private String desc_german;

    @Field private String name_greek;
    @Field private String desc_greek;

    @Field private String name_hindi;
    @Field private String desc_hindi;

    @Field private String name_indonesian;
    @Field private String desc_indonesian;

    @Field private String name_italian;
    @Field private String desc_italian;

    @Field private String name_irish;
    @Field private String desc_irish;

    @Field private String name_japanese;
    @Field private String desc_japanese;

    @Field private String name_korean;
    @Field private String desc_korean;

    @Field private String name_english;
    @Field private String desc_english;

    @Field private String name_latvian;
    @Field private String desc_latvian;

    @Field private String name_norwegian;
    @Field private String desc_norwegian;

    @Field private String name_persian;
    @Field private String desc_persian;

    @Field private String name_polish;
    @Field private String desc_polish;

    @Field private String name_portuguese;
    @Field private String desc_portuguese;

    @Field private String name_romanian;
    @Field private String desc_romanian;

    @Field private String name_russian;
    @Field private String desc_russian;

    @Field private String name_scandinavian;
    @Field private String desc_scandinavian;

    @Field private String name_serbian;
    @Field private String desc_serbian;

    @Field private String name_spanish;
    @Field private String desc_spanish;

    @Field private String name_swedish;
    @Field private String desc_swedish;

    @Field private String name_thai;
    @Field private String desc_thai;

    @Field private String name_turkish;
    @Field private String desc_turkish;

//    @Field private String name_ukrainian;
//    @Field private String desc_ukrainian;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getDesc_editorState_arabic() {
        return desc_editorState_arabic;
    }

    public void setDesc_editorState_arabic(String desc_editorState_arabic) {
        this.desc_editorState_arabic = desc_editorState_arabic;
    }

    public String getDesc_editorState_bengali() {
        return desc_editorState_bengali;
    }

    public void setDesc_editorState_bengali(String desc_editorState_bengali) {
        this.desc_editorState_bengali = desc_editorState_bengali;
    }

    public String getDesc_editorState_brazilian_portuguese() {
        return desc_editorState_brazilian_portuguese;
    }

    public void setDesc_editorState_brazilian_portuguese(String desc_editorState_brazilian_portuguese) {
        this.desc_editorState_brazilian_portuguese = desc_editorState_brazilian_portuguese;
    }

    public String getDesc_editorState_bulgarian() {
        return desc_editorState_bulgarian;
    }

    public void setDesc_editorState_bulgarian(String desc_editorState_bulgarian) {
        this.desc_editorState_bulgarian = desc_editorState_bulgarian;
    }

    public String getDesc_editorState_catalan() {
        return desc_editorState_catalan;
    }

    public void setDesc_editorState_catalan(String desc_editorState_catalan) {
        this.desc_editorState_catalan = desc_editorState_catalan;
    }

    public String getDesc_editorState_chinese() {
        return desc_editorState_chinese;
    }

    public void setDesc_editorState_chinese(String desc_editorState_chinese) {
        this.desc_editorState_chinese = desc_editorState_chinese;
    }

    public String getDesc_editorState_traditional_chinese() {
        return desc_editorState_traditional_chinese;
    }

    public void setDesc_editorState_traditional_chinese(String desc_editorState_traditional_chinese) {
        this.desc_editorState_traditional_chinese = desc_editorState_traditional_chinese;
    }

    public String getDesc_editorState_czech() {
        return desc_editorState_czech;
    }

    public void setDesc_editorState_czech(String desc_editorState_czech) {
        this.desc_editorState_czech = desc_editorState_czech;
    }

    public String getDesc_editorState_danish() {
        return desc_editorState_danish;
    }

    public void setDesc_editorState_danish(String desc_editorState_danish) {
        this.desc_editorState_danish = desc_editorState_danish;
    }

    public String getDesc_editorState_dutch() {
        return desc_editorState_dutch;
    }

    public void setDesc_editorState_dutch(String desc_editorState_dutch) {
        this.desc_editorState_dutch = desc_editorState_dutch;
    }

    public String getDesc_editorState_estonian() {
        return desc_editorState_estonian;
    }

    public void setDesc_editorState_estonian(String desc_editorState_estonian) {
        this.desc_editorState_estonian = desc_editorState_estonian;
    }

    public String getDesc_editorState_finnish() {
        return desc_editorState_finnish;
    }

    public void setDesc_editorState_finnish(String desc_editorState_finnish) {
        this.desc_editorState_finnish = desc_editorState_finnish;
    }

    public String getDesc_editorState_french() {
        return desc_editorState_french;
    }

    public void setDesc_editorState_french(String desc_editorState_french) {
        this.desc_editorState_french = desc_editorState_french;
    }

    public String getDesc_editorState_galician() {
        return desc_editorState_galician;
    }

    public void setDesc_editorState_galician(String desc_editorState_galician) {
        this.desc_editorState_galician = desc_editorState_galician;
    }

    public String getDesc_editorState_german() {
        return desc_editorState_german;
    }

    public void setDesc_editorState_german(String desc_editorState_german) {
        this.desc_editorState_german = desc_editorState_german;
    }

    public String getDesc_editorState_greek() {
        return desc_editorState_greek;
    }

    public void setDesc_editorState_greek(String desc_editorState_greek) {
        this.desc_editorState_greek = desc_editorState_greek;
    }

    public String getDesc_editorState_hindi() {
        return desc_editorState_hindi;
    }

    public void setDesc_editorState_hindi(String desc_editorState_hindi) {
        this.desc_editorState_hindi = desc_editorState_hindi;
    }

    public String getDesc_editorState_indonesian() {
        return desc_editorState_indonesian;
    }

    public void setDesc_editorState_indonesian(String desc_editorState_indonesian) {
        this.desc_editorState_indonesian = desc_editorState_indonesian;
    }

    public String getDesc_editorState_italian() {
        return desc_editorState_italian;
    }

    public void setDesc_editorState_italian(String desc_editorState_italian) {
        this.desc_editorState_italian = desc_editorState_italian;
    }

    public String getDesc_editorState_irish() {
        return desc_editorState_irish;
    }

    public void setDesc_editorState_irish(String desc_editorState_irish) {
        this.desc_editorState_irish = desc_editorState_irish;
    }

    public String getDesc_editorState_japanese() {
        return desc_editorState_japanese;
    }

    public void setDesc_editorState_japanese(String desc_editorState_japanese) {
        this.desc_editorState_japanese = desc_editorState_japanese;
    }

    public String getDesc_editorState_korean() {
        return desc_editorState_korean;
    }

    public void setDesc_editorState_korean(String desc_editorState_korean) {
        this.desc_editorState_korean = desc_editorState_korean;
    }

    public String getDesc_editorState_english() {
        return desc_editorState_english;
    }

    public void setDesc_editorState_english(String desc_editorState_english) {
        this.desc_editorState_english = desc_editorState_english;
    }

    public String getDesc_editorState_latvian() {
        return desc_editorState_latvian;
    }

    public void setDesc_editorState_latvian(String desc_editorState_latvian) {
        this.desc_editorState_latvian = desc_editorState_latvian;
    }

    public String getDesc_editorState_norwegian() {
        return desc_editorState_norwegian;
    }

    public void setDesc_editorState_norwegian(String desc_editorState_norwegian) {
        this.desc_editorState_norwegian = desc_editorState_norwegian;
    }

    public String getDesc_editorState_persian() {
        return desc_editorState_persian;
    }

    public void setDesc_editorState_persian(String desc_editorState_persian) {
        this.desc_editorState_persian = desc_editorState_persian;
    }

    public String getDesc_editorState_polish() {
        return desc_editorState_polish;
    }

    public void setDesc_editorState_polish(String desc_editorState_polish) {
        this.desc_editorState_polish = desc_editorState_polish;
    }

    public String getDesc_editorState_portuguese() {
        return desc_editorState_portuguese;
    }

    public void setDesc_editorState_portuguese(String desc_editorState_portuguese) {
        this.desc_editorState_portuguese = desc_editorState_portuguese;
    }

    public String getDesc_editorState_romanian() {
        return desc_editorState_romanian;
    }

    public void setDesc_editorState_romanian(String desc_editorState_romanian) {
        this.desc_editorState_romanian = desc_editorState_romanian;
    }

    public String getDesc_editorState_russian() {
        return desc_editorState_russian;
    }

    public void setDesc_editorState_russian(String desc_editorState_russian) {
        this.desc_editorState_russian = desc_editorState_russian;
    }

    public String getDesc_editorState_scandinavian() {
        return desc_editorState_scandinavian;
    }

    public void setDesc_editorState_scandinavian(String desc_editorState_scandinavian) {
        this.desc_editorState_scandinavian = desc_editorState_scandinavian;
    }

    public String getDesc_editorState_serbian() {
        return desc_editorState_serbian;
    }

    public void setDesc_editorState_serbian(String desc_editorState_serbian) {
        this.desc_editorState_serbian = desc_editorState_serbian;
    }

    public String getDesc_editorState_spanish() {
        return desc_editorState_spanish;
    }

    public void setDesc_editorState_spanish(String desc_editorState_spanish) {
        this.desc_editorState_spanish = desc_editorState_spanish;
    }

    public String getDesc_editorState_swedish() {
        return desc_editorState_swedish;
    }

    public void setDesc_editorState_swedish(String desc_editorState_swedish) {
        this.desc_editorState_swedish = desc_editorState_swedish;
    }

    public String getDesc_editorState_thai() {
        return desc_editorState_thai;
    }

    public void setDesc_editorState_thai(String desc_editorState_thai) {
        this.desc_editorState_thai = desc_editorState_thai;
    }

    public String getDesc_editorState_turkish() {
        return desc_editorState_turkish;
    }

    public void setDesc_editorState_turkish(String desc_editorState_turkish) {
        this.desc_editorState_turkish = desc_editorState_turkish;
    }

    public String getDesc_editorState_ukrainian() {
        return desc_editorState_ukrainian;
    }

    public void setDesc_editorState_ukrainian(String desc_editorState_ukrainian) {
        this.desc_editorState_ukrainian = desc_editorState_ukrainian;
    }

    public String getName_arabic() {
        return name_arabic;
    }

    public void setName_arabic(String name_arabic) {
        this.name_arabic = name_arabic;
    }

    public String getDesc_arabic() {
        return desc_arabic;
    }

    public void setDesc_arabic(String desc_arabic) {
        this.desc_arabic = desc_arabic;
    }

    public String getName_bengali() {
        return name_bengali;
    }

    public void setName_bengali(String name_bengali) {
        this.name_bengali = name_bengali;
    }

    public String getDesc_bengali() {
        return desc_bengali;
    }

    public void setDesc_bengali(String desc_bengali) {
        this.desc_bengali = desc_bengali;
    }

    public String getName_brazilian_portuguese() {
        return name_brazilian_portuguese;
    }

    public void setName_brazilian_portuguese(String name_brazilian_portuguese) {
        this.name_brazilian_portuguese = name_brazilian_portuguese;
    }

    public String getDesc_brazilian_portuguese() {
        return desc_brazilian_portuguese;
    }

    public void setDesc_brazilian_portuguese(String desc_brazilian_portuguese) {
        this.desc_brazilian_portuguese = desc_brazilian_portuguese;
    }

    public String getName_bulgarian() {
        return name_bulgarian;
    }

    public void setName_bulgarian(String name_bulgarian) {
        this.name_bulgarian = name_bulgarian;
    }

    public String getDesc_bulgarian() {
        return desc_bulgarian;
    }

    public void setDesc_bulgarian(String desc_bulgarian) {
        this.desc_bulgarian = desc_bulgarian;
    }

    public String getName_catalan() {
        return name_catalan;
    }

    public void setName_catalan(String name_catalan) {
        this.name_catalan = name_catalan;
    }

    public String getDesc_catalan() {
        return desc_catalan;
    }

    public void setDesc_catalan(String desc_catalan) {
        this.desc_catalan = desc_catalan;
    }

    public String getName_chinese() {
        return name_chinese;
    }

    public void setName_chinese(String name_chinese) {
        this.name_chinese = name_chinese;
    }

    public String getDesc_chinese() {
        return desc_chinese;
    }

    public void setDesc_chinese(String desc_chinese) {
        this.desc_chinese = desc_chinese;
    }

    public String getName_traditional_chinese() {
        return name_traditional_chinese;
    }

    public void setName_traditional_chinese(String name_traditional_chinese) {
        this.name_traditional_chinese = name_traditional_chinese;
    }

    public String getDesc_traditional_chinese() {
        return desc_traditional_chinese;
    }

    public void setDesc_traditional_chinese(String desc_traditional_chinese) {
        this.desc_traditional_chinese = desc_traditional_chinese;
    }

    public String getName_czech() {
        return name_czech;
    }

    public void setName_czech(String name_czech) {
        this.name_czech = name_czech;
    }

    public String getDesc_czech() {
        return desc_czech;
    }

    public void setDesc_czech(String desc_czech) {
        this.desc_czech = desc_czech;
    }

    public String getName_danish() {
        return name_danish;
    }

    public void setName_danish(String name_danish) {
        this.name_danish = name_danish;
    }

    public String getDesc_danish() {
        return desc_danish;
    }

    public void setDesc_danish(String desc_danish) {
        this.desc_danish = desc_danish;
    }

    public String getName_dutch() {
        return name_dutch;
    }

    public void setName_dutch(String name_dutch) {
        this.name_dutch = name_dutch;
    }

    public String getDesc_dutch() {
        return desc_dutch;
    }

    public void setDesc_dutch(String desc_dutch) {
        this.desc_dutch = desc_dutch;
    }

    public String getName_estonian() {
        return name_estonian;
    }

    public void setName_estonian(String name_estonian) {
        this.name_estonian = name_estonian;
    }

    public String getDesc_estonian() {
        return desc_estonian;
    }

    public void setDesc_estonian(String desc_estonian) {
        this.desc_estonian = desc_estonian;
    }

    public String getName_finnish() {
        return name_finnish;
    }

    public void setName_finnish(String name_finnish) {
        this.name_finnish = name_finnish;
    }

    public String getDesc_finnish() {
        return desc_finnish;
    }

    public void setDesc_finnish(String desc_finnish) {
        this.desc_finnish = desc_finnish;
    }

    public String getName_french() {
        return name_french;
    }

    public void setName_french(String name_french) {
        this.name_french = name_french;
    }

    public String getDesc_french() {
        return desc_french;
    }

    public void setDesc_french(String desc_french) {
        this.desc_french = desc_french;
    }

    public String getName_galician() {
        return name_galician;
    }

    public void setName_galician(String name_galician) {
        this.name_galician = name_galician;
    }

    public String getDesc_galician() {
        return desc_galician;
    }

    public void setDesc_galician(String desc_galician) {
        this.desc_galician = desc_galician;
    }

    public String getName_german() {
        return name_german;
    }

    public void setName_german(String name_german) {
        this.name_german = name_german;
    }

    public String getDesc_german() {
        return desc_german;
    }

    public void setDesc_german(String desc_german) {
        this.desc_german = desc_german;
    }

    public String getName_greek() {
        return name_greek;
    }

    public void setName_greek(String name_greek) {
        this.name_greek = name_greek;
    }

    public String getDesc_greek() {
        return desc_greek;
    }

    public void setDesc_greek(String desc_greek) {
        this.desc_greek = desc_greek;
    }

    public String getName_hindi() {
        return name_hindi;
    }

    public void setName_hindi(String name_hindi) {
        this.name_hindi = name_hindi;
    }

    public String getDesc_hindi() {
        return desc_hindi;
    }

    public void setDesc_hindi(String desc_hindi) {
        this.desc_hindi = desc_hindi;
    }

    public String getName_indonesian() {
        return name_indonesian;
    }

    public void setName_indonesian(String name_indonesian) {
        this.name_indonesian = name_indonesian;
    }

    public String getDesc_indonesian() {
        return desc_indonesian;
    }

    public void setDesc_indonesian(String desc_indonesian) {
        this.desc_indonesian = desc_indonesian;
    }

    public String getName_italian() {
        return name_italian;
    }

    public void setName_italian(String name_italian) {
        this.name_italian = name_italian;
    }

    public String getDesc_italian() {
        return desc_italian;
    }

    public void setDesc_italian(String desc_italian) {
        this.desc_italian = desc_italian;
    }

    public String getName_irish() {
        return name_irish;
    }

    public void setName_irish(String name_irish) {
        this.name_irish = name_irish;
    }

    public String getDesc_irish() {
        return desc_irish;
    }

    public void setDesc_irish(String desc_irish) {
        this.desc_irish = desc_irish;
    }

    public String getName_japanese() {
        return name_japanese;
    }

    public void setName_japanese(String name_japanese) {
        this.name_japanese = name_japanese;
    }

    public String getDesc_japanese() {
        return desc_japanese;
    }

    public void setDesc_japanese(String desc_japanese) {
        this.desc_japanese = desc_japanese;
    }

    public String getName_korean() {
        return name_korean;
    }

    public void setName_korean(String name_korean) {
        this.name_korean = name_korean;
    }

    public String getDesc_korean() {
        return desc_korean;
    }

    public void setDesc_korean(String desc_korean) {
        this.desc_korean = desc_korean;
    }

    public String getName_english() {
        return name_english;
    }

    public void setName_english(String name_english) {
        this.name_english = name_english;
    }

    public String getDesc_english() {
        return desc_english;
    }

    public void setDesc_english(String desc_english) {
        this.desc_english = desc_english;
    }

    public String getName_latvian() {
        return name_latvian;
    }

    public void setName_latvian(String name_latvian) {
        this.name_latvian = name_latvian;
    }

    public String getDesc_latvian() {
        return desc_latvian;
    }

    public void setDesc_latvian(String desc_latvian) {
        this.desc_latvian = desc_latvian;
    }

    public String getName_norwegian() {
        return name_norwegian;
    }

    public void setName_norwegian(String name_norwegian) {
        this.name_norwegian = name_norwegian;
    }

    public String getDesc_norwegian() {
        return desc_norwegian;
    }

    public void setDesc_norwegian(String desc_norwegian) {
        this.desc_norwegian = desc_norwegian;
    }

    public String getName_persian() {
        return name_persian;
    }

    public void setName_persian(String name_persian) {
        this.name_persian = name_persian;
    }

    public String getDesc_persian() {
        return desc_persian;
    }

    public void setDesc_persian(String desc_persian) {
        this.desc_persian = desc_persian;
    }

    public String getName_polish() {
        return name_polish;
    }

    public void setName_polish(String name_polish) {
        this.name_polish = name_polish;
    }

    public String getDesc_polish() {
        return desc_polish;
    }

    public void setDesc_polish(String desc_polish) {
        this.desc_polish = desc_polish;
    }

    public String getName_portuguese() {
        return name_portuguese;
    }

    public void setName_portuguese(String name_portuguese) {
        this.name_portuguese = name_portuguese;
    }

    public String getDesc_portuguese() {
        return desc_portuguese;
    }

    public void setDesc_portuguese(String desc_portuguese) {
        this.desc_portuguese = desc_portuguese;
    }

    public String getName_romanian() {
        return name_romanian;
    }

    public void setName_romanian(String name_romanian) {
        this.name_romanian = name_romanian;
    }

    public String getDesc_romanian() {
        return desc_romanian;
    }

    public void setDesc_romanian(String desc_romanian) {
        this.desc_romanian = desc_romanian;
    }

    public String getName_russian() {
        return name_russian;
    }

    public void setName_russian(String name_russian) {
        this.name_russian = name_russian;
    }

    public String getDesc_russian() {
        return desc_russian;
    }

    public void setDesc_russian(String desc_russian) {
        this.desc_russian = desc_russian;
    }

    public String getName_scandinavian() {
        return name_scandinavian;
    }

    public void setName_scandinavian(String name_scandinavian) {
        this.name_scandinavian = name_scandinavian;
    }

    public String getDesc_scandinavian() {
        return desc_scandinavian;
    }

    public void setDesc_scandinavian(String desc_scandinavian) {
        this.desc_scandinavian = desc_scandinavian;
    }

    public String getName_serbian() {
        return name_serbian;
    }

    public void setName_serbian(String name_serbian) {
        this.name_serbian = name_serbian;
    }

    public String getDesc_serbian() {
        return desc_serbian;
    }

    public void setDesc_serbian(String desc_serbian) {
        this.desc_serbian = desc_serbian;
    }

    public String getName_spanish() {
        return name_spanish;
    }

    public void setName_spanish(String name_spanish) {
        this.name_spanish = name_spanish;
    }

    public String getDesc_spanish() {
        return desc_spanish;
    }

    public void setDesc_spanish(String desc_spanish) {
        this.desc_spanish = desc_spanish;
    }

    public String getName_swedish() {
        return name_swedish;
    }

    public void setName_swedish(String name_swedish) {
        this.name_swedish = name_swedish;
    }

    public String getDesc_swedish() {
        return desc_swedish;
    }

    public void setDesc_swedish(String desc_swedish) {
        this.desc_swedish = desc_swedish;
    }

    public String getName_thai() {
        return name_thai;
    }

    public void setName_thai(String name_thai) {
        this.name_thai = name_thai;
    }

    public String getDesc_thai() {
        return desc_thai;
    }

    public void setDesc_thai(String desc_thai) {
        this.desc_thai = desc_thai;
    }

    public String getName_turkish() {
        return name_turkish;
    }

    public void setName_turkish(String name_turkish) {
        this.name_turkish = name_turkish;
    }

    public String getDesc_turkish() {
        return desc_turkish;
    }

    public void setDesc_turkish(String desc_turkish) {
        this.desc_turkish = desc_turkish;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPerms() {
        return perms;
    }

    public void setPerms(String perms) {
        this.perms = perms;
    }

    public String getSettings() {
        return settings;
    }

    public void setSettings(String settings) {
        this.settings = settings;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDefaultLang() {
        return defaultLang;
    }

    public void setDefaultLang(String defaultLang) {
        this.defaultLang = defaultLang;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }


    @Override
    public SharingLink clone(){
        return (SharingLink) super.clone();
    }


    


}
