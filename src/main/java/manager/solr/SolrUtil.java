package manager.solr;

import manager.solr.constants.CustomProcessors;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;

public abstract class SolrUtil {

    /**
     * Bad Design Of SolrJ causes this.
     */
    public static DocumentObjectBinder binder = new DocumentObjectBinder();

    public static String getMultipleFieldParam(String ...params){
        return String.join(",", params);
    }

    public static String getAutoGenerateIdConfig(){
        return getMultipleFieldParam(CustomProcessors.IGNORE_ID, CustomProcessors.AUTO_GENERATE_ID);
    }
}
