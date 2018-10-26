import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

/**
 * This class is the Firewall class, which is designed by Illumio Coding Assignment
 * 
 * It has two public method: constructor, to read the input file which contains the rules,
 * And accept_packet method, to check if an input is acceptable or blocked.
 * 
 * I use 4 hashmap to storage the 4 rules, for each map, the key is the portNumber,
 * the value is the set of IP address in that port number
 * .
 * The algorithm in this class is brute force, the Tricky point for this problem is how to get different IPs 
 * from an IP range, in the worst case in my class, e.g. 1.2.3.4-192.168.5.7, there will be 4 nested for-loop,
 * the time complexity will be 255 ^ 4 ~ 4 billion!
 * 
 * If I have enough time, I will change the whole algorithm using a data structure: Interval Tree. 
 */
public class Firewall {
	
	private SourceReader sr = null;
	private Map<Integer, Set<String>> inboundTcpRules = null;
	private Map<Integer, Set<String>> outboundTcpRules = null;
	private Map<Integer, Set<String>> inboundUdpRules = null;
	private Map<Integer, Set<String>> outboundUdpRules = null;
	
	/**
	 * Construct a new Firewall class
	 * in this method, it will call SourceReader class to read the file line by line
	 * and for each line, it will call a series of private method to help parse the rules
	 * and when it finish, it will add each port number and ip address to the associate rules map
	 * @param inputFileName is the input file name which contains all the rules
	 */
	public Firewall(String inputFileName) {
		sr = new SourceReader(inputFileName);
		inboundTcpRules = new HashMap<Integer, Set<String>>();
		outboundTcpRules = new HashMap<Integer, Set<String>>();
		inboundUdpRules = new HashMap<Integer, Set<String>>();
		outboundUdpRules = new HashMap<Integer, Set<String>>();
		
		String line = sr.readLine();
		while (line != null) {
			parseInputLine(line);
			
			line = sr.readLine();
		}
		
		sr.closeReaders();
	}
	
	/**
	 * This method is check if an input will be acceptable or blocked based on the rules
	 * @param direction
	 * @param protocol
	 * @param port is one integer number
	 * @param ip_address is one string only contains one ip address
	 * @return true or false the input will be acceptable or blocked based on the rules
	 */
	public boolean accept_packet(String direction, String protocol, int port, String ip_address) {
		Map<Integer, Set<String>> rules = rulesSelection(direction, protocol);
		
		try {
			/* The rules does not cover such combination of direction and protocol
			 * or the input rule file is not an complete file,
			 * it does not contain all the 4 combination of direction and protocol
			 */
			if (rules.isEmpty()) {  
				throw new NoRuleException();
			}
		} catch (NoRuleException e) {
			e.printStackTrace();
		}
		
		if (!rules.containsKey(port)) {  // the associate rule does not contain the port number
			return false;
		}
		
		Set<String> ips = rules.get(port);
		if (ips.contains(ip_address)) {  // in the associate port number, it contains the ip_address
			return true;
		}
		
		return false;
	}
	
	/**
	 * This method is to parse the line into 4 parts: direction, protocol, ports and IPs
	 * @param line is the current line we read
	 */
	private void parseInputLine(String line) {
		String[] input = line.split(",");
		String direction = input[0];
		String protocol = input[1];
		String ports = input[2];
		String IPs = input[3];
		dealWithPorts(direction, protocol, ports, IPs);
	}
	
	/**
	 * This method is to check if in the "ports" part, it is a range
	 * If it is a range, we will traverse all the integer between that range
	 * @param direction
	 * @param protocol
	 * @param ports may be a range, or may be one port string
	 * @param IPs may be a range, or may be one IP string
	 */
	private void dealWithPorts(String direction, String protocol, String ports, String IPs) {
		if (ports.contains("-")) {
			String[] portArr = ports.split("-");
			int portStart = Integer.parseInt(portArr[0]);
			int portEnd = Integer.parseInt(portArr[1]);
			
			for (int port = portStart; port <= portEnd; port++) {
				dealWithIPs(direction, protocol, port, IPs);
			}
		} else {
			dealWithIPs(direction, protocol, Integer.parseInt(ports), IPs);
		}
	}
	
	/**
	 * This method is to check the "IPs" part, if the IP is a range
	 * If it is a range, we will seperate the start IP and end IP
	 * generate all the IPs between them, and traverse all IPs to add to the rules map
	 * @param direction
	 * @param protocol
	 * @param portNumber is an integer, not a range
	 * @param IPs may be a range, or maybe an IP string
	 */
	private void dealWithIPs(String direction, String protocol, int portNumber, String IPs) {
		if (IPs.contains("-")) {
			String[] IPsArr = IPs.split("-");
			String ipStart = IPsArr[0];
			String ipEnd = IPsArr[1];
			List<String> IPRanges = generateIPs(ipStart, ipEnd);
			
			for (String IP : IPRanges) {
				addToRules(direction, protocol, portNumber, IP);
			}
			
		} else {
			addToRules(direction, protocol, portNumber, IPs);
		}
	}
	
