pipeline {
    agent any
   tools {
  maven 'MAVEN_3_8'
  }
 stages {
        stage('Checkout') {
            steps{
         checkout([$class: 'GitSCM', branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/infotechirfannasim/docyard-be.git']]])
        }
        }
          stage('Build') {
         steps{
            bat "mvn clean install"
        }
     
       }
     stage('Test') {
         steps{
             echo 'This is testing phase.'
         }
       }
      stage('Deploy') {
         steps{
             echo 'This is deployment phase.'
         }
       }
    }
}
