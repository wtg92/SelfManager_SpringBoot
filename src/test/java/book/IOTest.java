package book;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

public class IOTest {
	
	public void testBasic() throws IOException {

		String str = "something....";
		byte[] dataForStr = str.getBytes();
		try (ByteArrayInputStream in = new ByteArrayInputStream(dataForStr)) {
			in.close();
		}
		
		
		
		Thread t = null;

		t.setPriority(0);

		FileInputStream in = null;

		FileOutputStream out = null;
		out.close();

		ByteArrayOutputStream outByte = null;
		outByte.close();
	}
	
	
	@Test
 	public void testString() throws IOException {
        final Charset UTF8 = Charset.forName("UTF-8");
        final Charset GBK = Charset.forName("GBK");

        try(ByteArrayOutputStream arrayOut = new ByteArrayOutputStream();
                Writer writer = new OutputStreamWriter(arrayOut,UTF8)){
            writer.write("test测试用字符串123");
            writer.flush();
            byte[] data = arrayOut.toByteArray();
            /*相同的编码集 test测试用字符串123*/
            System.out.println(new String(data,UTF8));
            /*不同的编码集 test娴嬭瘯鐢ㄥ瓧绗︿覆123*/
            System.out.println(new String(data,GBK));
        }
    }
 
 
 
	@Test
	public void detectFiles() throws Exception {
		final File target1 = new File("D:\\test", "target.rar");
		final File target2 = new File("D:\\test", "test.rar");

		try (FileInputStream in = new FileInputStream(target1);
				FileInputStream in2 = new FileInputStream(target2);) {

			int data;
			while ((data = in.read()) != -1) {
				int data2 = in2.read();
				if (data != data2) {
					System.out.println(data + ":" + data2);
				}
			}
		}
	}
 
    private static class CopyFileAsyncAttachment{
        ByteBuffer attachment;
        AsynchronousFileChannel target;
        int position;
        
        CopyFileAsyncAttachment(ByteBuffer attachment,AsynchronousFileChannel target,int position) {
            super();
            this.attachment = attachment;
            this.position = position;
            this.target = target;
        }
    }
    
    
    private static final CompletionHandler<Integer, CopyFileAsyncAttachment> ASYNC_FILE_CONTEXT =
            new CompletionHandler<Integer, CopyFileAsyncAttachment>() {

        @Override
        public void completed(Integer result, CopyFileAsyncAttachment attachment) {
            attachment.attachment.flip();
            attachment.target.write(attachment.attachment,attachment.position);
        }

        @Override
        public void failed(Throwable exc, CopyFileAsyncAttachment attachment) {
            throw new RuntimeException(exc);
        }
        
    };
	    
