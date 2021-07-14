package work;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;

public class DEBUG_Net {
	
	
	public static void main(String[] args) {
		System.out.println(args.length);
		for(String s:args) {
			System.out.println(s);
		}
	}
	
	 
	@Test
	public void test() throws UnknownHostException {
		InetAddress address = InetAddress.getByName("172.17.8.10");
		System.out.println(address);
		System.out.println(InetAddress.getAllByName("managerwtg.com").length);
	}
	
	
}
