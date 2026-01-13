pipeline {
    agent any

    options {
        timestamps()
    }

    environment {
        TESTCONTAINERS_RYUK_DISABLED = 'true'
        TESTCONTAINERS_CHECKS_DISABLE = 'true'
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
                    // Windows ortamından geliyorsa CRLF karakterlerini temizle
                    sh "sed -i 's/\r\$//' mvnw"
                    sh './mvnw clean package -DskipTests'
                }
            }
        }

        stage('3-Unit Tests') {
            steps {
                dir('backend') {
                    sh 'chmod +x mvnw'
                    sh "sed -i 's/\r\$//' mvnw"
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
                    sh "sed -i 's/\r\$//' mvnw"
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
                sh '''
                    # Sadece backend, db ve chrome servislerini başlat/güncelle. Jenkins'i yeniden başlatma!
                    docker compose build selenium
                    docker compose up -d --build backend db chrome
                    
                    echo "Waiting for backend to be healthy..."
                    timeout=120
                    elapsed=0
                    until curl -fsS http://host.docker.internal:8080/actuator/health > /dev/null; do
                      sleep 5
                      elapsed=$((elapsed+5))
                      if [ $elapsed -ge $timeout ]; then
                        echo "Backend health check timeout"
                        docker compose ps
                        docker compose logs backend
                        exit 1
                      fi
                      echo "Waiting... ($elapsed/$timeout sec)"
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
                        // Jenkins'in kendisini kapatmaması için sadece test servislerini durduruyoruz
            sh 'docker compose stop backend db chrome selenium || true'
            sh 'docker compose rm -f -v backend db chrome selenium || true'
        }
    }
}
