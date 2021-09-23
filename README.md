# tapis-Cmd

Provides a convenient way for Tapis commands to be issued from within a Java IDE and from the command line.

## Setup

1. Builds with "mvn clean install"

2. Any jwt files passed as a parameter **must** be placed in $HOME/TapisCmd/jwt

3. Any keys passed as a parameter **must** be the full path to the key on the local filesystem  

4. Any json request files passed as a parameter from the command line **must** be in $HOME/TapisCmd/requests,
   while IDE references to requests can be in tapis-cmd/src/main/java/edu/utexas/tacc/tapis/cmd/driver/requests,
   as well as $HOME/TapisCmd/requests

### Running

a) To run from command line, navigate to same directory as the tapis-cmd.jar file and run with the shell file 
	NOTE: **final arguments after class name must be in Quotes** 
              ex: "-jwt (jwt filename) -sys (system name)"

	Example CLI Command: 
	
	bash tapis-cmd (name of class) (args for class ex: -jwt (jwt filename)) **IN QUOTES**)

b) To run from IDE like Eclipse, build from the command line with "mvn clean install", import as an existing maven project,
     finally use a run configuration to pass the necessary parameters like -jwt (jwt filename)

#### Help Info

For additional info on parameters pass any class "-help true" as an argument and information on 
each parameter and its usage will be printed to command line for CLI and console for IDE.
