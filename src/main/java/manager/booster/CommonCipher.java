package manager.booster;

import com.alibaba.fastjson2.JSON;
import manager.exception.LogicException;
import manager.service.books.SharingLinksAgent;
import manager.system.SelfXErrors;
import manager.util.SecurityBasis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Unstable means different at every time server reboots
 * 关于CommonId:
 * 1。由于我只是想掩盖数字的信息 所以它一定是long
 * 2. 所谓CommonId unstable的话 处理的是 不会被实际使用的 只是想加密一下请求间的数据  stable 是要被存起来的
 *
 *  能unstable的尽量unstable
 *  stable相对unstable 开发上更不容易出错
 *
 * 这里列举所有CommonId stable/unstable 理由
 * 1. getLoginId -- Stable  -- 更改了鉴权机制后这个一直有就可以了
 * 2. FileRecord -- Stable -- 1.用户头像 直接存id 因此两者都行  2.共享页 需要直接存在state里 因此必须stable
 * 3. UserBasicInfo.ID -- Stable -- 1.由于用户看见，变化不好 2.共享Link 存储的是加密后的userId 因此必须stable
 * 4. UserBasicInfo.头像ID -- Stable -- 1.本质是fileId file是Stable 因此这里是stable
 * 5. SharingBook最后更新人 -- Unstable -- TODO 这个还未使用 但将来提供一个接口 暂定unstable
 * 6. PlanId -- Stable -- 1.用户直接使用，变化不好
 *
 */
@Component
public class CommonCipher {

    @Autowired
    SecurityBasis basis;

    public String encodeSharingLinkURLParams(SharingLinksAgent.EncryptionParams params){
        String jsonString = JSON.toJSONString(params);
        return basis.encodeStableInfo(jsonString);
    }

    public SharingLinksAgent.EncryptionParams decodeSharingLinkURLParams(String params){
        try{
            String s = basis.decodeStableInfo(params);
            return JSON.parseObject(s,SharingLinksAgent.EncryptionParams.class);
        }catch (Exception e){
            throw new LogicException(SelfXErrors.UNEXPECTED_ERROR);
        }
    }

    public static long getUnstableCommonId(String code) throws LogicException {
        try {
            return Long.parseLong(SecurityBasis.decodeUnstableInfo(code));
        } catch (Exception e) {
            throw new LogicException(SelfXErrors.ILLEGAL_CODE);
        }
    }

    public static String encodeUnstableCommonId(Object code) throws LogicException {
        return SecurityBasis.encodeUnstableInfo(code);
    }

    public long getStableCommonId(String code) throws LogicException {
        try {
            return Long.parseLong(basis.decodeStableInfo(code));
        } catch (Exception e) {
            e.printStackTrace();
            throw new LogicException(SelfXErrors.ILLEGAL_CODE);
        }
    }

    public String encodeStableCommonId(Object code) throws LogicException {
        return basis.encodeStableInfo(code);
    }
}
