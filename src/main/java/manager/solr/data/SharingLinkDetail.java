package manager.solr.data;

import java.io.Serializable;

public class SharingLinkDetail implements Serializable {

    public String encoding;


    public SharingLinkDetail(String encoding) {
        this.encoding = encoding;
    }
}
