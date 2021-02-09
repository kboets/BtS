pipeline {
  agent  any
  tools {
    maven 'mvn-3.6.1'
    jdk 'jdk-11.0.1'
  }
  
  stages {
        stage('Clean') {
            steps {
                sh "echo mvn -v"
                sh "mvn clean"               
            }
        }
    }
}