	/**
	 * This method is to add one port number and one IP address to the associate rules map
	 * @param direction
	 * @param protocol
	 * @param portNumber is an integer
	 * @param IP is a string which only contains one IP address
	 */
	private void addToRules(String direction, String protocol, int portNumber, String IP) {
		Map<Integer, Set<String>> rules = rulesSelection(direction, protocol);
		
		if (rules.containsKey(portNumber)) {
			Set<String> IPs = rules.get(portNumber);
			IPs.add(IP);
		} else {
			Set<String> IPs = new HashSet<String>();
			IPs.add(IP);
			rules.put(portNumber, IPs);
		}
	}
	
	/*
	 * This method is to get the associate rules map based on the direction and protocol
	 * @param direction
	 * @param protocol
	 * @return the associate rules map
	 */
	private Map<Integer, Set<String>> rulesSelection(String direction, String protocol) {
		Map<Integer, Set<String>> rules = null;
		if (direction.equals("inbound") & protocol.equals("tcp")) {
			rules = inboundTcpRules;
		} else if (direction.equals("outbound") & protocol.equals("tcp")) {
			rules = outboundTcpRules;
		} else if (direction.equals("inbound") & protocol.equals("udp")) {
			rules = inboundUdpRules;
		} else {  // direction.equals("outbound") & protocol.equals("udp")
			rules = outboundUdpRules;
		}
		
		return rules;
	}
	
	/**
	 * This method is the tricky point of the problem
	 * It is to generate all IPs based on an IP range
	 * The diff variable means at which integer, the start IP are different from the end IP.
	 * @param ipStart is the start IP
	 * @param ipEnd is the end IP
	 * @return A List of IP strings
	 */
	private List<String> generateIPs(String ipStart, String ipEnd) {
		List<String> ipList = new ArrayList<>();
		
		String[] arr1 = ipStart.split("\\.");  // important split on "\\." not "."
		int[] ip1 = new int[4];
		for (int i = 0; i < 4; i++) {
			ip1[i] = Integer.parseInt(arr1[i]);
		}
		
		String[] arr2 = ipEnd.split("\\.");  // important split on "\\." not "."
		int[] ip2 = new int[4];
		for (int i = 0; i < 4; i++) {
			ip2[i] = Integer.parseInt(arr2[i]);
		}
		
		int diff = -1;
		for (int i = 0; i < 4; i++) {
			if (ip1[i] == ip2[i]) {
				continue;
			}
			
			diff = i;
			break;
		}
		
		if (diff == 3) { // e.g 192.168.1.8-192.168.1.168
			diffIntegerAtPosition3(ip1, ip2, ipList);
			return ipList;
		}
		
		if (diff == 2) {  // e.g 192.168.1.1-192.168.3.9
			diffIntegerAtPosition2(ip1, ip2, ipList);
			return ipList;
		}
		
		if (diff == 1) {  // e.g 192.168.1.8-192.170.3.9
			diffIntegerAtPosition1(ip1, ip2, ipList);
			return ipList;
		}
		
		if (diff == 0) {  // e.g 1.2.3.4-192.168.5.7
			diffIntegerAtPosition0(ip1, ip2, ipList);
			return ipList;
		}
		
		return ipList;
	}
	
	/*
	 * deal with IP range like 192.168.1.8-192.168.1.168
	 * @param ip1 the integer array for 192.168.1.8
	 * @param ip2 the integer array for 192.168.1.168
	 */
	private void diffIntegerAtPosition3(int[] ip1, int[] ip2, List<String> ipList) {
		// 192.168.1.8-192.168.1.168
		for (int i = ip1[3]; i <= ip2[3]; i++) {
			ipList.add(new String("" + ip1[0] + "." + ip1[1] + "." + ip1[2] + "." + i));
		}
	}
	
