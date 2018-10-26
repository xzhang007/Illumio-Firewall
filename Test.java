/**
 * This class contains the main function
 * I implement it to run the test
 */
public class Test {

	public static void main(String[] args) {
		
		String inputFileName = "fw.csv";
		Firewall fw = new Firewall(inputFileName);
		System.out.println(fw.accept_packet("inbound", "tcp", 80, "192.168.1.2"));
		System.out.println(fw.accept_packet("inbound", "udp", 53, "192.168.2.1"));
		System.out.println(fw.accept_packet("outbound", "tcp", 10234, "192.168.10.11"));
		System.out.println(fw.accept_packet("inbound", "tcp", 81, "192.168.1.2"));
		System.out.println(fw.accept_packet("inbound", "udp", 24, "52.12.48.92"));
	}
}
