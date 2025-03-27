package manager.entity.general.books;

import jakarta.persistence.Column;
import manager.entity.SMSolrDoc;
import org.apache.solr.client.solrj.beans.Field;

import java.util.List;

public class PageNode extends SMSolrDoc {

    @Field
    private String type;

    @Field
    private String bookId;

    @Field
    private List<String> parentIds;

    @Field
    private List<Double> indexes;

    @Field
    private Boolean withTODOs;
    @Field
    private Boolean isHidden;

    @Field
    private Integer childrenNum;

    @Field
    private String srcType ;
    @Field
    private String srcParams;

    /**
     * 变量还是需要有一个类型的
     * 这样为未来的扩展比较好
     * 并且设置语言
     */
    @Field
    private List<String> variables;

    @Field
    private List<String> fileIds;

    public List<String> getFileIds() {
        return fileIds;
    }

    public void setFileIds(List<String> fileIds) {
        this.fileIds = fileIds;
    }

    public String getSrcType() {
        return srcType;
    }

    public void setSrcType(String srcType) {
        this.srcType = srcType;
    }

    public String getSrcParams() {
        return srcParams;
    }

    public void setSrcParams(String srcParams) {
        this.srcParams = srcParams;
    }

    public Boolean getIsHidden() {
        return isHidden;
    }

    public void setIsHidden(Boolean hidden) {
        isHidden = hidden;
    }

    @Field private String editorState_arabic;
    @Field private String editorState_bengali;
    @Field private String editorState_brazilian_portuguese;
    @Field private String editorState_bulgarian;
    @Field private String editorState_catalan;
    @Field private String editorState_chinese;
    @Field private String editorState_traditional_chinese;
    @Field private String editorState_czech;
    @Field private String editorState_danish;
    @Field private String editorState_dutch;
    @Field private String editorState_estonian;
    @Field private String editorState_finnish;
    @Field private String editorState_french;
    @Field private String editorState_galician;
    @Field private String editorState_german;
    @Field private String editorState_greek;
    @Field private String editorState_hindi;
    @Field private String editorState_indonesian;
    @Field private String editorState_italian;
    @Field private String editorState_irish;
    @Field private String editorState_japanese;
    @Field private String editorState_korean;
    @Field private String editorState_english;
    @Field private String editorState_latvian;
    @Field private String editorState_norwegian;
    @Field private String editorState_persian;
    @Field private String editorState_polish;
    @Field private String editorState_portuguese;
    @Field private String editorState_romanian;
    @Field private String editorState_russian;
    @Field private String editorState_scandinavian;
    @Field private String editorState_serbian;
    @Field private String editorState_spanish;
    @Field private String editorState_swedish;
    @Field private String editorState_thai;
    @Field private String editorState_turkish;
    @Field private String editorState_ukrainian;

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

//    @Field private String name_ukrainian;
//    @Field private String content_ukrainian;

    @Override
    public PageNode clone(){
        return (PageNode) super.clone();
    }

    public Boolean getHidden() {
        return isHidden;
    }

    public void setHidden(Boolean hidden) {
        isHidden = hidden;
    }

    public String getEditorState_arabic() {
        return editorState_arabic;
    }

    public void setEditorState_arabic(String editorState_arabic) {
        this.editorState_arabic = editorState_arabic;
    }

    public String getEditorState_bengali() {
        return editorState_bengali;
    }

    public void setEditorState_bengali(String editorState_bengali) {
        this.editorState_bengali = editorState_bengali;
    }

    public String getEditorState_brazilian_portuguese() {
        return editorState_brazilian_portuguese;
    }

    public void setEditorState_brazilian_portuguese(String editorState_brazilian_portuguese) {
        this.editorState_brazilian_portuguese = editorState_brazilian_portuguese;
    }

    public String getEditorState_bulgarian() {
        return editorState_bulgarian;
    }

    public void setEditorState_bulgarian(String editorState_bulgarian) {
        this.editorState_bulgarian = editorState_bulgarian;
    }

    public String getEditorState_catalan() {
        return editorState_catalan;
    }

    public void setEditorState_catalan(String editorState_catalan) {
        this.editorState_catalan = editorState_catalan;
    }

    public String getEditorState_chinese() {
        return editorState_chinese;
    }

    public void setEditorState_chinese(String editorState_chinese) {
        this.editorState_chinese = editorState_chinese;
    }

    public String getEditorState_traditional_chinese() {
        return editorState_traditional_chinese;
    }

    public void setEditorState_traditional_chinese(String editorState_traditional_chinese) {
        this.editorState_traditional_chinese = editorState_traditional_chinese;
    }

    public String getEditorState_czech() {
        return editorState_czech;
    }

    public void setEditorState_czech(String editorState_czech) {
        this.editorState_czech = editorState_czech;
    }

    public String getEditorState_danish() {
        return editorState_danish;
    }

    public void setEditorState_danish(String editorState_danish) {
        this.editorState_danish = editorState_danish;
    }

    public String getEditorState_dutch() {
        return editorState_dutch;
    }

    public void setEditorState_dutch(String editorState_dutch) {
        this.editorState_dutch = editorState_dutch;
    }

    public String getEditorState_estonian() {
        return editorState_estonian;
    }

    public void setEditorState_estonian(String editorState_estonian) {
        this.editorState_estonian = editorState_estonian;
    }

    public String getEditorState_finnish() {
        return editorState_finnish;
    }

    public void setEditorState_finnish(String editorState_finnish) {
        this.editorState_finnish = editorState_finnish;
    }

    public String getEditorState_french() {
        return editorState_french;
    }

