pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                script {
                    // Build the Docker image
                    docker.build('my-app')
                }
            }
        }
        stage('Deploy') {
            steps {
                sshagent(['my-ssh-credentials']) {
                    // Run the Ansible playbook to deploy the application
                    sh 'ansible-playbook -i inventory deploy.yml'
                }
            }
        }
    }
}
