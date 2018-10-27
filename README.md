# Illumio-Firewall
This project is to solve the coding assignment of Illumio about the Firewall
![Alt text](https://github.com/xzhang007/Illumio-Firewall/blob/master/classDiagram.jpg "Class Diagram")

【Algorithm】
The algorithm in this class is brute force, the Tricky point for this problem is how to get different IPs from an IP range, 
in the worst case in my class, e.g. 1.2.3.4-192.168.5.7, there will be 4 nested for-loop, 
the time complexity will be 255 ^ 4 ~ 4 billion!


If I have enough time, I will change the whole algorithm using a data structure: Interval Tree. This method will cover the
test case like IP address from 0.0.0.0-255.255.255.255.

【Classes】
There are 4 classes in the project, in each class I add enough annotations and comments for each method and important part,
I think it will be straightforward to read the code.



Firewall class--I use 4 hashmap to storage the 4 rules, for each map, the key is the portNumber,
 the value is the set of IP address in that port number.
 It has two public method: constructor, to read the input file which contains the rules,
And accept_packet method, to check if an input is acceptable or blocked.


SourceReader class--This class is to help Firewall to read the file.
Since in the java there might be some "try catch" cases that may throw exceptions as well as all readers should be closed finally,
I define a seperate class to deal with all of these situations,then in other parts of the programs, we don't need to pay attention to those stuff.


NoRuleException class--This class is an defined Exception class for some case like the input rule file is not an complete file,
it does not contain all the 4 combination of direction and protocol.


Test class--This class contains the main function, I implement it to run the test.


【Methods in Firewall class】
In the Firewall class, there are multiple methods:

Constructor:  Construct a new Firewall class, in this method, it will call SourceReader class to read the file line by line,
and for each line, it will call a series of private method to help parse the rules, and when it finish, it will add each port number and ip address to the associate rules map.

accept_packet(String direction, String protocol, int port, String ip_address): This method is check if an input will be acceptable or blocked based on the rules.

parseInputLine(String line): This method is to parse the line into 4 parts: direction, protocol, ports and IPs.

dealWithPorts(String direction, String protocol, String ports, String IPs): This method is to check if in the "ports" part, it is a range. If it is a range, we will traverse all the integer between that range.

dealWithIPs(String direction, String protocol, int portNumber, String IPs): This method is to check the "IPs" part, if the IP is a range. If it is a range, we will seperate the start IP and end IP.

addToRules(String direction, String protocol, int portNumber, String IP): This method is to add one port number and one IP address to the associate rules map.

rulesSelection(String direction, String protocol): This method is to get the associate rules map based on the direction and protocol.

generateIPs(String ipStart, String ipEnd): This method is the tricky point of the problem. It is to generate all IPs based on an IP range.

diffIntegerAtPosition3(int[] ip1, int[] ip2, List<String> ipList): deal with IP range like 192.168.1.8-192.168.1.168

diffIntegerAtPosition2(int[] ip1, int[] ip2, List<String> ipList): deal with IP range like 192.168.1.1-192.168.3.9

diffIntegerAtPosition1(int[] ip1, int[] ip2, List<String> ipList): deal with IP range like 192.168.1.8-192.170.3.9
 
diffIntegerAtPosition0(int[] ip1, int[] ip2, List<String> ipList): deal with IP range like 1.2.3.4-192.168.5.7



I like the team: the Platform Team, the Policy Team as well as Data Team.

Thanks for reading.
