pipeline {
  agent  any
  tools {
    maven 'mvn-3.6.1'
    jdk 'jdk-11.0.1'
  }
  
  stages {
        stage('Clean') {
            steps {
                echo sh "mvn -v"
                sh "mvn clean"               
            }
        }
    }
}