    public void setEditorState_french(String editorState_french) {
        this.editorState_french = editorState_french;
    }

    public String getEditorState_galician() {
        return editorState_galician;
    }

    public void setEditorState_galician(String editorState_galician) {
        this.editorState_galician = editorState_galician;
    }

    public String getEditorState_german() {
        return editorState_german;
    }

    public void setEditorState_german(String editorState_german) {
        this.editorState_german = editorState_german;
    }

    public String getEditorState_greek() {
        return editorState_greek;
    }

    public void setEditorState_greek(String editorState_greek) {
        this.editorState_greek = editorState_greek;
    }

    public String getEditorState_hindi() {
        return editorState_hindi;
    }

    public void setEditorState_hindi(String editorState_hindi) {
        this.editorState_hindi = editorState_hindi;
    }

    public String getEditorState_indonesian() {
        return editorState_indonesian;
    }

    public void setEditorState_indonesian(String editorState_indonesian) {
        this.editorState_indonesian = editorState_indonesian;
    }

    public String getEditorState_italian() {
        return editorState_italian;
    }

    public void setEditorState_italian(String editorState_italian) {
        this.editorState_italian = editorState_italian;
    }

    public String getEditorState_irish() {
        return editorState_irish;
    }

    public void setEditorState_irish(String editorState_irish) {
        this.editorState_irish = editorState_irish;
    }

    public String getEditorState_japanese() {
        return editorState_japanese;
    }

    public void setEditorState_japanese(String editorState_japanese) {
        this.editorState_japanese = editorState_japanese;
    }

    public String getEditorState_korean() {
        return editorState_korean;
    }

    public void setEditorState_korean(String editorState_korean) {
        this.editorState_korean = editorState_korean;
    }

    public String getEditorState_english() {
        return editorState_english;
    }

    public void setEditorState_english(String editorState_english) {
        this.editorState_english = editorState_english;
    }

    public String getEditorState_latvian() {
        return editorState_latvian;
    }

    public void setEditorState_latvian(String editorState_latvian) {
        this.editorState_latvian = editorState_latvian;
    }

    public String getEditorState_norwegian() {
        return editorState_norwegian;
    }

    public void setEditorState_norwegian(String editorState_norwegian) {
        this.editorState_norwegian = editorState_norwegian;
    }

    public String getEditorState_persian() {
        return editorState_persian;
    }

    public void setEditorState_persian(String editorState_persian) {
        this.editorState_persian = editorState_persian;
    }

    public String getEditorState_polish() {
        return editorState_polish;
    }

    public void setEditorState_polish(String editorState_polish) {
        this.editorState_polish = editorState_polish;
    }

    public String getEditorState_portuguese() {
        return editorState_portuguese;
    }

    public void setEditorState_portuguese(String editorState_portuguese) {
        this.editorState_portuguese = editorState_portuguese;
    }

    public String getEditorState_romanian() {
        return editorState_romanian;
    }

    public void setEditorState_romanian(String editorState_romanian) {
        this.editorState_romanian = editorState_romanian;
    }

    public String getEditorState_russian() {
        return editorState_russian;
    }

    public void setEditorState_russian(String editorState_russian) {
        this.editorState_russian = editorState_russian;
    }

    public String getEditorState_scandinavian() {
        return editorState_scandinavian;
    }

    public void setEditorState_scandinavian(String editorState_scandinavian) {
        this.editorState_scandinavian = editorState_scandinavian;
    }

    public String getEditorState_serbian() {
        return editorState_serbian;
    }

    public void setEditorState_serbian(String editorState_serbian) {
        this.editorState_serbian = editorState_serbian;
    }

    public String getEditorState_spanish() {
        return editorState_spanish;
    }

    public void setEditorState_spanish(String editorState_spanish) {
        this.editorState_spanish = editorState_spanish;
    }

    public String getEditorState_swedish() {
        return editorState_swedish;
    }

    public void setEditorState_swedish(String editorState_swedish) {
        this.editorState_swedish = editorState_swedish;
    }

    public String getEditorState_thai() {
        return editorState_thai;
    }

    public void setEditorState_thai(String editorState_thai) {
        this.editorState_thai = editorState_thai;
    }

    public String getEditorState_turkish() {
        return editorState_turkish;
    }

    public void setEditorState_turkish(String editorState_turkish) {
        this.editorState_turkish = editorState_turkish;
    }

    public String getEditorState_ukrainian() {
        return editorState_ukrainian;
    }

    public void setEditorState_ukrainian(String editorState_ukrainian) {
        this.editorState_ukrainian = editorState_ukrainian;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getWithTODOs() {
        return withTODOs;
    }

    public void setWithTODOs(Boolean withTODOs) {
        this.withTODOs = withTODOs;
    }

    public Integer getChildrenNum() {
        return childrenNum;
    }

    public void setChildrenNum(Integer childrenNum) {
        this.childrenNum = childrenNum;
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

//    public String getName_ukrainian() {
//        return name_ukrainian;
//    }
//
//    public void setName_ukrainian(String name_ukrainian) {
//        this.name_ukrainian = name_ukrainian;
//    }
//
//    public String getContent_ukrainian() {
//        return content_ukrainian;
//    }
//
//    public void setContent_ukrainian(String content_ukrainian) {
//        this.content_ukrainian = content_ukrainian;
//    }

    public List<String> getParentIds() {
        return parentIds;
    }

    public void setParentIds(List<String> parentIds) {
        this.parentIds = parentIds;
    }

    public List<Double> getIndexes() {
        return indexes;
    }

    public void setIndexes(List<Double> indexes) {
        this.indexes = indexes;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }


}
