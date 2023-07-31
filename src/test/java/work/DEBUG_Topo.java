package work;

import java.util.LinkedList;
import java.util.Queue;

import org.junit.Test;

public class DEBUG_Topo {
	
	@Test
	public void test1() {
		int numCourses=2;
		int[][] prerequisites = new int[][] {{1,0}} ;
		
		System.out.println(canFinish(numCourses,prerequisites));
	}
	
	
	public boolean canFinish(int numCourses, int[][] prerequisites) {
        
        int[] rlt = new int[numCourses];

        for(int i=0;i<prerequisites.length;i++){
            int cur = prerequisites[i][0];
            int pre = prerequisites[i][1];
            rlt[cur] ++;
        }
        
        
        Queue<Integer> qu = new LinkedList<Integer>();

        return findZeroIndex(rlt,prerequisites,qu);
    }

    public boolean findZeroIndex(int[] rlt,int[][] prerequisites,Queue<Integer> qu){

        boolean flag = true;
        for(int i=0;i<rlt.length;i++){
            if(rlt[i]==0){
                qu.add(i);
            }else {
            	flag = false;
            }
        }
        if(flag) {
        	return true;
        }
        
        if(qu.size() == 0){
            return false;
        }
        int targetNum = qu.poll();
        for(int i=0;i<prerequisites.length;i++){
            if(prerequisites[i][1] == targetNum){
            	int cur = prerequisites[i][0];
                rlt[cur] --;
            }
        }
        return findZeroIndex(rlt,prerequisites,qu);

    }

	
}
