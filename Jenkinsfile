pipeline {
    agent any

    options {
        timestamps()
    }

    stages {
        stage('1-Checkout (GitHub)') {
            steps {
                checkout scm
            }
        }

        stage('2-Build') {
            steps {
                dir('backend') {
                    sh 'chmod +x mvnw'
                    sh './mvnw clean package -DskipTests'
                }
            }
        }

        stage('3-Unit Tests') {
            steps {
                dir('backend') {
                    sh 'chmod +x mvnw'
                    sh './mvnw test'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'backend/target/surefire-reports/*.xml'
                }
            }
        }

        stage('4-Integration Tests') {
            steps {
                dir('backend') {
                    sh 'chmod +x mvnw'
                    // integration profile sende varsa çalışır; yoksa bu stage fail olabilir
                    sh './mvnw verify -Pintegration'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'backend/target/failsafe-reports/*.xml'
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
                    until curl -fsS http://localhost:8080/actuator/health > /dev/null; do
                      sleep 5
                      elapsed=$((elapsed+5))
                      if [ $elapsed -ge $timeout ]; then
                        echo "Backend health check timeout"
                        docker compose ps
                        exit 1
                      fi
                    done
                    echo "Backend is healthy!"
                '''
            }
        }

        stage('6.1-Selenium Scenario 1') {
            steps {
                sh 'docker compose run --rm selenium pytest -k test_01_login_and_dashboard'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'selenium-tests/selenium-reports/*.xml'
                    archiveArtifacts allowEmptyArchive: true, artifacts: 'selenium-tests/selenium-reports/**'
                }
            }
        }

        stage('6.2-Selenium Scenario 2') {
            steps {
                sh 'docker compose run --rm selenium pytest -k test_02_create_facility_and_audit'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'selenium-tests/selenium-reports/*.xml'
                    archiveArtifacts allowEmptyArchive: true, artifacts: 'selenium-tests/selenium-reports/**'
                }
            }
        }

        stage('6.3-Selenium Scenario 3') {
            steps {
                sh 'docker compose run --rm selenium pytest -k test_03_fill_checklist_and_create_nc_and_capa'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'selenium-tests/selenium-reports/*.xml'
                    archiveArtifacts allowEmptyArchive: true, artifacts: 'selenium-tests/selenium-reports/**'
                }
            }
        }
    }

    post {
        always {
            sh 'docker compose down -v || true'
        }
    }
}
