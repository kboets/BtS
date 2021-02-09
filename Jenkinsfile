pipeline {
  agent  any
  tools {
    maven 'mvn-3.6.1'
    jdk 'jdk-11.0.1'
  }
  
   environment {
      //Use Pipeline Utility Steps plugin to read information from pom.xml into env variables
      IMAGE = readMavenPom().getArtifactId()
      VERSION = readMavenPom().getVersion()
    }
  
  stages {
        stage('Clean') {
            steps {
                echo "${VERSION}"              
            }
        }
    }
}
