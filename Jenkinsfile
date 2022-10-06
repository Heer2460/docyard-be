pipeline {
    agent any

    stages {
        stage('Build') {
            steps { 
               "mvn clean install" 
            }
        }
   stage('Test') {
            steps {
                echo 'Test APP'
            }
        }
   stage('Deploy') {
            steps {
                echo 'Deploy APP'
            }
        }
    }
}
