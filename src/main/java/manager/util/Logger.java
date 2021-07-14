package manager.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * TODO 迟早还是换回Java的Logger
 * @author 王天戈
 *
 */
public class Logger {
	
	class Accumulator{
		 private List<OneBuffer> buffers = new ArrayList<>();
		  
		  private int currentListIndex = 0;
		  
		  private static final int BUFFER_SIZE = 3000;
		  
		  public Accumulator() {
		    OneBuffer oneBuffer = new OneBuffer();
		    this.buffers.add(oneBuffer);
		  }
		  
		  public void add(long paramLong) {
		    OneBuffer oneBuffer = (OneBuffer)this.buffers.get(this.currentListIndex);
		    if (oneBuffer.isFull()) {
		      OneBuffer oneBuffer1 = new OneBuffer();
		      oneBuffer1.add(paramLong);
		      this.buffers.add(oneBuffer1);
		      this.currentListIndex++;
		    } else {
		      oneBuffer.add(paramLong);
		    } 
		  }
		  
		  public long[] getBuffer(int paramInt) {
		    assert paramInt <= this.currentListIndex;
		    OneBuffer oneBuffer = (OneBuffer)this.buffers.get(paramInt);
		    return oneBuffer.getBuffer();
		  }
		  
		  public List<OneBuffer> getBuffers() { return this.buffers; }
		  
		  public int getCurrentListIndex() { return this.currentListIndex; }
		  
		  class OneBuffer {
		    long[] buffer = null;
		    
		    private int currentAmount = 0;
		    
		    public OneBuffer() { this.buffer = new long[3000]; }
		    
		    public boolean isFull() { return (this.currentAmount == this.buffer.length); }
		    
		    public void add(long param1Long) {
		      this.buffer[this.currentAmount] = param1Long;
		      this.currentAmount++;
		    }
		    
		    public long[] getBuffer() {
		      if (this.currentAmount == this.buffer.length)
		        return this.buffer; 
		      long[] arrayOfLong = new long[this.currentAmount];
		      for (byte b = 0; b < arrayOfLong.length; b++)
		        arrayOfLong[b] = this.buffer[b]; 
		      return arrayOfLong;
		    }
		    
		    public int getCurrentAmount() { return this.currentAmount; }
		  }
	}
	
	
	  private static HashMap<String, Logger> instancePool = null;
	  
	  private String appName = "";
	  
	  private ResourceBundle properties = null;
	  
	  private static HashMap<String, Accumulator> accumulators = new HashMap<>();
	  
	  private Map<String, Boolean> enableInProperties = new HashMap<>();
	  
	  private Map<String, Boolean> hasKeyInProperties = new HashMap<>();
	  
	  public String getCallerMethodName(int paramInt) {
	    List list = (List)Arrays.stream(Thread.currentThread().getStackTrace()).filter(paramStackTraceElement -> paramStackTraceElement.getClassName().startsWith("com.")).filter(paramStackTraceElement -> !paramStackTraceElement.getMethodName().contains("lambda")).map(paramStackTraceElement -> paramStackTraceElement.getMethodName()).collect(Collectors.toList());
	    return (list.size() < paramInt + 1) ? ((list.size() > 0) ? (String)list.get(list.size() - 1) : "UnknownMethod") : (String)list.get(paramInt);
	  }
	  
	  public String getCallerMethodName() { return getCallerMethodName(2); }
	  
	  public String getCallerMethodNameWithFather() { return getCallerMethodName(3) + "." + getCallerMethodName(2); }
	  
		public static Logger getInstance(String key, String loggerProperties) {
			if (instancePool == null)
				instancePool = new HashMap<>();
			Logger logger = (Logger) instancePool.get(key);
			if (logger == null) {
				logger = new Logger();
				logger.appName = key;
				System.out.println("CREATE LOGGER FOR " + key);
				try {
					logger.properties = ResourceBundle.getBundle(loggerProperties);
				} catch (Exception exception) {
					System.err.println("[SystemError]No Logger setting found! propertiesName=" + loggerProperties);
				}
				instancePool.put(key, logger);
			}
			return logger;
		}
	  
	  public String getAppName() { return this.appName; }
	  