    @Test
    public void mayDestroyComputer() throws Exception{
        final File src = new File("D:\\test","target.rar");
        final File target1 = new File("D:\\test","test.rar");
        
        target1.delete();
        target1.createNewFile();
        
        final int bufSize = 10000;
        
        try(AsynchronousFileChannel srcChannel =
                AsynchronousFileChannel.open(src.toPath(),StandardOpenOption.READ);
                AsynchronousFileChannel trgChannel1 =
                        AsynchronousFileChannel.open(target1.toPath(), StandardOpenOption.WRITE);){
            
            long t1 = System.currentTimeMillis();
            
            long size = srcChannel.size();
            int groupNum = (int) (size/bufSize);
            int lastGroupNum = (int) (size%bufSize);
            
            for(int i=0;i<groupNum;i++) {
                ByteBuffer buf = ByteBuffer.allocate(bufSize);
                final int position = i*bufSize;
                CopyFileAsyncAttachment attachment = new CopyFileAsyncAttachment(buf,trgChannel1,position);
                srcChannel.read(buf,position,attachment,ASYNC_FILE_CONTEXT);
            }
            
            ByteBuffer buf = ByteBuffer.allocate(lastGroupNum);
            final int position = groupNum*bufSize;
            CopyFileAsyncAttachment contextAttachment = new CopyFileAsyncAttachment(buf,trgChannel1,position);
            srcChannel.read(buf,position,contextAttachment,ASYNC_FILE_CONTEXT);
            
            long t2 = System.currentTimeMillis();
            System.out.println("分片异步复制方法"+srcChannel.size()+"字节，耗时"+(t2-t1));
            
            
        }
 
    }  
	    
    
    @Test
    public void destroyComputerByNIO() throws Exception{
	    final File src = new File("D:\\test","target.rar");
	    final File target1 = new File("D:\\test","test.rar");
	    
	    target1.delete();
	    target1.createNewFile();
	    
	    final int bufSize = 10000;
	    AsynchronousFileChannel srcChannel = AsynchronousFileChannel.open(src.toPath(),StandardOpenOption.READ);
	    AsynchronousFileChannel trgChannel1 = AsynchronousFileChannel.open(target1.toPath(), StandardOpenOption.WRITE);
	        
	    long t1 = System.currentTimeMillis();
	    
        long size = srcChannel.size();
        int groupNum = (int) (size/bufSize);
        int lastGroupNum = (int) (size%bufSize);
        
        final CompletionHandler<Integer, CopyFileAsyncAttachment> ASYNC_FILE_CONTEXT =
                new CompletionHandler<Integer, CopyFileAsyncAttachment>() {
            @Override
            public void completed(Integer result, CopyFileAsyncAttachment attachment) {
                attachment.attachment.flip();
                attachment.target.write(attachment.attachment,attachment.position);
            }
            @Override
            public void failed(Throwable exc, CopyFileAsyncAttachment attachment) {
                throw new RuntimeException(exc);
            }
        };
        
        for(int i=0;i<groupNum;i++) {
            ByteBuffer buf = ByteBuffer.allocate(bufSize);
            final int position = i*bufSize;
            CopyFileAsyncAttachment attachment = new CopyFileAsyncAttachment(buf,trgChannel1,position);
            srcChannel.read(buf,position,attachment,ASYNC_FILE_CONTEXT);
        }
        
        ByteBuffer buf = ByteBuffer.allocate(lastGroupNum);
        final int position = groupNum*bufSize;
        CopyFileAsyncAttachment contextAttachment = new CopyFileAsyncAttachment(buf,trgChannel1,position);
        srcChannel.read(buf,position,contextAttachment,ASYNC_FILE_CONTEXT);
        
        long t2 = System.currentTimeMillis();
        
        System.out.println("分片异步复制方法"+srcChannel.size()+"字节，耗时"+(t2-t1));
        while(true) {
        	/*模拟应用的启动状态*/
        }
    }  
    
    
    
    @Test
    public void copyFileByAsyncNIO2() throws Exception{
	    final File src = new File("D:\\test","target.rar");
	    final File target1 = new File("D:\\test","test.rar");
	    
	    target1.delete();
	    target1.createNewFile();
	    
	    final int bufSize = 10000;
	    
	    AsynchronousFileChannel srcChannel = AsynchronousFileChannel
	    		.open(src.toPath(),StandardOpenOption.READ);
	    AsynchronousFileChannel trgChannel1 = AsynchronousFileChannel
	    		.open(target1.toPath(), StandardOpenOption.WRITE);
	        
	    long t1 = System.currentTimeMillis();
	        
        long size = srcChannel.size();
        int groupNum = (int) (size/bufSize);
        int lastGroupNum = (int) (size%bufSize);
        
        for(int i=0;i<groupNum;i++) {
            ByteBuffer buf = ByteBuffer.allocate(bufSize);
            final int position = i*bufSize;
            srcChannel.read(buf,position).get();
            buf.flip();
            trgChannel1.write(buf, position);
        }
        
        ByteBuffer buf = ByteBuffer.allocate(lastGroupNum);
        final int position = groupNum*bufSize;
        srcChannel.read(buf,position).get();
        buf.flip();
        trgChannel1.write(buf, position);
        
        long t2 = System.currentTimeMillis();
        System.out.println("分片异步复制方法"+srcChannel.size()+"字节，读操作耗时"+(t2-t1));
        while(true) {
        	/*模拟应用的启动状态*/
        }
	        
    }  
    
    
    @Test
    public void copyFileByAsyncNIO() throws Exception{
    	final File src = new File("D:\\test", "target.rar");
		final File target = new File("D:\\test", "test.rar");

		target.delete();
		target.createNewFile();

		final int bufSize = 10000;

		AsynchronousFileChannel srcChannel = AsynchronousFileChannel.open(src.toPath()
				, StandardOpenOption.READ);
		AsynchronousFileChannel trgChannel = AsynchronousFileChannel.open(target.toPath()
				, StandardOpenOption.WRITE);
		long t1 = System.currentTimeMillis();
		ByteBuffer buf = ByteBuffer.allocate(bufSize);
		
		copyFileUnitOperation(srcChannel, buf, 0, t1, trgChannel);
		
		long t2 = System.currentTimeMillis();
		System.out.println("异步复制方法" + srcChannel.size() + "字节，启动耗时" + (t2 - t1));
		while (true) {
			/*模拟应用的启动状态*/
		}
    }  
    
