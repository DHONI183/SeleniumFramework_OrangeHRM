pipeline {
    agent any  
    tools {
        maven 'MVN_Home' 
    }
    environment {
        COMPOSE_PATH = "${WORKSPACE}/docker"
        SELENIUM_GRID = "true"
    }
    stages {
        stage('Start Selenium Grid via Docker Compose') {
            steps {
                script {
                    echo "Starting Selenium Grid with Docker Compose..."
                    try {
                        bat "docker compose -f ${COMPOSE_PATH}\\docker-compose.yml up -d"
                        echo "Waiting for Selenium Grid to be ready..."
                        sleep 30
                    } catch (Exception e) {
                        error "Failed to start Selenium Grid: ${e.getMessage()}"
                    }
                }
            }
        }
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/DHONI183/SeleniumFramework_OrangeHRM.git'
            }
        }
        stage('Build') {
            steps {
                bat 'mvn clean install -DseleniumGrid=true'
            }
        }
        stage('Test') {
            steps {
                bat "mvn clean test -DseleniumGrid=true"
            }
        }
        stage('Reports') {
            steps {
                script {
                    // Publish HTML report
                    if (fileExists('src/test/resources/ExtentReport/SparkReport.html')) {
                        publishHTML(target: [
                            reportDir: 'src/test/resources/ExtentReport',  
                            reportFiles: 'SparkReport.html',  
                            reportName: 'ExtentReport'
                        ])
                    }
                    
                    // Archive artifacts
                    archiveArtifacts artifacts: '**/src/test/resources/ExtentReport/*.html', 
                                    fingerprint: true, 
                                    allowEmptyArchive: true
                    
                    // Publish test results
                    if (fileExists('target/surefire-reports')) {
                        publishTestResults testResultsPattern: 'target/surefire-reports/*.xml',
                                          allowEmptyResults: true
                    }
                }
            }
        }
        stage('Stop Selenium Grid') {
            steps {
                script {
                    echo "Stopping Selenium Grid..."
                    bat "docker compose -f ${COMPOSE_PATH}\\docker-compose.yml down --remove-orphans || exit 0"
                }
            }
        }
    }
    post {
		
		 
    always {
        archiveArtifacts artifacts: '**/src/test/resources/ExtentReport/*.html', fingerprint: true
        junit 'target/surefire-reports/*.xml'
           } 
        success {
            emailext (
                to: 'manish17nov95@gmail.com',
                subject: "Build Success: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """
                <html>
                <body>
                <p>Hello Team,</p>
                <p>The latest Jenkins build has completed successfully.</p>
                <p><b>Project Name:</b> ${env.JOB_NAME}</p>
                <p><b>Build Number:</b> #${env.BUILD_NUMBER}</p>
                <p><b>Build Status:</b> <span style="color: green;"><b>SUCCESS</b></span></p>
                <p><b>Build URL:</b> <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
                <p><b>Extent Report:</b> <a href="${env.BUILD_URL}HTML_20Extent_20Report/">Click here</a></p>
                <p>Best regards,</p>
                <p><b>Automation Team</b></p>
                </body>
                </html>
                """,
                mimeType: 'text/html',
                attachLog: true
            )
        }
        failure {
            emailext (
                to: 'manish17nov95@gmail.com',
                subject: "Build Failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """
                <html>
                <body>
                <p>Hello Team,</p>
                <p>The latest Jenkins build has <b style="color: red;">FAILED</b>.</p>
                <p><b>Project Name:</b> ${env.JOB_NAME}</p>
                <p><b>Build Number:</b> #${env.BUILD_NUMBER}</p>
                <p><b>Build Status:</b> <span style="color: red;"><b>FAILED &#10060;</b></span></p>
                <p><b>Build URL:</b> <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
                <p><b>Please check the logs and take necessary actions.</b></p>
                <p><b>Extent Report (if available):</b> <a href="${env.BUILD_URL}HTML_20Extent_20Report/">Click here</a></p>
                <p>Best regards,</p>
                <p><b>Automation Team</b></p>
                </body>
                </html>
                """,
                mimeType: 'text/html',
                attachLog: true
            )
        }
        cleanup {
            script {
                echo "Final cleanup..."
                bat "docker compose -f ${COMPOSE_PATH}\\docker-compose.yml down --remove-orphans || exit 0"
            }
        }
    }
}