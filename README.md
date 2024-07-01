# Automated Deployment Pipeline with Docker, Jenkins, and Ansible

This README provides an overview and key steps for setting up an automated deployment pipeline using Docker, Jenkins, and Ansible on an Ubuntu EC2 instance.

## Overview

- **Docker**: Used for containerizing the application.
- **Jenkins**: Automates the build and deployment process.
- **Ansible**: Manages deployment and configuration on the EC2 instance.

## Steps

### 1. Set Up Ubuntu EC2 Instance

1. **Launch EC2 Instance**:
   - Go to the AWS Management Console.
   - Launch an Ubuntu 20.04 EC2 instance.
   - Configure instance type, security groups, etc.
   - SSH into the instance.

2. **Update the Instance**:
   ```sh
   sudo apt update && sudo apt upgrade -y
   
# 2. Install Docker

## 1.Install Docker:
    sudo apt update
    sudo apt install docker.io -y
    
## 2.Start and Enable Docker:
    sudo systemctl start docker
    sudo systemctl enable docker
    
### 3.Add User to Docker Group:
    sudo usermod -aG docker ubuntu

# 3. Install Jenkins
##  1.Install Java (Jenkins dependency):
    sudo apt install openjdk-11-jdk -y

## 2.Add Jenkins Repository and Install Jenkins:
    curl -fsSL https://pkg.jenkins.io/debian/jenkins.io.key | sudo tee /usr/share/keyrings/jenkins-keyring.asc
    echo deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc] https://pkg.jenkins.io/debian binary/ | sudo tee /etc/apt/sources.list.d/jenkins.list > /dev/null
    sudo apt update
    sudo apt install jenkins -y
    
## 3.Start and Enable Jenkins:
    sudo systemctl start jenkins
    sudo systemctl enable jenkins

## 4.Access Jenkins:

- Open port 8080 in EC2 security group.
- Access Jenkins at http://<your-ec2-instance-ip>:8080.

## 5.Unlock Jenkins:
 - Retrieve the initial admin password:

           sudo cat /var/lib/jenkins/secrets/initialAdminPassword
   
# 4. Install Ansible
- Install Ansible:

      sudo apt install ansible -y
  
# 5. Configure Docker for Your Application

## 1.Create Dockerfile:

    FROM node:14
    WORKDIR /app
    COPY . .
    RUN npm install
    EXPOSE 3000
    CMD ["npm", "start"]

## 2.Build and Run Docker Container:

    docker build -t my-app .
    docker run -p 3000:3000 my-app

# 6. Set Up Jenkins Pipeline

## 1.Install Plugins:

- Docker
- Git
- Pipeline
- Ansible

## 2.Create Pipeline Job:

- Go to Jenkins dashboard -> New Item -> Pipeline.
- Add the following pipeline script:

      pipeline {
       agent any
      stages {
        stage('Build') {
            steps {
                script {
                    docker.build('my-app')
                }
            }
        }
        stage('Deploy') {
            steps {
                sshagent(['my-ssh-credentials']) {
                    sh 'ansible-playbook -i inventory deploy.yml'
                }
            }
        }
      }
      }









