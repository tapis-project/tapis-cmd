# tapis-Cmd

Provides a convenient way for Tapis commands to be issued from within a Java IDE and from the command line.

## Setup

1. Builds with "mvn clean install" and "mvn -f shaded-pom.xml package"

2. Any jwt files passed as a parameter **must** be placed in $HOME/TapisCmd/jwt and should have the naming
   convention "target-tenant-user.properties". The jwt should contain a BASE_URL and a USER_JWT.

3. Any keys passed as a parameter **must** be the full path to the key on the local filesystem  

4. Any json request files passed as a parameter from the command line **must** be in $HOME/TapisCmd/requests,
   while IDE references to requests can be in tapis-cmd/src/main/java/edu/utexas/tacc/tapis/cmd/driver/requests,
   as well as $HOME/TapisCmd/requests

### Running

a) To run from command line, navigate to the top level directory and run with the shell file 

	Example CLI Command: 
	
		./tapiscmd AppGet -jwt dev-dev-testuser2 -app SleepSeconds -version 0.0.1

        ./tapiscmd FilesList -sys tapisv3-exec -path /jobs/input -limit 5 -offset 1 -meta true -jwt dev-dev-testuser2

        ./tapiscmd JobSubmit -req Job_Submit_SleepSeconds_tapisv3-exec.json -jwt dev-dev-testuser2

b) To run from IDE like Eclipse, build from the command line with "mvn clean install", import as an existing maven project,
     finally use a run configuration to pass the necessary parameters like -jwt (jwt filename)

	Example IDE Command:

		Run Config for AppGet: -jwt dev-dev-testuser2 -app SleepSeconds -version 0.0.1

        Run Config for FilesList: -sys tapisv3-exec -path /jobs/input -limit 5 -offset 1 -meta true -jwt dev-dev-testuser2

        Run Config for JobSubmit: -req Job_Submit_SleepSeconds_tapisv3-exec.json -jwt dev-dev-testuser2

#### Help Info

For additional info on parameters pass any class "-help t" as an argument and information on 
each parameter and its usage will be printed to command line for CLI and console for IDE.
