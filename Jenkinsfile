pipeline {
  agent  any
  tools {
    maven 'mvn-3.6.1'
    jdk 'jdk-11.0.1'
  }

   stages {
     stage('compile') {
       steps {
            echo 'compiling the application...'
            echo mvn -v | grep -i java
            //sh 'mvn clean compile'
          }
     }

   }    
}
