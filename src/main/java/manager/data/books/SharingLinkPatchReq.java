package manager.data.books;

import manager.solr.SolrFields;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SharingLinkPatchReq implements Serializable {

    public String id;
    public Boolean isCommunityLink;

    public Integer type;

    public String defaultLang;

    public String contentId;

    public Map<String, Object> getUpdateObj(){
        Map<String, Object> updatingAttrs  = new HashMap<>();
        updatingAttrs.put(SolrFields.TYPE,type);
        updatingAttrs.put(SolrFields.DEFAULT_LANG,defaultLang);
        updatingAttrs.put(SolrFields.CONTENT_ID,contentId);
        return updatingAttrs;
    }

    

}
