package book;

import java.util.function.Supplier;

public abstract class AbstarctFather {
	
	
	abstract Object doSomethingSpecialToGetRlt();
	
	public void hugeLogic() {
		/* 
		  * 庞大的、需要复用的逻辑流程
		 * ....
		 * ....
		 * */
		
		Object rlt = doSomethingSpecialToGetRlt();
		
		/*
		 * 依赖于rlt的庞大的、需要复用的逻辑流程
		 */
		doSomething(rlt);
	}
	
	



	private void doSomething(Object rlt) {
		// TODO Auto-generated method stub
		
	}

	public void hugeLogicWithBetterAPI(Supplier<Object> supplier) {
		
		/* 
		  * 庞大的、需要复用的逻辑流程
		 * ....
		 * ....
		 * */
		
		Object rlt = supplier.get();
		
		/*
		 * 依赖于rlt的庞大的、需要复用的逻辑流程
		 */
		doSomething(rlt);
	}
	
}




