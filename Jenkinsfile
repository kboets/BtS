pipeline {
  agent  any
  tools {
    maven 'mvn-3.6.1'
  }

   stages {
     stage('compile') {
       steps {
            echo 'compiling the application...'
            sh 'mvn clean compile'
          }
     }

   }    
}
