pipeline {
  agent  any
  tools {
    maven 'mvn-3.6.1'    
  }
    
  stages {
    stage ('Initialize') {
            steps {
                sh '''
                    echo "PATH = ${PATH}"
                    echo "M2_HOME = ${M2_HOME}"
                ''' 
            }
      } 
     
     stage('compile') {
       steps {
            echo 'compiling the application...'
            sh 'mvn clean compile'
          }
     }
    
  }

}
