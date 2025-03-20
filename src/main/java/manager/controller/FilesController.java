package manager.controller;

import com.alibaba.fastjson2.JSONObject;
import manager.entity.general.FileRecord;
import manager.service.FilesService;
import manager.booster.SecurityBooster;
import manager.util.UIUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import java.util.Map;

import static manager.system.SelfXParams.*;

@RestController
@RequestMapping("/files")
public class FilesController {

    @Resource
    private FilesService service;

    @Resource
    private SecurityBooster securityBooster;

    @PostMapping("/retrieveUploadURL")
    private Map<String,Object> retrieveUploadURL(@RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        Long sizeKB = param.getLong(SIZE_KB);
        String suffix = param.getString(SUFFIX);
        String srcParams = param.getString(SRC_PARAMS);
        return service.retrieveUploadURL(loginId,sizeKB,suffix,srcParams);
    }

    @PostMapping("/uploadDoneNotify")
    private void uploadDoneNotify( @RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        Long id = securityBooster.getStableCommonId(param.getString(ID)) ;
        service.uploadDoneNotify(loginId,id);
    }

    @GetMapping("/retrieveGetURL")
    private Map<String,Object> retrieveGetURL(@RequestHeader(name="Authorization", required = false) String authorizationHeader
            , @RequestParam(ID)String decodedID){
        long loginId = authorizationHeader == null ? 0 : UIUtil.getLoginId(authorizationHeader);
        Long id = securityBooster.getStableCommonId(decodedID) ;
        return service.retrieveGetURL(loginId,id);
    }

    @DeleteMapping("/fileRecord")
    private void deleteFileRecord(@RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        Long id = securityBooster.getStableCommonId(param.getString(ID));
        service.deleteFileRecord(loginId,id);
    }

    @GetMapping("/record")
    private FileRecord getRecord(@RequestHeader(name="Authorization", required = false) String authorizationHeader
            , @RequestParam(ID)String decodedID){
        long loginId = authorizationHeader == null ? 0 : UIUtil.getLoginId(authorizationHeader);
        Long id = securityBooster.getStableCommonId(decodedID) ;
        return service.getRecord(loginId,id);
    }

}
