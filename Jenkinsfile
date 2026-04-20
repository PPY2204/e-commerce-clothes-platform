pipeline {
    agent any

    environment {
        DOCKER_REGISTRY = 'your-docker-registry'
        SONAR_TOKEN = credentials('sonar-token')
        SERVICES = 'user-service,product-catalog-service,shopping-cart-service,order-service,payment-service,notification-service,review-service,recommendation-service'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('My SonarQube Server') {
                    sh 'mvn sonar:sonar'
                }
            }
        }

        stage('Build & Unit Test') {
            steps {
                sh 'mvn clean install -DskipTests'
            }
        }

        stage('Build & Push Docker Images') {
            steps {
                script {
                    def servicesList = SERVICES.split(',')
                    def stages = [:]

                    for (int i = 0; i < servicesList.size(); i++) {
                        def service = servicesList[i]
                        stages[service] = {
                            stage("Build ${service}") {
                                sh "docker build -f be/Dockerfile.service --build-arg SERVICE_NAME=${service} -t ${DOCKER_REGISTRY}/${service}:${BUILD_NUMBER} be/"
                                // sh "docker push ${DOCKER_REGISTRY}/${service}:${BUILD_NUMBER}"
                            }
                        }
                    }
                    parallel stages
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
        success {
            echo "Successfully built and deployed all services!"
        }
    }
}
