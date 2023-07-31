package work;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

public class DEBUG_AlgorithmForFun {
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	class Task{
		String start;
		String end;
		int num;
	}
	
	
	
	
	public void testTasks(int num,List<Task> details) {
		
		
		Collections.sort(details,Comparator.comparing(one->{
			return manager.util.TimeUtil.parseTime(one.start);
		}));
		
		int max = 0;
		
		while(!details.isEmpty()) {
			Task task = details.get(0);
			
			details.remove(0);
		}
		
	}
	
	
	
	
	
	
	/**
	 * 整数 n 统计并返回各位数字都不同的数字x的个数，其中0<=x<10n
	 */
	
	public int calculate(int n) {
		int maxX = 10*n-1;
		int minX = 0;
		/**7
		 * n=1  有 11个数              
		 * n=2  有 101个数
		 * n=3  有 1001个数
		 * n=4  有 10001个数
		 * 
		 * So 这是一个数据结构 其实 n代表着几位数 代表着
		 * 
		 * 1            
		 * 1 1           1
		 * 1 1 1       1 + 10*1
		 */
		return 0;
	}
	
	
	public static void main(String[] args) {
		for(int n=0;n<10;n++) {
			System.out.println();
		}
	}
	
	@Test
	public void test2() {
		String s = " ";
		int lengthOfLongestSubstring = lengthOfLongestSubstring(s);
		System.out.println(lengthOfLongestSubstring);
	}
	
	
    public int lengthOfLongestSubstring(String s) {
    	   int max = 0;
           List<String> temp = new ArrayList<>();
           for(char cc:s.toCharArray()){
               String c = ""+cc+"";
               System.out.println(c);
               if(temp.contains(c)){
                   if(max<=temp.size()){
                       max = temp.size();
                   }
                   temp.clear();
                   temp.add(c);
                   continue;
               }
               temp.add(c);
           }

           return max;
    }
	
    
    private static void print(List<List<String>>  base) {
    	for(int i=0;i<base.size();i++) {
    		List<String> list = base.get(i);
    		for(int j=0;j<list.size();j++) {
    			System.out.println(list.get(j));
    		}
    		System.out.println("===END "+i+" ==");
    	}
    }
    
    /**
     * https://leetcode.cn/problems/zigzag-conversion/submissions/
     */
    public String convert(String s, int numRows) {
        List<List<String>> base = new ArrayList<>();
        char[] chars = s.toCharArray();

        int col = 0;
        int subP = 0;

        for(char c:chars){
            String string = String.valueOf(c);

            boolean isSingle = subP%(numRows-1) != 0;

            if(isSingle){
                col ++ ;
                subP ++ ;
                base.add(new ArrayList<String>());
            }else{
                subP = 0;
            }
            
            if(base.size()==col){
                base.add(new ArrayList<String>());
            }
            List<String> subBase = base.get(col);
            boolean isFinished = subBase.size() == numRows;
            if(!isSingle && isFinished){
                col ++ ;
                subP ++ ;
                subBase = new ArrayList<String>();
                base.add(subBase);
            }

            subBase.add(string);
        }
        
        print(base);
        
        int pointer=0;
        StringBuffer rlt = new StringBuffer();

        for(int i=0;i<numRows;i++){
            for(int j=0;j<base.size();j++){
                boolean isNotSingle = j%(numRows-1) == 0;
                List<String> subBase = base.get(j);
                if(isNotSingle){
                    pointer = 0;
                    if(subBase.size() > i){
                        rlt.append(subBase.get(i));
                    }
                }else{
                    pointer++;
                    if(pointer == numRows - i){
                        rlt.append(subBase.get(0));
                    }
                }
                
            }
        }

        return rlt.toString();

    }
    
    
    @Test
    public void test3() {
//		String lengthOfLongestSubstring = convert("PAYPALISHIRING"
//				,3);
//		System.out.println(lengthOfLongestSubstring);
    	String s = "babad";
    	System.out.println(longestPalindrome(s));
    }
    
    public String longestPalindrome(String s) {
    	  if(s.length() == 0
    	            || s.length() == 1){
    	            return s;
    	        }
    	        char[] base = s.toCharArray();
    	        int pos1 = 0;
    	        int pos2 = base.length-1;

    	        String max = "";

    	        while(pos1<pos2){
    	            while(pos1<pos2){
    	            	String target = s.substring(pos1,pos2+1);
    	                if(isRecursive(target) &&
    	                        target.length()>max.length()){
    	                    max = target;
    	                }
    	                pos2--;
    	            }
    	            pos2 = base.length-1;
    	            pos1++;
    	        }
    	        return max;
    }


    
    
    public static String getString(char[] base,int start,int end){
        StringBuffer rlt = new StringBuffer();
        for(int i=start;i<=end;i++){
            rlt.append(base[i]);
        }


        return rlt.toString();
    }

    public static boolean isRecursive(String s){
    	  int pos1 = 0;
          int pos2 = s.length()-1;

          char[] base = s.toCharArray();

          while(pos1 < pos2){
              if(base[pos1] != base[pos2]){
                  return false;
              }
              pos1++;
              pos2--;
          }
          return true;
    }
    
    @Test
    public void test6() {
    	HashMap<String, Object> r = new HashMap<String, Object>();
    }
    
}