	  public void errorLog(String paramString) { 
		  System.err.println("[Error] " + getTimeTag() + "\n" + paramString + "\n");
		 }
	  
	  
	  public void errorLog(String paramString,String appendMes) { 
		errorLog(paramString+" "+appendMes); 
	  }
	  
	  public void errorLog(Exception paramException) { errorLog(getCallerMethodName(2) + ": " + paramException.getMessage()); }
	  
	  private boolean hasKeyInProperty(String paramString) {
	    if (this.properties == null) {
	      System.err.println("[SystemError]no resource");
	      return false;
	    } 
	    return this.properties.containsKey(paramString);
	  }
	  
	  private boolean hasKey(String paramString) {
	    if (!this.hasKeyInProperties.containsKey(paramString))
	      this.hasKeyInProperties.put(paramString, Boolean.valueOf(hasKeyInProperty(paramString))); 
	    return ((Boolean)this.hasKeyInProperties.get(paramString)).booleanValue();
	  }
	  
	  private boolean isEnable(String paramString) {
	    if (!this.enableInProperties.containsKey(paramString))
	      this.enableInProperties.put(paramString, Boolean.valueOf(isEnableInProperties(paramString))); 
	    return ((Boolean)this.enableInProperties.get(paramString)).booleanValue();
	  }
	  
	  private boolean isEnableInProperties(String paramString) {
	    if (this.properties == null) {
	      System.err.println("[SystemError]no resource");
	      return false;
	    } 
	    String str = this.properties.getString(paramString).trim();
	    if (str.equals("1"))
	      return true; 
	    if (str.equals("0"))
	      return false; 
	    System.err.println("[SystemError]Illegal Value(" + str + ") found in Logger Setting, Key=info");
	    return false;
	  }
	  
	  public void infoLog(String paramString) {
	    if (isEnable("info"))
	      System.out.println("[info]" + paramString); 
	  }
	  
	  public void warningLog(String paramString) {
	    if (isEnable("warning"))
	      System.out.println("[warning]" + paramString); 
	  }
	  
	  public void debugLog(String paramString) { debugLog(paramString, getCallerMethodName(3)); }
	  
	  public void debugLog(String paramString1, String paramString2) {
	    if (hasKey(paramString2)) {
	      if (isEnable(paramString2))
	        System.out.println("[debug:" + paramString2 + "]" + getTimeTag() + "\n" + paramString1 + "\n"); 
	    } else {
	      System.out.println("[debug:" + paramString2 + "]" + getTimeTag() + "\t\tKey Not Existed: " + paramString2 + "!\n" + paramString1 + "\n");
	    } 
	  }
	  
	  static String getTimeTag() {
	    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    return "[" + simpleDateFormat.format(new Date(System.currentTimeMillis())) + "]";
	  }
	  
	  public Stream<String> getAccumulators() { return accumulators.keySet().stream(); }
	  
	  public void clearAllAccumulators() { accumulators.clear(); }
	  
	  public void clearAccumulator(String paramString) {
	    if (accumulators.get(paramString) != null) {
	      accumulators.remove(paramString);
	      debugLog("clear key:" + paramString, "Accumulator");
	    } 
	  }
	  
	  public void accumulate(String paramString, long paramLong) {
	    if (accumulators.get(paramString) != null) {
	      Accumulator accumulator = (Accumulator)accumulators.get(paramString);
	      accumulator.add(paramLong);
	    } else {
	      Accumulator accumulator = new Accumulator();
	      accumulator.add(paramLong);
	      accumulators.put(paramString, accumulator);
	      debugLog("create key:" + paramString, "Accumulator");
	    } 
	  }
	  
	  public long getSumOfAccumulator(String paramString) {
	    if (accumulators.get(paramString) != null) {
	      Accumulator accumulator = (Accumulator)accumulators.get(paramString);
	      long l = 0L;
	      for (byte b = 0; b <= accumulator.getCurrentListIndex(); b++) {
	        long[] arrayOfLong = accumulator.getBuffer(b);
	        for (byte b1 = 0; b1 < arrayOfLong.length; b1++)
	          l += arrayOfLong[b1]; 
	      } 
	      return l;
	    } 
	    warningLog("[get sum]Accumulator(" + paramString + ") isn't existed.");
	    return 0L;
	  }
	  
