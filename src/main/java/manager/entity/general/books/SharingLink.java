package manager.entity.general.books;

import manager.entity.SMSolrDoc;
import org.apache.solr.client.solrj.beans.Field;

import java.util.List;

public class SharingLink extends SMSolrDoc {

    @Field
    private List<String> rules;

    @Field
    private Boolean isEnterCommunity;

    @Field
    private String bookId;

    @Field
    private Long copyTimes;

    @Field
    private Long watchingTimes;


}
