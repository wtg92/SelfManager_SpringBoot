package manager.util;

//import java.io.OutputStream;
//
//import org.bytedeco.ffmpeg.global.avcodec;
//import org.bytedeco.javacv.*;

public class MediaVideoTransfer {
	
//
//
//    private OutputStream outputStream;
//
//    private String rtspUrl;
//
//    private String rtspTransportType;
//
//    private FFmpegFrameGrabber grabber;
//
//    private FFmpegFrameRecorder recorder;
//
//    private boolean isStart = false;
//
//
//
//
//
//    public MediaVideoTransfer(OutputStream outputStream, String rtspUrl, String rtspTransportType) {
//		super();
//		this.outputStream = outputStream;
//		this.rtspUrl = rtspUrl;
//		this.rtspTransportType = rtspTransportType;
//	}
//
//	/**
//     * 开启获取rtsp流
//     */
//    public void live() {
//        System.out.println("连接rtsp：" + rtspUrl + ",开始创建grabber");
//        boolean isSuccess = createGrabber(rtspUrl);
//        if (isSuccess) {
//            System.out.println("创建grabber成功");
//        } else {
//            System.out.println("创建grabber失败");
//        }
//        startCameraPush();
//    }
//
//    /**
//     * 构造视频抓取器
//     *
//     * @param rtsp 拉流地址
//     * @return 创建成功与否
//     */
//    private boolean createGrabber(String rtsp) {
//        // 获取视频源
//        try {
//            grabber = FFmpegFrameGrabber.createDefault(rtsp);
//            grabber.setOption("rtsp_transport", rtspTransportType);
//            FFmpegLogCallback.set();
//            grabber.start();
//            isStart = true;
//            recorder = new FFmpegFrameRecorder(outputStream, grabber.getImageWidth(), grabber.getImageHeight(), grabber.getAudioChannels());
//            //avcodec.AV_CODEC_ID_H264  //AV_CODEC_ID_MPEG4
//            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
//            recorder.setFormat("flv");
////            recorder.setSampleRate(grabber.getSampleRate());
//            recorder.setAudioChannels(grabber.getAudioChannels());
//            recorder.setFrameRate(grabber.getFrameRate());
//
//            recorder.setSampleRate(11025);
//            return true;
//        } catch (FrameGrabber.Exception e) {
//            System.err.println("创建解析rtsp FFmpegFrameGrabber 失败");
//            System.err.println(e);
//            stop();
//            reset();
//            return false;
//        }
//    }
//
//    /**
//     * 推送图片（摄像机直播）
//     */
//    private void startCameraPush() {
//        if (grabber == null) {
//            System.out.println("重试连接rtsp：" + rtspUrl + ",开始创建grabber");
//            boolean isSuccess = createGrabber(rtspUrl);
//            if (isSuccess) {
//                System.out.println("创建grabber成功");
//            } else {
//                System.out.println("创建grabber失败");
//            }
//        }
//        try {
//            if (grabber != null) {
//                recorder.start();
//                Frame frame;
//                while (isStart && (frame = grabber.grabFrame()) != null) {
//                    recorder.setTimestamp(grabber.getTimestamp());
//                    recorder.record(frame);
//                }
//                stop();
//                reset();
//            }
//        } catch (FrameGrabber.Exception | RuntimeException | FrameRecorder.Exception e) {
//            System.err.println(e);
//            stop();
//            reset();
//        }
//    }
//
//    private void stop() {
//        try {
//            if (recorder != null) {
//                recorder.stop();
//                recorder.release();
//            }
//            if (grabber != null) {
//                grabber.stop();
//            }
//        } catch (Exception e) {
//            System.err.println(e);
//        }
//    }
//
//    private void reset() {
//        recorder = null;
//        grabber = null;
//        isStart = false;
//    }
}
