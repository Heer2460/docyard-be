pipeline {
    agent any
    triggers {
        githubPush()
    }
    stages {
        stage('Clone Code') {
            steps {
                checkout changelog: false, poll: false, scm: scmGit(branches: [[name: '*/devRemoteFeature']], extensions: [], userRemoteConfigs: [[credentialsId: '7032a7b7-8a6a-46db-8b20-947d4b955a1d', url: 'https://github.com/infotechirfannasim/docyard-be.git']])
            }
        }
         stage('clean code') {
            steps {
               bat 'mvn -B -DskipTests clean'
            }
        }
        stage('Compile Code') {
            steps {
               bat 'mvn -B -DskipTests compile'
            }
        }
        stage('Install Code') {
            steps {
               bat 'mvn -B -DskipTests install'
            }
        }
        stage('Deploy Code') {
             steps {
                // Run Docker-Compose to build images and run services.
                bat 'docker-compose -f docker-compose.yml up -d'
            }
        }
    }
}