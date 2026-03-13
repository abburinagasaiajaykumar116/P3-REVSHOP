pipeline {
    agent any

    tools {
        maven 'Maven' // Adjust based on Jenkins Global Tool Configuration
        nodejs 'NodeJS' // Adjust based on Jenkins Global Tool Configuration
    }

    environment {
        FRONTEND_DIR = 'revshop-frontend'
    }

    stages {
        stage('Checkout') {
            steps {
                // Assuming Jenkins pulls from SCM automatically or this step does the checkout if configured with a repository url.
                checkout scm
            }
        }

        stage('Build Backend') {
            steps {
                // Package all modules assuming Jenkins runs in workspace root.
                // Since there is no parent POM, we package them individually.
                echo 'Building Config Server...'
                dir('revshop-config-server') { bat 'mvn clean package -DskipTests' }
                
                echo 'Building Eureka Server...'
                dir('revshop-eureka-server') { bat 'mvn clean package -DskipTests' }

                echo 'Building API Gateway...'
                dir('revshop-api-gateway') { bat 'mvn clean package -DskipTests' }

                echo 'Building User Service...'
                dir('revshop-user-service') { bat 'mvn clean package -DskipTests' }

                echo 'Building Product Service...'
                dir('revshop-product-service') { bat 'mvn clean package -DskipTests' }

                echo 'Building Order Service...'
                dir('revshop-order-service') { bat 'mvn clean package -DskipTests' }

                echo 'Building Cart Service...'
                dir('revshop-cart') { bat 'mvn clean package -DskipTests' }

                echo 'Building Payment Service...'
                dir('revshop-payment-service') { bat 'mvn clean package -DskipTests' }

                echo 'Building Notification Service...'
                dir('revshop-notification') { bat 'mvn clean package -DskipTests' }
            }
        }

        stage('Build Frontend') {
            steps {
                dir("${FRONTEND_DIR}") {
                    bat 'npm install --legacy-peer-deps'
                    bat 'npm run build'
                }
            }
        }

        stage('Docker Compose Build and Deploy') {
            steps {
                // Ensure Docker is running and available in PATH
                bat 'docker-compose down'
                bat 'docker-compose build'
                bat 'docker-compose up -d'
            }
        }
    }

    post {
        success {
            echo 'Deployment Successful! The application is running via Docker Compose.'
        }
        failure {
            echo 'Deployment failed. Please check the logs.'
        }
    }
}