	  public long getMaxOfAccumulator(String paramString) {
	    if (accumulators.get(paramString) != null) {
	      Accumulator accumulator = (Accumulator)accumulators.get(paramString);
	      if (accumulator.getCurrentListIndex() == 0 && accumulator.getBuffer(0).length == 0) {
	        warningLog("[get max]Accumulator(" + paramString + ") has no records.");
	        return -1L;
	      } 
	      long l = accumulator.getBuffer(0)[0];
	      for (byte b = 0; b <= accumulator.getCurrentListIndex(); b++) {
	        long[] arrayOfLong = accumulator.getBuffer(b);
	        for (byte b1 = 0; b1 < arrayOfLong.length; b1++) {
	          if (arrayOfLong[b1] > l)
	            l = arrayOfLong[b1]; 
	        } 
	      } 
	      return l;
	    } 
	    errorLog("[getMax]Accumulator(" + paramString + ") isn't existed.");
	    return -1L;
	  }
	  
	  public long getMinOfAccumulator(String paramString) {
	    if (accumulators.get(paramString) != null) {
	      Accumulator accumulator = (Accumulator)accumulators.get(paramString);
	      if (accumulator.getCurrentListIndex() == 0 && accumulator.getBuffer(0).length == 0) {
	        warningLog("[get min]Accumulator(" + paramString + ") has no records.");
	        return -1L;
	      } 
	      long l = accumulator.getBuffer(0)[0];
	      for (byte b = 0; b <= accumulator.getCurrentListIndex(); b++) {
	        long[] arrayOfLong = accumulator.getBuffer(b);
	        for (byte b1 = 0; b1 < arrayOfLong.length; b1++) {
	          if (arrayOfLong[b1] < l)
	            l = arrayOfLong[b1]; 
	        } 
	      } 
	      return l;
	    } 
	    errorLog("[getMin]Accumulator(" + paramString + ") isn't existed.");
	    return -1L;
	  }
	  
	  public long getCountOfAccumulator(String paramString) {
	    if (accumulators.get(paramString) != null) {
	      Accumulator accumulator = (Accumulator)accumulators.get(paramString);
	      long l = 0L;
	      for (byte b = 0; b <= accumulator.getCurrentListIndex(); b++) {
	        long[] arrayOfLong = accumulator.getBuffer(b);
	        l += arrayOfLong.length;
	      } 
	      return l;
	    } 
	    return 0L;
	  }
	  
	  public double getAverageOfAccumulator(String paramString) {
	    if (accumulators.get(paramString) != null) {
	      Accumulator accumulator = (Accumulator)accumulators.get(paramString);
	      if (accumulator.getCurrentListIndex() == 0 && accumulator.getBuffer(0).length == 0) {
	        warningLog("[get average]Accumulator(" + paramString + ") has no records.");
	        return 0.0D;
	      } 
	      long l1 = getSumOfAccumulator(paramString);
	      long l2 = getCountOfAccumulator(paramString);
	      return l1 / l2;
	    } 
	    errorLog("[getAvg]Accumulator(" + paramString + ") isn't existed.");
	    return -1.0D;
	  }
	  
	  public String printAccumulator(String paramString) {
	    if (accumulators.containsKey(paramString)) {
	      String rlt = "";
	      long l1 = getCountOfAccumulator(paramString);
	      long l2 = getSumOfAccumulator(paramString);
	      long l3 = getMaxOfAccumulator(paramString);
	      long l4 = getMinOfAccumulator(paramString);
	      double d = getAverageOfAccumulator(paramString);
	      rlt += "\n";
	      rlt +=  "===============================";
	      rlt += "\n";
	      rlt += "Accumulator: " + paramString;
	      rlt += "\n";
	      rlt += "sum: " + l2;
	      /*TODO 待处理*/
//	      rlt += "average: " + (null = null + " ms\t").format("%.2f", new Object[] { Double.valueOf(d) });
//	      rlt += " ms\n";
	      rlt += "count: " + l1;
	      rlt += "\t\t";
	      rlt += "max: " + l3;
	      rlt += " ms\t";
	      rlt +=  "min: " + l4;
	      rlt +=  " ms\n";
	      rlt +=  "===============================";
	      return rlt + "\n";
	    } 
	    return "Report: Accumulator(" + paramString + ") isn't existed.";
	  }
}
