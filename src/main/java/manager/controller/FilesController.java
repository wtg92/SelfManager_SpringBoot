package manager.controller;

import com.alibaba.fastjson2.JSONObject;
import manager.entity.general.books.SharingBook;
import manager.service.FilesService;
import manager.servlet.ServletAdapter;
import manager.util.UIUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import java.util.Map;

import static manager.system.SMParams.*;

@RestController
@RequestMapping("/files")
public class FilesController {

    @Resource
    private FilesService service;


    @PostMapping("/retrieveUploadURL")
    private Map<String,Object> retrieveUploadURL(@RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        Long sizeKB = param.getLong(SIZE_KB);
        String suffix = param.getString(SUFFIX);
        return service.retrieveUploadURL(loginId,sizeKB,suffix);
    }

    @PostMapping("/uploadDoneNotify")
    private void uploadDoneNotify( @RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        Long id = ServletAdapter.getCommonId(param.getString(ID)) ;
        service.uploadDoneNotify(loginId,id);
    }

    @GetMapping("/retrieveGetURL")
    private Map<String,Object> retrieveGetURL(@RequestHeader("Authorization") String authorizationHeader
            , @RequestParam(ID)String decodedID){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        Long id = ServletAdapter.getCommonId(decodedID) ;
        return service.retrieveGetURL(loginId,id);
    }

    @DeleteMapping("/fileRecord")
    private void deleteFileRecord(@RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        Long id = ServletAdapter.getCommonId(param.getString(ID));
        service.deleteFileRecord(loginId,id);
    }

}
