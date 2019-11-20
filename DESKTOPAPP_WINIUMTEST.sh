export PATH=$PATH:$M2_HOME/bin
source ~/.bash_profile
cd $AUTOMATION_PATH
mvn clean test -P Desktop_Suite
