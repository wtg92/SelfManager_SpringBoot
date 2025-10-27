package manager.solr.data;

import manager.solr.books.SharingLink;

import java.io.Serializable;

public class SharingLinkDetail implements Serializable {

    public String encoding;

    public SharingLink link;

    public SharingLinkDetail(String encoding) {
        this.encoding = encoding;
    }
}
