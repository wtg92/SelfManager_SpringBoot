package manager.entity.general.books;

import manager.entity.SMSolrDoc;
import org.apache.solr.client.solrj.beans.Field;

import java.util.List;

public class BookPage extends SMSolrDoc {

    @Field
    private String bookId;

    @Field
    private List<String> parentIds;

    @Field
    private List<Integer> indexes;

    /**
     * 变量还是需要有一个类型的
     * 这样为未来的扩展比较好
     * 并且设置语言
     */
    @Field
    private List<String> variables;
    @Field private String name_arabic;
    @Field private String content_arabic;

    @Field private String name_bengali;
    @Field private String content_bengali;

    @Field private String name_brazilian_portuguese;
    @Field private String content_brazilian_portuguese;

    @Field private String name_bulgarian;
    @Field private String content_bulgarian;

    @Field private String name_catalan;
    @Field private String content_catalan;

    @Field private String name_chinese;
    @Field private String content_chinese;

    @Field private String name_traditional_chinese;
    @Field private String content_traditional_chinese;

    @Field private String name_czech;
    @Field private String content_czech;

    @Field private String name_danish;
    @Field private String content_danish;

    @Field private String name_dutch;
    @Field private String content_dutch;

    @Field private String name_estonian;
    @Field private String content_estonian;

    @Field private String name_finnish;
    @Field private String content_finnish;

    @Field private String name_french;
    @Field private String content_french;

    @Field private String name_galician;
    @Field private String content_galician;

    @Field private String name_german;
    @Field private String content_german;

    @Field private String name_greek;
    @Field private String content_greek;

    @Field private String name_hindi;
    @Field private String content_hindi;

    @Field private String name_indonesian;
    @Field private String content_indonesian;

    @Field private String name_italian;
    @Field private String content_italian;

    @Field private String name_irish;
    @Field private String content_irish;

    @Field private String name_japanese;
    @Field private String content_japanese;

    @Field private String name_korean;
    @Field private String content_korean;

    @Field private String name_english;
    @Field private String content_english;

    @Field private String name_latvian;
    @Field private String content_latvian;

    @Field private String name_norwegian;
    @Field private String content_norwegian;

    @Field private String name_persian;
    @Field private String content_persian;

    @Field private String name_polish;
    @Field private String content_polish;

    @Field private String name_portuguese;
    @Field private String content_portuguese;

    @Field private String name_romanian;
    @Field private String content_romanian;

    @Field private String name_russian;
    @Field private String content_russian;

    @Field private String name_scandinavian;
    @Field private String content_scandinavian;

    @Field private String name_serbian;
    @Field private String content_serbian;

    @Field private String name_spanish;
    @Field private String content_spanish;

    @Field private String name_swedish;
    @Field private String content_swedish;

    @Field private String name_thai;
    @Field private String content_thai;

    @Field private String name_turkish;
    @Field private String content_turkish;

    @Field private String name_ukrainian;
    @Field private String content_ukrainian;

    @Override
    public BookPage clone(){
        return (BookPage) super.clone();
    }

    public List<String> getVariables() {
        return variables;
    }

    public void setVariables(List<String> variables) {
        this.variables = variables;
    }

    public String getName_arabic() {
        return name_arabic;
    }

    public void setName_arabic(String name_arabic) {
        this.name_arabic = name_arabic;
    }

    public String getContent_arabic() {
        return content_arabic;
    }

    public void setContent_arabic(String content_arabic) {
        this.content_arabic = content_arabic;
    }

    public String getName_bengali() {
        return name_bengali;
    }

    public void setName_bengali(String name_bengali) {
        this.name_bengali = name_bengali;
    }

    public String getContent_bengali() {
        return content_bengali;
    }

    public void setContent_bengali(String content_bengali) {
        this.content_bengali = content_bengali;
    }

    public String getName_brazilian_portuguese() {
        return name_brazilian_portuguese;
    }

    public void setName_brazilian_portuguese(String name_brazilian_portuguese) {
        this.name_brazilian_portuguese = name_brazilian_portuguese;
    }

    public String getContent_brazilian_portuguese() {
        return content_brazilian_portuguese;
    }

    public void setContent_brazilian_portuguese(String content_brazilian_portuguese) {
        this.content_brazilian_portuguese = content_brazilian_portuguese;
    }

    public String getName_bulgarian() {
        return name_bulgarian;
    }

    public void setName_bulgarian(String name_bulgarian) {
        this.name_bulgarian = name_bulgarian;
    }

    public String getContent_bulgarian() {
        return content_bulgarian;
    }

    public void setContent_bulgarian(String content_bulgarian) {
        this.content_bulgarian = content_bulgarian;
    }

    public String getName_catalan() {
        return name_catalan;
    }

    public void setName_catalan(String name_catalan) {
        this.name_catalan = name_catalan;
    }

    public String getContent_catalan() {
        return content_catalan;
    }

    public void setContent_catalan(String content_catalan) {
        this.content_catalan = content_catalan;
    }

    public String getName_chinese() {
        return name_chinese;
    }

