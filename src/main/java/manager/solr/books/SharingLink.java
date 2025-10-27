package manager.solr.books;

import manager.solr.data.SharingLinkPermission;
import manager.entity.SMSolrDoc;
import org.apache.solr.client.solrj.beans.Field;

import java.util.List;

public class SharingLink extends SMSolrDoc {


    private SharingLinkPermission decodedPerm;



    private String sharingLink;
    private Float score;

    private PageNode content;

    /*
     * 1.SinglePage 2.FullBook 3.PageNode
     */
    @Field
    private Integer type;

    @Field
    private String contentId;

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
    private Integer copyNum;

    @Field
    private Integer likesNum;

    @Field
    private Integer dislikesNum;

    @Field List<Long> likeUser;

    @Field List<Long> dislikeUsers;


    /*
     * Tags_
     * 1.为了查看公共链接时 可以分类
     * 2.进入搜索 完全匹配 同样影响评分
     * 3.有上限
     */
    @Field List<String> tags_arabic;
    @Field List<String> tags_bengali;
    @Field List<String> tags_brazilian_portuguese;
    @Field List<String> tags_bulgarian;
    @Field List<String> tags_catalan;
    @Field List<String> tags_chinese;
    @Field List<String> tags_traditional_chinese;
    @Field List<String> tags_czech;
    @Field List<String> tags_danish;
    @Field List<String> tags_dutch;
    @Field List<String> tags_estonian;
    @Field List<String> tags_finnish;
    @Field List<String> tags_french;
    @Field List<String> tags_galician;
    @Field List<String> tags_german;
    @Field List<String> tags_greek;
    @Field List<String> tags_hindi;
    @Field List<String> tags_indonesian;
    @Field List<String> tags_italian;
    @Field List<String> tags_irish;
    @Field List<String> tags_japanese;
    @Field List<String> tags_korean;
    @Field List<String> tags_english;
    @Field List<String> tags_latvian;
    @Field List<String> tags_norwegian;
    @Field List<String> tags_persian;
    @Field List<String> tags_polish;
    @Field List<String> tags_portuguese;
    @Field List<String> tags_romanian;
    @Field List<String> tags_russian;
    @Field List<String> tags_scandinavian;
    @Field List<String> tags_serbian;
    @Field List<String> tags_spanish;
    @Field List<String> tags_swedish;
    @Field List<String> tags_thai;
    @Field List<String> tags_turkish;
    @Field List<String> tags_ukrainian;



    @Field private String desc_extra_arabic;
    @Field private String desc_extra_bengali;
    @Field private String desc_extra_brazilian_portuguese;
    @Field private String desc_extra_bulgarian;
    @Field private String desc_extra_catalan;
    @Field private String desc_extra_chinese;
    @Field private String desc_extra_traditional_chinese;
    @Field private String desc_extra_czech;
    @Field private String desc_extra_danish;
    @Field private String desc_extra_dutch;
    @Field private String desc_extra_estonian;
    @Field private String desc_extra_finnish;
    @Field private String desc_extra_french;
    @Field private String desc_extra_galician;
    @Field private String desc_extra_german;
    @Field private String desc_extra_greek;
    @Field private String desc_extra_hindi;
    @Field private String desc_extra_indonesian;
    @Field private String desc_extra_italian;
    @Field private String desc_extra_irish;
    @Field private String desc_extra_japanese;
    @Field private String desc_extra_korean;
    @Field private String desc_extra_english;
    @Field private String desc_extra_latvian;
    @Field private String desc_extra_norwegian;
    @Field private String desc_extra_persian;
    @Field private String desc_extra_polish;
    @Field private String desc_extra_portuguese;
    @Field private String desc_extra_romanian;
    @Field private String desc_extra_russian;
    @Field private String desc_extra_scandinavian;
    @Field private String desc_extra_serbian;
    @Field private String desc_extra_spanish;
    @Field private String desc_extra_swedish;
    @Field private String desc_extra_thai;
    @Field private String desc_extra_turkish;
    @Field private String desc_extra_ukrainian;

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

    @Field private String name_ukrainian;
    @Field private String desc_ukrainian;

    public String getSharingLink() {
        return sharingLink;
    }

