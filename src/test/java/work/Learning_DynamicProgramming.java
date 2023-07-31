package work;

import org.junit.Test;

public class Learning_DynamicProgramming {
	
	@Test
	public void test2() {
		
	}
	
	
    static class Counter{
        int start;
        int end;
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + end;
			result = prime * result + start;
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Counter other = (Counter) obj;
			if (end != other.end)
				return false;
			if (start != other.start)
				return false;
			return true;
		}
        
        
    }
	
	
	@Test
	public void test1() {
		int[] i = new int[] {1,5,3,4,6,9,7,8};
		System.out.println(maxLTS(i));
	}
	
	public int maxLTS(int[] s) {
		
		int max = 0;
		
		int[] rlt = new int[s.length];
		
		for(int i=0;i<s.length;i++) {
			rlt[i] = 1;
			for(int j=0;j<i;j++) {
				if(s[i]>s[j]
						&& rlt[i]<(rlt[j]+1)) {
					rlt[i] = rlt[j]+1;
					if(max < rlt[i]) {
						max = rlt[i];
					}
				}
			}
		}
		
		
		return max;
	}
	
	
}
