package manager.controller;

import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import manager.booster.CommonCipher;
import manager.booster.SecurityBooster;
import manager.entity.general.FileRecord;
import manager.service.FilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static manager.system.SelfXParams.ID;
import static manager.system.SelfXParams.SIZE_KB;
import static manager.system.SelfXParams.SRC_PARAMS;
import static manager.system.SelfXParams.SUFFIX;

@RestController
@RequestMapping("/files")
public class FilesController {

    @Autowired
    private FilesService service;

    @Autowired
    private SecurityBooster securityBooster;

    @Autowired
    private CommonCipher commonCipher;

    @PostMapping("/retrieveUploadURL")
    private Map<String,Object> retrieveUploadURL(HttpServletRequest request
            , @RequestBody JSONObject param ){
        long loginId = securityBooster.requireUserId(request);
        Long sizeKB = param.getLong(SIZE_KB);
        String suffix = param.getString(SUFFIX);
        String srcParams = param.getString(SRC_PARAMS);
        return service.retrieveUploadURL(loginId,sizeKB,suffix,srcParams);
    }

    @PostMapping("/uploadDoneNotify")
    private void uploadDoneNotify( HttpServletRequest request
            , @RequestBody JSONObject param ){
        long loginId = securityBooster.requireUserId(request);
        Long id = commonCipher.getStableCommonId(param.getString(ID)) ;
        service.uploadDoneNotify(loginId,id);
    }

    @GetMapping("/retrieveGetURL")
    private Map<String,Object> retrieveGetURL(HttpServletRequest request
            , @RequestParam(ID)String decodedID){
        Long loginId = securityBooster.requireOptionalUserId(request);
        Long id = commonCipher.getStableCommonId(decodedID) ;
        return service.retrieveGetURL(loginId,id);
    }

    @DeleteMapping("/fileRecord")
    private void deleteFileRecord(HttpServletRequest request
            , @RequestBody JSONObject param ){
        long loginId = securityBooster.requireUserId(request);
        Long id = commonCipher.getStableCommonId(param.getString(ID));
        service.deleteFileRecord(loginId,id);
    }

    @GetMapping("/record")
    private FileRecord getRecord(HttpServletRequest request
            , @RequestParam(ID)String encodedID){
        Long loginId = securityBooster.requireOptionalUserId(request);
        Long id = commonCipher.getStableCommonId(encodedID) ;
        return service.getRecord(loginId,id);
    }

}
