package manager.solr.data;

import com.alibaba.fastjson2.JSON;

import java.io.Serializable;

public class SharingLinkPermission implements Serializable {
     public SharingLinkReadPermission readPerms = new SharingLinkReadPermission();

     public static SharingLinkPermission analyze(String str){
          return JSON.parseObject(str, SharingLinkPermission.class);
     }

     @Override
     public String toString() {
          return JSON.toJSONString(this);
     }
}
