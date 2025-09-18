pipeline {
    agent any
    options {
        skipDefaultCheckout()
    }
    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/DHONI183/SeleniumFramework_OrangeHRM.git'
            }
        }
        stage('Build') {
            steps {
                bat 'mvn clean install'
            }
        }
        stage('Test') {
            steps {
                bat 'mvn test'
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
    
    post {
        always {
            script {
                archiveArtifacts artifacts: '**/src/test/resources/ExtentReport/*.html', fingerprint: true
                junit 'target/surefire-reports/*.xml'
            }
        }
        success {
            emailext (
                to: 'manish17nov95@gmail.com',
                subject: "Build Success: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "Build Successful! Check reports here: ${env.BUILD_URL}"
            )
        }
        failure {
            emailext (
                to: 'manish17nov95@gmail.com',
                subject: "Build Failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "Build Failed! Check logs here: ${env.BUILD_URL}"
            )
        }
    }
}
