package manager.solr.data;

import java.io.Serializable;

public class SharingLinkPatchReq implements Serializable {

    public String id;
    public Boolean isCommunityLink;

    public Integer type;

    public String defaultLang;

    public String contentId;

    public SharingLinkPermission perms;
    
}
