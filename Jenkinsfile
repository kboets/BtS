pipeline {
  agent  any
  tools {
    maven 'mvn-3.6.1'
  }

   stages {
       sh "mvn -N help:effective-pom -Doutput=target/pom-effective.xml"

       script {
           pom = readMavenPom(file: 'target/pom-effective.xml')
           projectArtifactId = pom.getArtifactId()
           projectGroupId = pom.getGroupId()
           projectVersion = pom.getVersion()
           projectName = pom.getName()
       }

     echo "Building ${projectArtifactId}:${projectVersion}"

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
