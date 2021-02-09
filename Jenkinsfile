pipeline {
  agent  any
  tools {
    maven 'mvn-3.6.1'
    jdk 'jdk-11.0.1'
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
     stage ('Build') {
            steps {
                echo 'This is a minimal pipeline.'
            }
        }
  }

}
