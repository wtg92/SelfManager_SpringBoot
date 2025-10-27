package manager.solr.data;

import java.io.Serializable;
import java.util.Map;

public class SharingLinkPatchReq implements Serializable {

    public String id;
    public Boolean isCommunityLink;

    public Integer type;

    public String defaultLang;

    public String contentId;

    public SharingLinkPermission perms;
    public Map<String, Object> multiLangFields;

    public String settings;

}
