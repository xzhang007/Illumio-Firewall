# Illumio-Firewall
This project is to solve the coding assignment of Illumio about the Firewall
![Alt text](blob/master/classDiagram.jpg?raw=true "Class Diagram")


The algorithm in this class is brute force, the Tricky point for this problem is how to get different IPs from an IP range, 
in the worst case in my class, e.g. 1.2.3.4-192.168.5.7, there will be 4 nested for-loop, 
the time complexity will be 255 ^ 4 ~ 4 billion!


If I have enough time, I will change the whole algorithm using a data structure: Interval Tree. This method will cover the
test case like IP address from 0.0.0.0-255.255.255.255.


There are 4 classes in the project, in each class I add enough annotations and comments for each method and important part,
I think it will be straightforward to read the code.


Firewall class--It has two public method: constructor, to read the input file which contains the rules,
And accept_packet method, to check if an input is acceptable or blocked.


SourceReader class--This class is to help Firewall to read the file.
Since in the java there might be some "try catch" cases that may throw exceptions as well as all readers should be closed finally,
I define a seperate class to deal with all of these situations,then in other parts of the programs, we don't need to pay attention to those stuff.


NoRuleException class--This class is an defined Exception class for some case like the input rule file is not an complete file,
it does not contain all the 4 combination of direction and protocol.


Test class--This class contains the main function, I implement it to run the test.

I like the team: the Platform Team, the Policy Team as well as Data Team.

Thanks for reading.
