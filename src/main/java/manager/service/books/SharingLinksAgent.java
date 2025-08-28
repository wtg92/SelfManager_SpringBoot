package manager.service.books;

import manager.booster.SecurityBooster;
import manager.solr.books.SharingLink;
import org.checkerframework.checker.index.qual.PolyUpperBound;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;

@Component
public class SharingLinksAgent {

    @Resource
    private SecurityBooster securityBooster;


    public static class EncryptionParams implements Serializable {
        public String id;
        public long loginId;
        public String bookId;
        public Boolean isCommunityLink;
    }

    public String generateURL(String id, long loginId, String bookId, Boolean isCommunityLink) {
        EncryptionParams params = new EncryptionParams();
        params.id = id;
        params.loginId = loginId;
        params.bookId = bookId;
        params.isCommunityLink = isCommunityLink;
        return securityBooster.encodeSharingLinkURLParams(params);
    }
}
