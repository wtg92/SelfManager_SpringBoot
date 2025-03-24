package manager.entity.general.books;

import manager.entity.SMSolrDoc;
import org.apache.solr.client.solrj.beans.Field;

import java.util.List;

/**
 * TODO 配置文件里 已经预留出ImgId_ 字段了 有时间 为Book 弄一个封面
 */
public class SharingBook extends SMSolrDoc {
    @Field
    private Integer status;

    @Field
    private Integer style;

    /**
     * Tree or list
     */
    @Field
    private Integer displayPattern;

    @Field
    private Integer seqWeight;

    @Field
    private String defaultLang;



    /**
     * 变量还是需要有一个类型的
     * 这样为未来的扩展比较好
     * 并且设置语言 -- 因为是String 所以可以设置语言
     * 还可以设置 是否区分通用
     */
    @Field
    private List<String> variables;

    @Override
    public SharingBook clone(){
        return (SharingBook) super.clone();
    }

    /**
     * FOR UI
     */
    private String updaterEncodedId;

    public String getUpdaterEncodedId() {
        return updaterEncodedId;
    }

    public void setUpdaterEncodedId(String updaterEncodedId) {
        this.updaterEncodedId = updaterEncodedId;
    }

    @Field private String name_arabic;
    @Field private String comment_arabic;

    @Field private String name_bengali;
    @Field private String comment_bengali;

    @Field private String name_brazilian_portuguese;
    @Field private String comment_brazilian_portuguese;

    @Field private String name_bulgarian;
    @Field private String comment_bulgarian;

    @Field private String name_catalan;
    @Field private String comment_catalan;

    @Field private String name_chinese;
    @Field private String comment_chinese;

    @Field private String name_traditional_chinese;
    @Field private String comment_traditional_chinese;

    @Field private String name_czech;
    @Field private String comment_czech;

    @Field private String name_danish;
    @Field private String comment_danish;

    @Field private String name_dutch;
    @Field private String comment_dutch;

    @Field private String name_estonian;
    @Field private String comment_estonian;

    @Field private String name_finnish;
    @Field private String comment_finnish;

    @Field private String name_french;
    @Field private String comment_french;

    @Field private String name_galician;
    @Field private String comment_galician;

    @Field private String name_german;
    @Field private String comment_german;

    @Field private String name_greek;
    @Field private String comment_greek;

    @Field private String name_hindi;
    @Field private String comment_hindi;

    @Field private String name_indonesian;
    @Field private String comment_indonesian;

    @Field private String name_italian;
    @Field private String comment_italian;

    @Field private String name_irish;
    @Field private String comment_irish;

    @Field private String name_japanese;
    @Field private String comment_japanese;

    @Field private String name_korean;
    @Field private String comment_korean;

    @Field private String name_english;
    @Field private String comment_english;

    @Field private String name_latvian;
    @Field private String comment_latvian;

    @Field private String name_norwegian;
    @Field private String comment_norwegian;

    @Field private String name_persian;
    @Field private String comment_persian;

    @Field private String name_polish;
    @Field private String comment_polish;

    @Field private String name_portuguese;
    @Field private String comment_portuguese;

    @Field private String name_romanian;
    @Field private String comment_romanian;

    @Field private String name_russian;
    @Field private String comment_russian;

    @Field private String name_scandinavian;
    @Field private String comment_scandinavian;

    @Field private String name_serbian;
    @Field private String comment_serbian;

    @Field private String name_spanish;
    @Field private String comment_spanish;

    @Field private String name_swedish;
    @Field private String comment_swedish;

    @Field private String name_thai;
    @Field private String comment_thai;

    @Field private String name_turkish;
    @Field private String comment_turkish;

    @Field private String name_ukrainian;
    @Field private String comment_ukrainian;

    public String getName_traditional_chinese() {
        return name_traditional_chinese;
    }

    public void setName_traditional_chinese(String name_traditional_chinese) {
        this.name_traditional_chinese = name_traditional_chinese;
    }

    public String getComment_traditional_chinese() {
        return comment_traditional_chinese;
    }

