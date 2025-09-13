package manager.solr.data;

import manager.solr.data.SharingLinkReadPermIDUnit;

import java.io.Serializable;
import java.util.List;

public class SharingLinkReadPermission implements Serializable {

    public Boolean noLimit;
    public Boolean allUsers;
    public Boolean personal;
    public Boolean organization;
    public List<SharingLinkReadPermIDUnit> personalIds;
    public List<SharingLinkReadPermIDUnit> organizationIds;
}
