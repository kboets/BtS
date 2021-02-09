pipeline {
  agent  any
  tools {
    maven 'mvn-3.6.1'
    jdk 'jdk-11.0.1'
  }
  
  stages {
        stage('Clean') {
            steps {
                sh "echo JAVA_HOME=$JAVA_HOME"
                sh "mvn clean"               
            }
        }
    }
}