    public void setComment_traditional_chinese(String comment_traditional_chinese) {
        this.comment_traditional_chinese = comment_traditional_chinese;
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

    public String getComment_arabic() {
        return comment_arabic;
    }

    public void setComment_arabic(String comment_arabic) {
        this.comment_arabic = comment_arabic;
    }

    public String getName_bengali() {
        return name_bengali;
    }

    public void setName_bengali(String name_bengali) {
        this.name_bengali = name_bengali;
    }

    public String getComment_bengali() {
        return comment_bengali;
    }

    public void setComment_bengali(String comment_bengali) {
        this.comment_bengali = comment_bengali;
    }

    public String getName_brazilian_portuguese() {
        return name_brazilian_portuguese;
    }

    public void setName_brazilian_portuguese(String name_brazilian_portuguese) {
        this.name_brazilian_portuguese = name_brazilian_portuguese;
    }

    public String getComment_brazilian_portuguese() {
        return comment_brazilian_portuguese;
    }

    public void setComment_brazilian_portuguese(String comment_brazilian_portuguese) {
        this.comment_brazilian_portuguese = comment_brazilian_portuguese;
    }

    public String getName_bulgarian() {
        return name_bulgarian;
    }

    public void setName_bulgarian(String name_bulgarian) {
        this.name_bulgarian = name_bulgarian;
    }

    public String getComment_bulgarian() {
        return comment_bulgarian;
    }

    public void setComment_bulgarian(String comment_bulgarian) {
        this.comment_bulgarian = comment_bulgarian;
    }

    public String getName_catalan() {
        return name_catalan;
    }

    public void setName_catalan(String name_catalan) {
        this.name_catalan = name_catalan;
    }

    public String getComment_catalan() {
        return comment_catalan;
    }

    public void setComment_catalan(String comment_catalan) {
        this.comment_catalan = comment_catalan;
    }

    public String getName_chinese() {
        return name_chinese;
    }

    public void setName_chinese(String name_chinese) {
        this.name_chinese = name_chinese;
    }

    public String getComment_chinese() {
        return comment_chinese;
    }

    public void setComment_chinese(String comment_chinese) {
        this.comment_chinese = comment_chinese;
    }

    public String getName_czech() {
        return name_czech;
    }

    public void setName_czech(String name_czech) {
        this.name_czech = name_czech;
    }

    public String getComment_czech() {
        return comment_czech;
    }

    public void setComment_czech(String comment_czech) {
        this.comment_czech = comment_czech;
    }

    public String getName_danish() {
        return name_danish;
    }

    public void setName_danish(String name_danish) {
        this.name_danish = name_danish;
    }

    public String getComment_danish() {
        return comment_danish;
    }

    public void setComment_danish(String comment_danish) {
        this.comment_danish = comment_danish;
    }

    public String getName_dutch() {
        return name_dutch;
    }

    public void setName_dutch(String name_dutch) {
        this.name_dutch = name_dutch;
    }

    public String getComment_dutch() {
        return comment_dutch;
    }

    public void setComment_dutch(String comment_dutch) {
        this.comment_dutch = comment_dutch;
    }

    public String getName_estonian() {
        return name_estonian;
    }

    public void setName_estonian(String name_estonian) {
        this.name_estonian = name_estonian;
    }

    public String getComment_estonian() {
        return comment_estonian;
    }

    public void setComment_estonian(String comment_estonian) {
        this.comment_estonian = comment_estonian;
    }

    public String getName_finnish() {
        return name_finnish;
    }

    public void setName_finnish(String name_finnish) {
        this.name_finnish = name_finnish;
    }

    public String getComment_finnish() {
        return comment_finnish;
    }

    public void setComment_finnish(String comment_finnish) {
        this.comment_finnish = comment_finnish;
    }

    public String getName_french() {
        return name_french;
    }

    public void setName_french(String name_french) {
        this.name_french = name_french;
    }

    public String getComment_french() {
        return comment_french;
    }

    public void setComment_french(String comment_french) {
        this.comment_french = comment_french;
    }

    public String getName_galician() {
        return name_galician;
    }

    public void setName_galician(String name_galician) {
        this.name_galician = name_galician;
    }

    public String getComment_galician() {
        return comment_galician;
    }

    public void setComment_galician(String comment_galician) {
        this.comment_galician = comment_galician;
    }

    public String getName_german() {
        return name_german;
    }

    public void setName_german(String name_german) {
        this.name_german = name_german;
    }

    public String getComment_german() {
        return comment_german;
    }

    public void setComment_german(String comment_german) {
        this.comment_german = comment_german;
    }

    public String getName_greek() {
        return name_greek;
    }

    public void setName_greek(String name_greek) {
        this.name_greek = name_greek;
    }

    public String getComment_greek() {
        return comment_greek;
    }

    public void setComment_greek(String comment_greek) {
        this.comment_greek = comment_greek;
    }

    public String getName_hindi() {
        return name_hindi;
    }

    public void setName_hindi(String name_hindi) {
        this.name_hindi = name_hindi;
    }

    public String getComment_hindi() {
        return comment_hindi;
    }

    public void setComment_hindi(String comment_hindi) {
        this.comment_hindi = comment_hindi;
    }

    public String getName_indonesian() {
        return name_indonesian;
    }

    public void setName_indonesian(String name_indonesian) {
        this.name_indonesian = name_indonesian;
    }

    public String getComment_indonesian() {
        return comment_indonesian;
    }

    public void setComment_indonesian(String comment_indonesian) {
        this.comment_indonesian = comment_indonesian;
    }

    public String getName_italian() {
        return name_italian;
    }

    public void setName_italian(String name_italian) {
        this.name_italian = name_italian;
    }

    public String getComment_italian() {
        return comment_italian;
    }

    public void setComment_italian(String comment_italian) {
        this.comment_italian = comment_italian;
    }

    public String getName_irish() {
        return name_irish;
    }

    public void setName_irish(String name_irish) {
        this.name_irish = name_irish;
    }

    public String getComment_irish() {
        return comment_irish;
    }

    public void setComment_irish(String comment_irish) {
        this.comment_irish = comment_irish;
    }

    public String getName_japanese() {
        return name_japanese;
    }

    public void setName_japanese(String name_japanese) {
        this.name_japanese = name_japanese;
    }

    public String getComment_japanese() {
        return comment_japanese;
    }

    public void setComment_japanese(String comment_japanese) {
        this.comment_japanese = comment_japanese;
    }

    public String getName_korean() {
        return name_korean;
    }

    public void setName_korean(String name_korean) {
        this.name_korean = name_korean;
    }

    public String getComment_korean() {
        return comment_korean;
    }

    public void setComment_korean(String comment_korean) {
        this.comment_korean = comment_korean;
    }

    public String getName_english() {
        return name_english;
    }

    public void setName_english(String name_english) {
        this.name_english = name_english;
    }

    public String getComment_english() {
        return comment_english;
    }

    public void setComment_english(String comment_english) {
        this.comment_english = comment_english;
    }

    public String getName_latvian() {
        return name_latvian;
    }

    public void setName_latvian(String name_latvian) {
        this.name_latvian = name_latvian;
    }

    public String getComment_latvian() {
        return comment_latvian;
    }

    public void setComment_latvian(String comment_latvian) {
        this.comment_latvian = comment_latvian;
    }

    public String getName_norwegian() {
        return name_norwegian;
    }

    public void setName_norwegian(String name_norwegian) {
        this.name_norwegian = name_norwegian;
    }

    public String getComment_norwegian() {
        return comment_norwegian;
    }

    public void setComment_norwegian(String comment_norwegian) {
        this.comment_norwegian = comment_norwegian;
    }

    public String getName_persian() {
        return name_persian;
    }

    public void setName_persian(String name_persian) {
        this.name_persian = name_persian;
    }

    public String getComment_persian() {
        return comment_persian;
    }

    public void setComment_persian(String comment_persian) {
        this.comment_persian = comment_persian;
    }

    public String getName_polish() {
        return name_polish;
    }

    public void setName_polish(String name_polish) {
        this.name_polish = name_polish;
    }

    public String getComment_polish() {
        return comment_polish;
    }

    public void setComment_polish(String comment_polish) {
        this.comment_polish = comment_polish;
    }

    public String getName_portuguese() {
        return name_portuguese;
    }

    public void setName_portuguese(String name_portuguese) {
        this.name_portuguese = name_portuguese;
    }

    public String getComment_portuguese() {
        return comment_portuguese;
    }

    public void setComment_portuguese(String comment_portuguese) {
        this.comment_portuguese = comment_portuguese;
    }

    public String getName_romanian() {
        return name_romanian;
    }

    public void setName_romanian(String name_romanian) {
        this.name_romanian = name_romanian;
    }

    public String getComment_romanian() {
        return comment_romanian;
    }

    public void setComment_romanian(String comment_romanian) {
        this.comment_romanian = comment_romanian;
    }

    public String getName_russian() {
        return name_russian;
    }

    public void setName_russian(String name_russian) {
        this.name_russian = name_russian;
    }

    public String getComment_russian() {
        return comment_russian;
    }

    public void setComment_russian(String comment_russian) {
        this.comment_russian = comment_russian;
    }

    public String getName_scandinavian() {
        return name_scandinavian;
    }

    public void setName_scandinavian(String name_scandinavian) {
        this.name_scandinavian = name_scandinavian;
    }

    public String getComment_scandinavian() {
        return comment_scandinavian;
    }

    public void setComment_scandinavian(String comment_scandinavian) {
        this.comment_scandinavian = comment_scandinavian;
    }

    public String getName_serbian() {
        return name_serbian;
    }

    public void setName_serbian(String name_serbian) {
        this.name_serbian = name_serbian;
    }

    public String getComment_serbian() {
        return comment_serbian;
    }

    public void setComment_serbian(String comment_serbian) {
        this.comment_serbian = comment_serbian;
    }

    public String getName_spanish() {
        return name_spanish;
    }

    public void setName_spanish(String name_spanish) {
        this.name_spanish = name_spanish;
    }

    public String getComment_spanish() {
        return comment_spanish;
    }

    public void setComment_spanish(String comment_spanish) {
        this.comment_spanish = comment_spanish;
    }

    public String getName_swedish() {
        return name_swedish;
    }

    public void setName_swedish(String name_swedish) {
        this.name_swedish = name_swedish;
    }

    public String getComment_swedish() {
        return comment_swedish;
    }

    public void setComment_swedish(String comment_swedish) {
        this.comment_swedish = comment_swedish;
    }

    public String getName_thai() {
        return name_thai;
    }

    public void setName_thai(String name_thai) {
        this.name_thai = name_thai;
    }

    public String getComment_thai() {
        return comment_thai;
    }

    public void setComment_thai(String comment_thai) {
        this.comment_thai = comment_thai;
    }

    public String getName_turkish() {
        return name_turkish;
    }

    public void setName_turkish(String name_turkish) {
        this.name_turkish = name_turkish;
    }

    public String getComment_turkish() {
        return comment_turkish;
    }

    public void setComment_turkish(String comment_turkish) {
        this.comment_turkish = comment_turkish;
    }

    public String getName_ukrainian() {
        return name_ukrainian;
    }

    public void setName_ukrainian(String name_ukrainian) {
        this.name_ukrainian = name_ukrainian;
    }

    public String getComment_ukrainian() {
        return comment_ukrainian;
    }

    public void setComment_ukrainian(String comment_ukrainian) {
        this.comment_ukrainian = comment_ukrainian;
    }

    public Integer getDisplayPattern() {
        return displayPattern;
    }

    public void setDisplayPattern(Integer displayPattern) {
        this.displayPattern = displayPattern;
    }


    public String getDefaultLang() {
        return defaultLang;
    }

    public void setDefaultLang(String defaultLang) {
        this.defaultLang = defaultLang;
    }


    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStyle() {
        return style;
    }

    public void setStyle(Integer style) {
        this.style = style;
    }

    public Integer getSeqWeight() {
        return seqWeight;
    }

    public void setSeqWeight(Integer seqWeight) {
        this.seqWeight = seqWeight;
    }


}
