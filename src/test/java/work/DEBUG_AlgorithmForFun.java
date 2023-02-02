package work;

public class DEBUG_AlgorithmForFun {
	
	/**
	 * 整数 n 统计并返回各位数字都不同的数字x的个数，其中0<=x<10n
	 */
	
	public int calculate(int n) {
		int maxX = 10*n-1;
		int minX = 0;
		/**
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
	
}
