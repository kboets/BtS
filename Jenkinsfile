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
    
    stage('test') {
          steps {
            echo 'testing the application...'
            sh 'mvn test'
          }
     }

     stage('build') {
       steps {
            echo 'building the application...'
            sh 'mvn -DskipTests clean install'
          }
     }
    
  }

}