    public SharingLinkPermission getDecodedPerm() {
        return decodedPerm;
    }

    public void setDecodedPerm(SharingLinkPermission decodedPerm) {
        this.decodedPerm = decodedPerm;
    }

    public void setSharingLink(String sharingLink) {
        this.sharingLink = sharingLink;
    }

    public PageNode getContent() {
        return content;
    }

    public void setContent(PageNode content) {
        this.content = content;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public List<String> getTags_arabic() {
        return tags_arabic;
    }

    public void setTags_arabic(List<String> tags_arabic) {
        this.tags_arabic = tags_arabic;
    }

    public List<String> getTags_bengali() {
        return tags_bengali;
    }

    public void setTags_bengali(List<String> tags_bengali) {
        this.tags_bengali = tags_bengali;
    }

    public List<String> getTags_brazilian_portuguese() {
        return tags_brazilian_portuguese;
    }

    public void setTags_brazilian_portuguese(List<String> tags_brazilian_portuguese) {
        this.tags_brazilian_portuguese = tags_brazilian_portuguese;
    }

    public List<String> getTags_bulgarian() {
        return tags_bulgarian;
    }

    public void setTags_bulgarian(List<String> tags_bulgarian) {
        this.tags_bulgarian = tags_bulgarian;
    }

    public List<String> getTags_catalan() {
        return tags_catalan;
    }

    public void setTags_catalan(List<String> tags_catalan) {
        this.tags_catalan = tags_catalan;
    }

    public List<String> getTags_chinese() {
        return tags_chinese;
    }

    public void setTags_chinese(List<String> tags_chinese) {
        this.tags_chinese = tags_chinese;
    }

    public List<String> getTags_traditional_chinese() {
        return tags_traditional_chinese;
    }

    public void setTags_traditional_chinese(List<String> tags_traditional_chinese) {
        this.tags_traditional_chinese = tags_traditional_chinese;
    }

    public List<String> getTags_czech() {
        return tags_czech;
    }

    public void setTags_czech(List<String> tags_czech) {
        this.tags_czech = tags_czech;
    }

    public List<String> getTags_danish() {
        return tags_danish;
    }

    public void setTags_danish(List<String> tags_danish) {
        this.tags_danish = tags_danish;
    }

    public List<String> getTags_dutch() {
        return tags_dutch;
    }

    public void setTags_dutch(List<String> tags_dutch) {
        this.tags_dutch = tags_dutch;
    }

    public List<String> getTags_estonian() {
        return tags_estonian;
    }

    public void setTags_estonian(List<String> tags_estonian) {
        this.tags_estonian = tags_estonian;
    }

    public List<String> getTags_finnish() {
        return tags_finnish;
    }

    public void setTags_finnish(List<String> tags_finnish) {
        this.tags_finnish = tags_finnish;
    }

    public List<String> getTags_french() {
        return tags_french;
    }

    public void setTags_french(List<String> tags_french) {
        this.tags_french = tags_french;
    }

    public List<String> getTags_galician() {
        return tags_galician;
    }

    public void setTags_galician(List<String> tags_galician) {
        this.tags_galician = tags_galician;
    }

    public List<String> getTags_german() {
        return tags_german;
    }

    public void setTags_german(List<String> tags_german) {
        this.tags_german = tags_german;
    }

    public List<String> getTags_greek() {
        return tags_greek;
    }

    public void setTags_greek(List<String> tags_greek) {
        this.tags_greek = tags_greek;
    }

    public List<String> getTags_hindi() {
        return tags_hindi;
    }

    public void setTags_hindi(List<String> tags_hindi) {
        this.tags_hindi = tags_hindi;
    }

    public List<String> getTags_indonesian() {
        return tags_indonesian;
    }

    public void setTags_indonesian(List<String> tags_indonesian) {
        this.tags_indonesian = tags_indonesian;
    }

    public List<String> getTags_italian() {
        return tags_italian;
    }

    public void setTags_italian(List<String> tags_italian) {
        this.tags_italian = tags_italian;
    }

    public List<String> getTags_irish() {
        return tags_irish;
    }

    public void setTags_irish(List<String> tags_irish) {
        this.tags_irish = tags_irish;
    }

    public List<String> getTags_japanese() {
        return tags_japanese;
    }

    public void setTags_japanese(List<String> tags_japanese) {
        this.tags_japanese = tags_japanese;
    }

    public List<String> getTags_korean() {
        return tags_korean;
    }

    public void setTags_korean(List<String> tags_korean) {
        this.tags_korean = tags_korean;
    }

    public List<String> getTags_english() {
        return tags_english;
    }

    public void setTags_english(List<String> tags_english) {
        this.tags_english = tags_english;
    }

    public List<String> getTags_latvian() {
        return tags_latvian;
    }

    public void setTags_latvian(List<String> tags_latvian) {
        this.tags_latvian = tags_latvian;
    }

    public List<String> getTags_norwegian() {
        return tags_norwegian;
    }

    public void setTags_norwegian(List<String> tags_norwegian) {
        this.tags_norwegian = tags_norwegian;
    }

    public List<String> getTags_persian() {
        return tags_persian;
    }

    public void setTags_persian(List<String> tags_persian) {
        this.tags_persian = tags_persian;
    }

    public List<String> getTags_polish() {
        return tags_polish;
    }

    public void setTags_polish(List<String> tags_polish) {
        this.tags_polish = tags_polish;
    }

    public List<String> getTags_portuguese() {
        return tags_portuguese;
    }

    public void setTags_portuguese(List<String> tags_portuguese) {
        this.tags_portuguese = tags_portuguese;
    }

    public List<String> getTags_romanian() {
        return tags_romanian;
    }

    public void setTags_romanian(List<String> tags_romanian) {
        this.tags_romanian = tags_romanian;
    }

    public List<String> getTags_russian() {
        return tags_russian;
    }

    public void setTags_russian(List<String> tags_russian) {
        this.tags_russian = tags_russian;
    }

    public List<String> getTags_scandinavian() {
        return tags_scandinavian;
    }

    public void setTags_scandinavian(List<String> tags_scandinavian) {
        this.tags_scandinavian = tags_scandinavian;
    }

    public List<String> getTags_serbian() {
        return tags_serbian;
    }

    public void setTags_serbian(List<String> tags_serbian) {
        this.tags_serbian = tags_serbian;
    }

    public List<String> getTags_spanish() {
        return tags_spanish;
    }

    public void setTags_spanish(List<String> tags_spanish) {
        this.tags_spanish = tags_spanish;
    }

    public List<String> getTags_swedish() {
        return tags_swedish;
    }

    public void setTags_swedish(List<String> tags_swedish) {
        this.tags_swedish = tags_swedish;
    }

    public List<String> getTags_thai() {
        return tags_thai;
    }

    public void setTags_thai(List<String> tags_thai) {
        this.tags_thai = tags_thai;
    }

    public List<String> getTags_turkish() {
        return tags_turkish;
    }

    public void setTags_turkish(List<String> tags_turkish) {
        this.tags_turkish = tags_turkish;
    }

    public List<String> getTags_ukrainian() {
        return tags_ukrainian;
    }

    public void setTags_ukrainian(List<String> tags_ukrainian) {
        this.tags_ukrainian = tags_ukrainian;
    }

    public String getDesc_extra_arabic() {
        return desc_extra_arabic;
    }

    public void setDesc_extra_arabic(String desc_extra_arabic) {
        this.desc_extra_arabic = desc_extra_arabic;
    }

    public String getDesc_extra_bengali() {
        return desc_extra_bengali;
    }

    public void setDesc_extra_bengali(String desc_extra_bengali) {
        this.desc_extra_bengali = desc_extra_bengali;
    }

    public String getDesc_extra_brazilian_portuguese() {
        return desc_extra_brazilian_portuguese;
    }

    public void setDesc_extra_brazilian_portuguese(String desc_extra_brazilian_portuguese) {
        this.desc_extra_brazilian_portuguese = desc_extra_brazilian_portuguese;
    }

    public String getDesc_extra_bulgarian() {
        return desc_extra_bulgarian;
    }

    public void setDesc_extra_bulgarian(String desc_extra_bulgarian) {
        this.desc_extra_bulgarian = desc_extra_bulgarian;
    }

    public String getDesc_extra_catalan() {
        return desc_extra_catalan;
    }

    public void setDesc_extra_catalan(String desc_extra_catalan) {
        this.desc_extra_catalan = desc_extra_catalan;
    }

    public String getDesc_extra_chinese() {
        return desc_extra_chinese;
    }

    public void setDesc_extra_chinese(String desc_extra_chinese) {
        this.desc_extra_chinese = desc_extra_chinese;
    }

    public String getDesc_extra_traditional_chinese() {
        return desc_extra_traditional_chinese;
    }

    public void setDesc_extra_traditional_chinese(String desc_extra_traditional_chinese) {
        this.desc_extra_traditional_chinese = desc_extra_traditional_chinese;
    }

    public String getDesc_extra_czech() {
        return desc_extra_czech;
    }

    public void setDesc_extra_czech(String desc_extra_czech) {
        this.desc_extra_czech = desc_extra_czech;
    }

    public String getDesc_extra_danish() {
        return desc_extra_danish;
    }

    public void setDesc_extra_danish(String desc_extra_danish) {
        this.desc_extra_danish = desc_extra_danish;
    }

    public String getDesc_extra_dutch() {
        return desc_extra_dutch;
    }

    public void setDesc_extra_dutch(String desc_extra_dutch) {
        this.desc_extra_dutch = desc_extra_dutch;
    }

    public String getDesc_extra_estonian() {
        return desc_extra_estonian;
    }

    public void setDesc_extra_estonian(String desc_extra_estonian) {
        this.desc_extra_estonian = desc_extra_estonian;
    }

    public String getDesc_extra_finnish() {
        return desc_extra_finnish;
    }

    public void setDesc_extra_finnish(String desc_extra_finnish) {
        this.desc_extra_finnish = desc_extra_finnish;
    }

    public String getDesc_extra_french() {
        return desc_extra_french;
    }

    public void setDesc_extra_french(String desc_extra_french) {
        this.desc_extra_french = desc_extra_french;
    }

    public String getDesc_extra_galician() {
        return desc_extra_galician;
    }

    public void setDesc_extra_galician(String desc_extra_galician) {
        this.desc_extra_galician = desc_extra_galician;
    }

    public String getDesc_extra_german() {
        return desc_extra_german;
    }

    public void setDesc_extra_german(String desc_extra_german) {
        this.desc_extra_german = desc_extra_german;
    }

    public String getDesc_extra_greek() {
        return desc_extra_greek;
    }

    public void setDesc_extra_greek(String desc_extra_greek) {
        this.desc_extra_greek = desc_extra_greek;
    }

    public String getDesc_extra_hindi() {
        return desc_extra_hindi;
    }

    public void setDesc_extra_hindi(String desc_extra_hindi) {
        this.desc_extra_hindi = desc_extra_hindi;
    }

    public String getDesc_extra_indonesian() {
        return desc_extra_indonesian;
    }

    public void setDesc_extra_indonesian(String desc_extra_indonesian) {
        this.desc_extra_indonesian = desc_extra_indonesian;
    }

    public String getDesc_extra_italian() {
        return desc_extra_italian;
    }

    public void setDesc_extra_italian(String desc_extra_italian) {
        this.desc_extra_italian = desc_extra_italian;
    }

    public String getDesc_extra_irish() {
        return desc_extra_irish;
    }

    public void setDesc_extra_irish(String desc_extra_irish) {
        this.desc_extra_irish = desc_extra_irish;
    }

    public String getDesc_extra_japanese() {
        return desc_extra_japanese;
    }

    public void setDesc_extra_japanese(String desc_extra_japanese) {
        this.desc_extra_japanese = desc_extra_japanese;
    }

    public String getDesc_extra_korean() {
        return desc_extra_korean;
    }

    public void setDesc_extra_korean(String desc_extra_korean) {
        this.desc_extra_korean = desc_extra_korean;
    }

    public String getDesc_extra_english() {
        return desc_extra_english;
    }

    public void setDesc_extra_english(String desc_extra_english) {
        this.desc_extra_english = desc_extra_english;
    }

    public String getDesc_extra_latvian() {
        return desc_extra_latvian;
    }

    public void setDesc_extra_latvian(String desc_extra_latvian) {
        this.desc_extra_latvian = desc_extra_latvian;
    }

    public String getDesc_extra_norwegian() {
        return desc_extra_norwegian;
    }

    public void setDesc_extra_norwegian(String desc_extra_norwegian) {
        this.desc_extra_norwegian = desc_extra_norwegian;
    }

    public String getDesc_extra_persian() {
        return desc_extra_persian;
    }

    public void setDesc_extra_persian(String desc_extra_persian) {
        this.desc_extra_persian = desc_extra_persian;
    }

    public String getDesc_extra_polish() {
        return desc_extra_polish;
    }

    public void setDesc_extra_polish(String desc_extra_polish) {
        this.desc_extra_polish = desc_extra_polish;
    }

    public String getDesc_extra_portuguese() {
        return desc_extra_portuguese;
    }

    public void setDesc_extra_portuguese(String desc_extra_portuguese) {
        this.desc_extra_portuguese = desc_extra_portuguese;
    }

    public String getDesc_extra_romanian() {
        return desc_extra_romanian;
    }

    public void setDesc_extra_romanian(String desc_extra_romanian) {
        this.desc_extra_romanian = desc_extra_romanian;
    }

    public String getDesc_extra_russian() {
        return desc_extra_russian;
    }

    public void setDesc_extra_russian(String desc_extra_russian) {
        this.desc_extra_russian = desc_extra_russian;
    }

    public String getDesc_extra_scandinavian() {
        return desc_extra_scandinavian;
    }

    public void setDesc_extra_scandinavian(String desc_extra_scandinavian) {
        this.desc_extra_scandinavian = desc_extra_scandinavian;
    }

    public String getDesc_extra_serbian() {
        return desc_extra_serbian;
    }

    public void setDesc_extra_serbian(String desc_extra_serbian) {
        this.desc_extra_serbian = desc_extra_serbian;
    }

    public String getDesc_extra_spanish() {
        return desc_extra_spanish;
    }

    public void setDesc_extra_spanish(String desc_extra_spanish) {
        this.desc_extra_spanish = desc_extra_spanish;
    }

    public String getDesc_extra_swedish() {
        return desc_extra_swedish;
    }

    public void setDesc_extra_swedish(String desc_extra_swedish) {
        this.desc_extra_swedish = desc_extra_swedish;
    }

    public String getDesc_extra_thai() {
        return desc_extra_thai;
    }

    public void setDesc_extra_thai(String desc_extra_thai) {
        this.desc_extra_thai = desc_extra_thai;
    }

    public String getDesc_extra_turkish() {
        return desc_extra_turkish;
    }

    public void setDesc_extra_turkish(String desc_extra_turkish) {
        this.desc_extra_turkish = desc_extra_turkish;
    }

    public String getDesc_extra_ukrainian() {
        return desc_extra_ukrainian;
    }

    public void setDesc_extra_ukrainian(String desc_extra_ukrainian) {
        this.desc_extra_ukrainian = desc_extra_ukrainian;
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

    public Integer getCopyNum() {
        return copyNum;
    }

    public void setCopyNum(Integer copyNum) {
        this.copyNum = copyNum;
    }

    public Integer getLikesNum() {
        return likesNum;
    }

    public void setLikesNum(Integer likesNum) {
        this.likesNum = likesNum;
    }

    public Integer getDislikesNum() {
        return dislikesNum;
    }

    public void setDislikesNum(Integer dislikesNum) {
        this.dislikesNum = dislikesNum;
    }

    public List<Long> getLikeUser() {
        return likeUser;
    }

    public void setLikeUser(List<Long> likeUser) {
        this.likeUser = likeUser;
    }

    public List<Long> getDislikeUsers() {
        return dislikeUsers;
    }

    public void setDislikeUsers(List<Long> dislikeUsers) {
        this.dislikeUsers = dislikeUsers;
    }

    public String getName_ukrainian() {
        return name_ukrainian;
    }

    public void setName_ukrainian(String name_ukrainian) {
        this.name_ukrainian = name_ukrainian;
    }

    public String getDesc_ukrainian() {
        return desc_ukrainian;
    }

    public void setDesc_ukrainian(String desc_ukrainian) {
        this.desc_ukrainian = desc_ukrainian;
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
