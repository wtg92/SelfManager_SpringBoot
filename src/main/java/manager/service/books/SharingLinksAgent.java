package manager.service.books;

import com.alibaba.fastjson2.JSON;
import manager.booster.SecurityBooster;
import manager.exception.LogicException;
import manager.solr.data.SharingLinkDetail;
import manager.solr.data.SharingLinkPatchReq;
import manager.solr.data.SharingLinkPermission;
import manager.solr.SolrFields;
import manager.solr.books.SharingLink;
import manager.system.SelfXErrors;
import manager.system.books.SharingLinkStatus;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/*
 * 该类的职能：
 * 1.解码 编码
 * 2.权限的处理 ---> 静态
 * 3.当请求一个页 或 一个列表时 经--->
 * 不包含的职能：
 * 1.数据库的操作 ---> 该层不引入缓存
 */
@Component
public class SharingLinksAgent {

    @Resource
    private SecurityBooster securityBooster;

    public static Map<String, Object> transferSolrUpdateParams(SharingLinkPatchReq req) {
        Map<String, Object> updatingAttrs = new HashMap<>();

        updatingAttrs.put(SolrFields.TYPE, req.type);
        updatingAttrs.put(SolrFields.DEFAULT_LANG, req.defaultLang);

        if (req.contentId != null) {
            updatingAttrs.put(SolrFields.CONTENT_ID, req.contentId);
        }

        updatingAttrs.put(SolrFields.PERMS, JSON.toJSONString(req.perms));

        if (req.multiLangFields != null && !req.multiLangFields.isEmpty()) {
            updatingAttrs.putAll(req.multiLangFields);
        }

        if(req.settings != null && !req.settings.isEmpty()){
            updatingAttrs.put(SolrFields.SETTINGS, req.settings);
        }

        return updatingAttrs;
    }


    /*
      loginId是有可能为Null的
       1.权限校验（读）
       2.
     */
    public void fill(SharingLinkDetail detail, @Nullable Long loginId, SharingLink link, SharingLinksAgent.EncryptionParams params) {
        checkPermission(loginId,link,params);
        detail.link = link;
    }

    public void checkPermission(@Nullable Long loginId, SharingLink link, SharingLinksAgent.EncryptionParams params){
        // 检查Status unexpected.
        if (!Objects.equals(link.getStatus(), SharingLinkStatus.PUBLIC)) {
            throw new LogicException(SelfXErrors.LINK_NONE_PUBLIC,link.getStatus());
        }
        checkReadPermission(loginId, params, link.getDecodedPerm());
    }

    /**
     * 1.1 如果是CommunityLink 则不受任何限制
     * 1.2 本人一定是有读的权限的
     * 1.3 Un limit
     *
     * @param loginId Nullable
     * @param params
     */
    private void checkReadPermission(@Nullable Long loginId, SharingLinksAgent.EncryptionParams params, SharingLinkPermission perms) {
        if (params.isCommunityLink) {
            return;
        }
        if (Objects.equals(loginId, params.loginId)) {
            return;
        }
        if (perms.readPerms.noLimit) {
            return;
        }
        if (perms.readPerms.allUsers) {
            if (loginId != null) {
                return;
            } else {
                throw new LogicException(SelfXErrors.LINK_READ_PERM_ERROR);
            }
        }
        if (perms.readPerms.personal) {
            if (loginId != null && perms.readPerms.personalIds.stream().anyMatch(id -> id.id.equals(securityBooster.encodeStableCommonId(loginId)))) {
                return;
            } else {
                throw new LogicException(SelfXErrors.LINK_READ_PERM_ERROR);
            }
        }

        throw new LogicException(SelfXErrors.LINK_READ_PERM_ERROR);
    }


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

    public EncryptionParams decode(String encoding) {
        return securityBooster.decodeSharingLinkURLParams(encoding);
    }
}
