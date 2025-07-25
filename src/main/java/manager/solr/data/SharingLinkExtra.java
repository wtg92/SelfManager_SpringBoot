package manager.solr.data;

import com.alibaba.fastjson2.JSON;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SharingLinkExtra implements Serializable {

    public List<String> fileIds = new ArrayList<>();

    public String trace = "";

    public static SharingLinkExtra analyze(String str){
        return JSON.parseObject(str,SharingLinkExtra.class);
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
