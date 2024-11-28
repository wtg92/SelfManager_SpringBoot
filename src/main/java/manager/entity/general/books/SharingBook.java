package manager.entity.general.books;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import manager.entity.SMSolrDoc;
import manager.system.SMDB;
import manager.system.career.BookStyle;
import org.apache.solr.client.solrj.beans.Field;

import java.util.ArrayList;
import java.util.List;

@Table(name = SMDB.E_SHARING_BOOK)
public class SharingBook extends SMSolrDoc {
    @Field
    private Integer status;

    @Field
    private Integer style;

    @Field
    private Integer displayPattern;

    @Field
    private Integer seqWeight;

    @Field
    private String defaultLang;

    @Field
    private String comment_ch;
    @Field
    private String comment_en;
    @Field
    private String comment_ja;



    @Field
    private String name_ch;
    @Field
    private String name_en;
    @Field
    private String name_ja;

    @Field
    private List<String> variables_ch;
    @Field
    private List<String> variables_en;
    @Field
    private List<String> variables_ja;

    public Integer getDisplayPattern() {
        return displayPattern;
    }

    public void setDisplayPattern(Integer displayPattern) {
        this.displayPattern = displayPattern;
    }

    public String getName_ch() {
        return name_ch;
    }

    public void setName_ch(String name_ch) {
        this.name_ch = name_ch;
    }

    public String getName_en() {
        return name_en;
    }

    public void setName_en(String name_en) {
        this.name_en = name_en;
    }

    public String getName_ja() {
        return name_ja;
    }

    public void setName_ja(String name_ja) {
        this.name_ja = name_ja;
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

    public String getComment_ch() {
        return comment_ch;
    }

    public void setComment_ch(String comment_ch) {
        this.comment_ch = comment_ch;
    }

    public String getComment_en() {
        return comment_en;
    }

    public void setComment_en(String comment_en) {
        this.comment_en = comment_en;
    }

    public String getComment_ja() {
        return comment_ja;
    }

    public void setComment_ja(String comment_ja) {
        this.comment_ja = comment_ja;
    }

    public List<String> getVariables_ch() {
        return variables_ch;
    }

    public void setVariables_ch(List<String> variables_ch) {
        this.variables_ch = variables_ch;
    }

    public List<String> getVariables_en() {
        return variables_en;
    }

    public void setVariables_en(List<String> variables_en) {
        this.variables_en = variables_en;
    }

    public List<String> getVariables_ja() {
        return variables_ja;
    }

    public void setVariables_ja(List<String> variables_ja) {
        this.variables_ja = variables_ja;
    }
}
