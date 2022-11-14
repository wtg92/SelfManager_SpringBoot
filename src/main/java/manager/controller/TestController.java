package manager.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.*;

import manager.data.AjaxResult;
import manager.util.MediaVideoTransfer;

@RestController
@RequestMapping("/test")
public class TestController {
	
//	AtomicInteger sign = new AtomicInteger();
//    ConcurrentHashMap<Integer, String> pathMap = new ConcurrentHashMap<>();
//    ConcurrentHashMap<Integer, PipedOutputStream> outputStreamMap = new ConcurrentHashMap<>();
//    ConcurrentHashMap<Integer, PipedInputStream> inputStreamMap = new ConcurrentHashMap<>();
//
//    @PostMapping("/putVideo")
//    @ResponseBody
//    public AjaxResult putVideoPathByPost(String path) {
//        try {
//            int id = sign.getAndIncrement();
//            pathMap.put(id, path);
//            PipedOutputStream pipedOutputStream = new PipedOutputStream();
//            PipedInputStream pipedInputStream = new PipedInputStream();
//            pipedOutputStream.connect(pipedInputStream);
//            outputStreamMap.put(id, pipedOutputStream);
//            inputStreamMap.put(id, pipedInputStream);
//            return AjaxResult.success(id);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return AjaxResult.error();
//        }
//    }
//
//    @GetMapping("/putVideo")
//    public AjaxResult putVideoPath(@RequestParam("url") String path) {
//        try {
//            int id = sign.getAndIncrement();
//            pathMap.put(id, path);
//            PipedOutputStream pipedOutputStream = new PipedOutputStream();
//            PipedInputStream pipedInputStream = new PipedInputStream();
//            pipedOutputStream.connect(pipedInputStream);
//            outputStreamMap.put(id, pipedOutputStream);
//            inputStreamMap.put(id, pipedInputStream);
//            return AjaxResult.success(id);
//        } catch (Exception e) {
//        	e.printStackTrace();
//        	return AjaxResult.error();
//        }
//    }
//
//
//	@GetMapping("/test")
//	public void test() {
//		System.out.println("Hello World");
//	}
//
//	@GetMapping("/getVideo")
//    public void getVideo(HttpServletRequest request, HttpServletResponse response,@RequestParam("id") int id) {
//        String fileName = UUID.randomUUID().toString();
//        response.addHeader("Content-Disposition", "attachment;filename=" + fileName + ".flv");
//        try {
//            ServletOutputStream outputStream = response.getOutputStream();
//            write(id, outputStream);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//	private void write(int id, OutputStream outputStream) {
//        try {
//            String path = pathMap.get(id);
//            PipedOutputStream pipedOutputStream = outputStreamMap.get(id);
//            new Thread(() -> {
//                MediaVideoTransfer mediaVideoTransfer = new MediaVideoTransfer(pipedOutputStream, path, "udp");
//                mediaVideoTransfer.live();
//            }).start();
//
//            print(inputStreamMap.get(id), outputStream);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void print(InputStream inputStream, OutputStream outputStream) throws IOException {
//        byte[] buffer = new byte[1024];
//        int length;
//        while ((length = inputStream.read(buffer)) != -1) {
//            outputStream.write(buffer, 0, length);
//        }
//    }
	
}
