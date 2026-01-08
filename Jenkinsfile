pipeline {
    agent any
    
    stages {
        stage('1-Checkout (GitHub)') {
            steps {
                checkout scm
            }
        }
        
        stage('2-Build') {
            steps {
                dir('backend') {
                    sh './mvnw clean package -DskipTests'
                }
            }
        }
        
        stage('3-Unit Tests') {
            steps {
                dir('backend') {
                    sh './mvnw test'
                }
            }
            post {
                always {
                    junit 'backend/target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('4-Integration Tests') {
            steps {
                dir('backend') {
                    sh './mvnw verify -Pintegration'
                }
            }
            post {
                always {
                    junit 'backend/target/failsafe-reports/*.xml'
                }
            }
        }
        
        stage('5-Run on Docker Containers') {
            steps {
                sh 'docker compose up -d --build'
                sh '''
                    echo "Waiting for backend to be healthy..."
                    timeout=120
                    elapsed=0
                    while [ $elapsed -lt $timeout ]; do
                        if curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
                            echo "Backend is healthy!"
                            break
                        fi
                        sleep 5
                        elapsed=$((elapsed + 5))
                    done
                    if [ $elapsed -ge $timeout ]; then
                        echo "Backend health check timeout"
                        exit 1
                    fi
                '''
            }
        }
        
        stage('6.1-Selenium Scenario 1') {
            steps {
                sh 'docker compose run --rm selenium pytest -k test_01_login_and_dashboard'
            }
            post {
                always {
                    junit 'selenium-tests/selenium-reports/*.xml'
                    archiveArtifacts artifacts: 'selenium-tests/selenium-reports/**', allowEmptyArchive: true
                }
            }
        }
        
        stage('6.2-Selenium Scenario 2') {
            steps {
                sh 'docker compose run --rm selenium pytest -k test_02_create_facility_and_audit'
            }
            post {
                always {
                    junit 'selenium-tests/selenium-reports/*.xml'
                    archiveArtifacts artifacts: 'selenium-tests/selenium-reports/**', allowEmptyArchive: true
                }
            }
        }
        
        stage('6.3-Selenium Scenario 3') {
            steps {
                sh 'docker compose run --rm selenium pytest -k test_03_fill_checklist_and_create_nc_and_capa'
            }
            post {
                always {
                    junit 'selenium-tests/selenium-reports/*.xml'
                    archiveArtifacts artifacts: 'selenium-tests/selenium-reports/**', allowEmptyArchive: true
                }
            }
        }
    }
    
    post {
        always {
            sh 'docker compose down -v'
        }
    }
}