	/*
	 * deal with IP range like 192.168.1.1-192.168.3.9
	 * @param ip1 the integer array for 192.168.1.1
	 * @param ip2 the integer array for 192.168.3.9
	 */
	private void diffIntegerAtPosition2(int[] ip1, int[] ip2, List<String> ipList) {
		// 192.168.1.1-192.168.1.255
		for (int i = ip1[3]; i <= 255; i++) {
			ipList.add(new String("" + ip1[0] + "." + ip1[1] + "." + ip1[2] + "." + i));
		}
					
		// 192.168.2.0-192.168.2.255
		for (int i = ip1[2] + 1; i <= ip2[2] - 1; i++) {
			for (int j = 0; j <= 255; j++) {
				ipList.add(new String("" + ip1[0] + "." + ip1[1] + "." + i + "." + j));
			}
		}
					
		// 192.168.3.0-192.168.3.9
		for (int i = 0; i <= ip2[3]; i++ ) {
			ipList.add(new String("" + ip1[0] + "."  + ip1[1] + "." + ip2[2] + "." + i));
		}
	}
	
	/*
	 * deal with IP range like 192.168.1.8-192.170.3.9
	 * @param ip1 the integer array for 192.168.1.8
	 * @param ip2 the integer array for 192.170.3.9
	 */
	private void diffIntegerAtPosition1(int[] ip1, int[] ip2, List<String> ipList) {
		// 192.168.1.8-192.168.1.255
		for (int i = ip1[3]; i <= 255; i++) {
			ipList.add(new String("" + ip1[0] + "." + ip1[1] + "." + ip1[2] + "." + i));
		}
		
		// 192.168.2.0-192.168.255.255
		for (int i = ip1[2] + 1; i <= 255; i++) {
			for (int j = 0; j <= 255; j++) {
				ipList.add(new String("" + ip1[0] + "." + ip1[1] + "." + i + "." + j));
			}
		}
		
		// 192.169.0.0-192.169.255.255
		for (int i = ip1[1] + 1; i <= ip2[1] - 1; i++ ) {
			for (int j = 0; j <= 255; j++) {
				for (int k = 0; k <= 255; k++) {
					ipList.add(new String("" + ip1[0] + "." + i + "." + j + "." + k));
				}
			}
		}
		
		// 192.170.0.0-192.170.2.255
		for (int i = 0; i <= ip2[2] - 1; i++) {
			for (int j = 0; j<= 255; j++) {
				ipList.add(new String("" + ip1[0] + "." + ip2[1] + "." + i + "." + j));
			}
		}
		
		// 192.170.3.0-192.170.3.9
		for (int i = 0; i <= ip2[3]; i++) {
			ipList.add(new String("" + ip1[0] + "." + ip2[1] + "." + ip2[2] + "." + i));
		}
	}
	
	/*
	 * deal with IP range like 1.2.3.4-192.168.5.7
	 * @param ip1 the integer array for 1.2.3.48
	 * @param ip2 the integer array for 192.168.5.7
	 */
	private void diffIntegerAtPosition0(int[] ip1, int[] ip2, List<String> ipList) {
		// 1.2.3.4-1.2.3.255
		for (int i = ip1[3]; i <= 255; i++) {
			ipList.add(new String("" + ip1[0] + "." + ip1[1] + "." + ip1[2] + "." + i));
		}
		
		// 1.2.4.0-1.2.255.255
		for (int i = ip1[2] + 1; i <= 255; i++) {
			for (int j = 0; j <= 255; j++) {
				ipList.add(new String("" + ip1[0] + "." + ip1[1] + "." + i + "." + j));
			}
		}
		
		// 1.3.0.0-1.255.255.255
		for (int i = ip1[1] + 1; i <= 255; i++) {
			for (int j = 0; j <= 255; j++) {
				for (int k = 0; k <= 255; k++) {
					ipList.add(new String("" + ip1[0] + "." + i + "." + j + "." + k));
				}
			}
		}
		
		// 2.0.0.0-191.255.255.255
		for (int i = ip1[0] + 1; i <= ip2[0] - 1; i++) {
			for (int j = 0; j <= 255; j++) {
				for (int k = 0; k <= 255; k++) {
					for (int l = 0; l <= 255; l++) {
						ipList.add(new String("" + i + "." + j + "." + k + "." + l));
					}
				}
			}
		}
		
		// 192.0.0.0-192.167.255.255
		for (int i = 0; i <= ip2[1] - 1; i++) {
			for (int j = 0; j <= 255; j++) {
				for (int k = 0; k <= 255; k++) {
					ipList.add(new String("" + ip2[0] + "." + i + "." + j + "." + k));
				}
			}
		}
		
		// 192.168.0.0-192.168.4.255
		for (int i = 0; i <= ip2[2] - 1; i++) {
			for (int j = 0; j <= 255; j++) {
				ipList.add(new String("" + ip2[0] + "." + ip2[1] + "." + i + "." + j));
			}
		}
		
		// 192.168.5.0-192.168.5.7
		for (int i = 0; i <= ip2[3]; i++) {
			ipList.add(new String("" + ip2[0] + "." + ip2[1] + "." + ip2[2] + i));
		}
	}
}
