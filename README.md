# distributed_Programming2

**problems I met in the final test**  
1. I forgot to modify sol_build.xml, so the client could not be compiled...  
2. the use of enum type and misused of 200 and 201(create of new resource) status code  
3. forgot to manage the shutdown of neo4j service, then cause the last test couldn't be passed
(NotFoundException->unknownNameException) in NffgClient-> testReachability  

**Steps to run**  
1. start neo4j and tomcat(in the xampp) services  
2. run ant
