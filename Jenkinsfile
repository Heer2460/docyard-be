pipeline {
    agent any

    stages {
        stage('Build') {
            steps { 
              echo 'mvn clean install -f docyard-be/pom.xml'
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
