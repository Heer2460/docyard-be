pipeline {
    agent any
 

   stages {
        stage('Github') {
            steps{
         checkout([$class: 'GitSCM', branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/infotechirfannasim/docyard-be.git']]])
        }
        }
          stage('Build') {
         steps{
             sh 'mvn clean install -f docyard-be/pom.xml'
         }
         
        }
    }
}