    public void setName_chinese(String name_chinese) {
        this.name_chinese = name_chinese;
    }

    public String getContent_chinese() {
        return content_chinese;
    }

    public void setContent_chinese(String content_chinese) {
        this.content_chinese = content_chinese;
    }

    public String getName_traditional_chinese() {
        return name_traditional_chinese;
    }

    public void setName_traditional_chinese(String name_traditional_chinese) {
        this.name_traditional_chinese = name_traditional_chinese;
    }

    public String getContent_traditional_chinese() {
        return content_traditional_chinese;
    }

    public void setContent_traditional_chinese(String content_traditional_chinese) {
        this.content_traditional_chinese = content_traditional_chinese;
    }

    public String getName_czech() {
        return name_czech;
    }

    public void setName_czech(String name_czech) {
        this.name_czech = name_czech;
    }

    public String getContent_czech() {
        return content_czech;
    }

    public void setContent_czech(String content_czech) {
        this.content_czech = content_czech;
    }

    public String getName_danish() {
        return name_danish;
    }

    public void setName_danish(String name_danish) {
        this.name_danish = name_danish;
    }

    public String getContent_danish() {
        return content_danish;
    }

    public void setContent_danish(String content_danish) {
        this.content_danish = content_danish;
    }

    public String getName_dutch() {
        return name_dutch;
    }

    public void setName_dutch(String name_dutch) {
        this.name_dutch = name_dutch;
    }

    public String getContent_dutch() {
        return content_dutch;
    }

    public void setContent_dutch(String content_dutch) {
        this.content_dutch = content_dutch;
    }

    public String getName_estonian() {
        return name_estonian;
    }

    public void setName_estonian(String name_estonian) {
        this.name_estonian = name_estonian;
    }

    public String getContent_estonian() {
        return content_estonian;
    }

    public void setContent_estonian(String content_estonian) {
        this.content_estonian = content_estonian;
    }

    public String getName_finnish() {
        return name_finnish;
    }

    public void setName_finnish(String name_finnish) {
        this.name_finnish = name_finnish;
    }

    public String getContent_finnish() {
        return content_finnish;
    }

    public void setContent_finnish(String content_finnish) {
        this.content_finnish = content_finnish;
    }

    public String getName_french() {
        return name_french;
    }

    public void setName_french(String name_french) {
        this.name_french = name_french;
    }

    public String getContent_french() {
        return content_french;
    }

    public void setContent_french(String content_french) {
        this.content_french = content_french;
    }

    public String getName_galician() {
        return name_galician;
    }

    public void setName_galician(String name_galician) {
        this.name_galician = name_galician;
    }

    public String getContent_galician() {
        return content_galician;
    }

    public void setContent_galician(String content_galician) {
        this.content_galician = content_galician;
    }

    public String getName_german() {
        return name_german;
    }

    public void setName_german(String name_german) {
        this.name_german = name_german;
    }

    public String getContent_german() {
        return content_german;
    }

    public void setContent_german(String content_german) {
        this.content_german = content_german;
    }

    public String getName_greek() {
        return name_greek;
    }

    public void setName_greek(String name_greek) {
        this.name_greek = name_greek;
    }

    public String getContent_greek() {
        return content_greek;
    }

    public void setContent_greek(String content_greek) {
        this.content_greek = content_greek;
    }

    public String getName_hindi() {
        return name_hindi;
    }

    public void setName_hindi(String name_hindi) {
        this.name_hindi = name_hindi;
    }

    public String getContent_hindi() {
        return content_hindi;
    }

    public void setContent_hindi(String content_hindi) {
        this.content_hindi = content_hindi;
    }

    public String getName_indonesian() {
        return name_indonesian;
    }

    public void setName_indonesian(String name_indonesian) {
        this.name_indonesian = name_indonesian;
    }

    public String getContent_indonesian() {
        return content_indonesian;
    }

    public void setContent_indonesian(String content_indonesian) {
        this.content_indonesian = content_indonesian;
    }

    public String getName_italian() {
        return name_italian;
    }

    public void setName_italian(String name_italian) {
        this.name_italian = name_italian;
    }

    public String getContent_italian() {
        return content_italian;
    }

    public void setContent_italian(String content_italian) {
        this.content_italian = content_italian;
    }

    public String getName_irish() {
        return name_irish;
    }

    public void setName_irish(String name_irish) {
        this.name_irish = name_irish;
    }

    public String getContent_irish() {
        return content_irish;
    }

    public void setContent_irish(String content_irish) {
        this.content_irish = content_irish;
    }

    public String getName_japanese() {
        return name_japanese;
    }

    public void setName_japanese(String name_japanese) {
        this.name_japanese = name_japanese;
    }

    public String getContent_japanese() {
        return content_japanese;
    }

    public void setContent_japanese(String content_japanese) {
        this.content_japanese = content_japanese;
    }

    public String getName_korean() {
        return name_korean;
    }

    public void setName_korean(String name_korean) {
        this.name_korean = name_korean;
    }

    public String getContent_korean() {
        return content_korean;
    }

    public void setContent_korean(String content_korean) {
        this.content_korean = content_korean;
    }

