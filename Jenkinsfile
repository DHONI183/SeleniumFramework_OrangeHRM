pipeline {
    agent any
    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/DHONI183/SeleniumFramework_OrangeHRM.git'
            }
        }
        stage('Build & Test') {
            steps {
                sh 'mvn clean test'
            }
        }
        stage('Publish Report') {
            steps {
                publishHTML(target: [
                    reportDir: 'target/extent-reports',
                    reportFiles: 'index.html',
                    keepAll: true,
                    reportName: 'Extent Report'
                ])
            }
        }
    }
}