    private void copyFileUnitOperation(AsynchronousFileChannel srcChannel,ByteBuffer buf ,long position
    		,long startTIme, AsynchronousFileChannel trgChannel) {
        srcChannel.read(buf, position, startTIme, new CompletionHandler<Integer, Long>() {
        
        @Override
        public void completed(Integer result, Long attachment) {
            if(result != -1) {
                buf.flip();
                trgChannel.write(buf, position,attachment,new CompletionHandler<Integer, Long>(){

					@Override
					public void completed(Integer result, Long attachment) {
						 buf.clear();
						 copyFileUnitOperation(srcChannel, buf, position+result, attachment,trgChannel);
					}

					@Override
					public void failed(Throwable exc, Long attachment) {
						throw new RuntimeException(exc);
					}
                	
                });
            }else {
            	try {
					srcChannel.close();
					trgChannel.close();
				} catch (IOException e) {
					e.printStackTrace();
					throw new RuntimeException(e.getMessage());
				}
            	
            	System.out.println("复制完毕 复制耗时"+(System.currentTimeMillis()-attachment));
            }
        }

        @Override
        public void failed(Throwable exc, Long attachment) {
            throw new RuntimeException(exc);
        }
    });
    }
    
    
    
    @Test
    public void testCopyFileByNIOForCompare() throws Exception{
        final File src = new File("D:\\test","target.rar");
        final File target1 = new File("D:\\test","test.rar");
        
        target1.delete();
        target1.createNewFile();
        
        ByteBuffer buf = ByteBuffer.allocate(10000);

        try(FileChannel srcChannel =
                FileChannel.open(src.toPath(),StandardOpenOption.READ);
            FileChannel trgChannel1 =
                    FileChannel.open(target1.toPath(), StandardOpenOption.WRITE);
                ){
            
            long t1 = System.currentTimeMillis();
            while((srcChannel.read(buf))!=-1) {
                buf.flip();
                trgChannel1.write(buf);
                buf.clear();
            };
            
            long t2 = System.currentTimeMillis();
            System.out.println("同步复制"+srcChannel.size()+"字节，耗时"+(t2-t1));
        }
        
    }

	
    
    
    @Test
    public void testCopyFileByNIO() throws Exception{
        final File src = new File("D:\\test","target.docx");
        final File target1 = new File("D:\\test","target1.docx");
        final File target2 = new File("D:\\test","target2.docx");
        
        target1.delete();
        target2.delete();
        target1.createNewFile();
        target2.createNewFile();
        
        ByteBuffer buf = ByteBuffer.allocate(10000);

        try(FileChannel srcChannel =
                FileChannel.open(src.toPath(),StandardOpenOption.READ);
            FileChannel trgChannel1 =
                    FileChannel.open(target1.toPath(), StandardOpenOption.WRITE);
            FileChannel trgChannel2 =
                    FileChannel.open(target2.toPath(), StandardOpenOption.WRITE);
                
            AsynchronousFileChannel trg3 = AsynchronousFileChannel.open(target1.toPath(), StandardOpenOption.WRITE);
                ){
            
            long t1 = System.currentTimeMillis();
            
            while((srcChannel.read(buf))!=-1) {
                buf.flip();
                trgChannel1.write(buf);
                buf.position(0);
                trgChannel2.write(buf);
                buf.clear();
            };
            
            long t2 = System.currentTimeMillis();
            System.out.println("耗时"+(t2-t1));
        }
        
    }
    
    
    
    @Test
    public void testCopyFile() throws Exception{
        final File src = new File("D:\\test","test.docx");
        final File target1 = new File("D:\\test","target1.docx");
        final File target2 = new File("D:\\test","target2.docx");
        
        try(InputStream in = new BufferedInputStream
                (new FileInputStream(src),10000);
                OutputStream outForCopy1 = new BufferedOutputStream
                        (new FileOutputStream(target1),10000);
                OutputStream outForCopy2 = new BufferedOutputStream
                        (new FileOutputStream(target2),10000)){
            int b;
            while ((b = in.read()) != -1) {
                outForCopy1.write(b);
                outForCopy2.write(b);
            }
        }
    }
    
    
    @Test
    public void testIterator() {
        
        List<Byte> data = new ArrayList<Byte>();
        data.add((byte)1);
        data.add((byte)2);
        
        Iterator<Byte> iterator = data.iterator();
        
        int dataLikeIO;
        while(iterator.hasNext()) {
            dataLikeIO = iterator.next();
        }
        
    }
    
    
}