    public String getName_english() {
        return name_english;
    }

    public void setName_english(String name_english) {
        this.name_english = name_english;
    }

    public String getContent_english() {
        return content_english;
    }

    public void setContent_english(String content_english) {
        this.content_english = content_english;
    }

    public String getName_latvian() {
        return name_latvian;
    }

    public void setName_latvian(String name_latvian) {
        this.name_latvian = name_latvian;
    }

    public String getContent_latvian() {
        return content_latvian;
    }

    public void setContent_latvian(String content_latvian) {
        this.content_latvian = content_latvian;
    }

    public String getName_norwegian() {
        return name_norwegian;
    }

    public void setName_norwegian(String name_norwegian) {
        this.name_norwegian = name_norwegian;
    }

    public String getContent_norwegian() {
        return content_norwegian;
    }

    public void setContent_norwegian(String content_norwegian) {
        this.content_norwegian = content_norwegian;
    }

    public String getName_persian() {
        return name_persian;
    }

    public void setName_persian(String name_persian) {
        this.name_persian = name_persian;
    }

    public String getContent_persian() {
        return content_persian;
    }

    public void setContent_persian(String content_persian) {
        this.content_persian = content_persian;
    }

    public String getName_polish() {
        return name_polish;
    }

    public void setName_polish(String name_polish) {
        this.name_polish = name_polish;
    }

    public String getContent_polish() {
        return content_polish;
    }

    public void setContent_polish(String content_polish) {
        this.content_polish = content_polish;
    }

    public String getName_portuguese() {
        return name_portuguese;
    }

    public void setName_portuguese(String name_portuguese) {
        this.name_portuguese = name_portuguese;
    }

    public String getContent_portuguese() {
        return content_portuguese;
    }

    public void setContent_portuguese(String content_portuguese) {
        this.content_portuguese = content_portuguese;
    }

    public String getName_romanian() {
        return name_romanian;
    }

    public void setName_romanian(String name_romanian) {
        this.name_romanian = name_romanian;
    }

    public String getContent_romanian() {
        return content_romanian;
    }

    public void setContent_romanian(String content_romanian) {
        this.content_romanian = content_romanian;
    }

    public String getName_russian() {
        return name_russian;
    }

    public void setName_russian(String name_russian) {
        this.name_russian = name_russian;
    }

    public String getContent_russian() {
        return content_russian;
    }

    public void setContent_russian(String content_russian) {
        this.content_russian = content_russian;
    }

    public String getName_scandinavian() {
        return name_scandinavian;
    }

    public void setName_scandinavian(String name_scandinavian) {
        this.name_scandinavian = name_scandinavian;
    }

    public String getContent_scandinavian() {
        return content_scandinavian;
    }

    public void setContent_scandinavian(String content_scandinavian) {
        this.content_scandinavian = content_scandinavian;
    }

    public String getName_serbian() {
        return name_serbian;
    }

    public void setName_serbian(String name_serbian) {
        this.name_serbian = name_serbian;
    }

    public String getContent_serbian() {
        return content_serbian;
    }

    public void setContent_serbian(String content_serbian) {
        this.content_serbian = content_serbian;
    }

    public String getName_spanish() {
        return name_spanish;
    }

    public void setName_spanish(String name_spanish) {
        this.name_spanish = name_spanish;
    }

    public String getContent_spanish() {
        return content_spanish;
    }

    public void setContent_spanish(String content_spanish) {
        this.content_spanish = content_spanish;
    }

    public String getName_swedish() {
        return name_swedish;
    }

    public void setName_swedish(String name_swedish) {
        this.name_swedish = name_swedish;
    }

    public String getContent_swedish() {
        return content_swedish;
    }

    public void setContent_swedish(String content_swedish) {
        this.content_swedish = content_swedish;
    }

    public String getName_thai() {
        return name_thai;
    }

    public void setName_thai(String name_thai) {
        this.name_thai = name_thai;
    }

    public String getContent_thai() {
        return content_thai;
    }

    public void setContent_thai(String content_thai) {
        this.content_thai = content_thai;
    }

    public String getName_turkish() {
        return name_turkish;
    }

    public void setName_turkish(String name_turkish) {
        this.name_turkish = name_turkish;
    }

    public String getContent_turkish() {
        return content_turkish;
    }

    public void setContent_turkish(String content_turkish) {
        this.content_turkish = content_turkish;
    }

    public String getName_ukrainian() {
        return name_ukrainian;
    }

    public void setName_ukrainian(String name_ukrainian) {
        this.name_ukrainian = name_ukrainian;
    }

    public String getContent_ukrainian() {
        return content_ukrainian;
    }

    public void setContent_ukrainian(String content_ukrainian) {
        this.content_ukrainian = content_ukrainian;
    }

    public List<String> getParentIds() {
        return parentIds;
    }

    public void setParentIds(List<String> parentIds) {
        this.parentIds = parentIds;
    }

    public List<Integer> getIndexes() {
        return indexes;
    }

    public void setIndexes(List<Integer> indexes) {
        this.indexes = indexes;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }


}
