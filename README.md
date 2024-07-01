# Automated Deployment Pipeline for a Web Application

This README provides an overview and key steps for setting up an automated deployment pipeline using Docker, Jenkins, and Ansible on an Ubuntu EC2 instance.

### Overview

This project demonstrates how to automate the deployment of a web application by integrating the following tools:

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
   sudo apt update 
   
### 2. Install Docker

#### 1.Install Docker:
    sudo apt update
    sudo apt install docker.io -y
    
#### 2.Start and Enable Docker:
    sudo systemctl start docker
    sudo systemctl enable docker
    
#### 3.Add User to Docker Group:
    sudo usermod -aG docker ubuntu

### 3. Install Jenkins
####  1.Install Java (Jenkins dependency):
    sudo apt install openjdk-11-jdk -y

#### 2.Add Jenkins Repository and Install Jenkins:
    sudo wget -O /usr/share/keyrings/jenkins-keyring.asc \
    https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key
    echo "deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc]" \
    https://pkg.jenkins.io/debian-stable binary/ | sudo tee \
    /etc/apt/sources.list.d/jenkins.list > /dev/null
    sudo apt-get update
    sudo apt-get install jenkins
    
#### 3.Start and Enable Jenkins:
    sudo systemctl start jenkins
    sudo systemctl enable jenkins

#### 4.Access Jenkins:

- Open port 8080 in EC2 security group.
- Access Jenkins at http://3.25.114.20:8080.

#### 5.Unlock Jenkins:
 - Retrieve the initial admin password:

           sudo cat /var/lib/jenkins/secrets/initialAdminPassword
   
#### 4. Install Ansible
- Install Ansible:

      sudo apt install ansible -y
  
### 5. Configure Docker for Application

#### 1.Create Dockerfile:

    FROM node:14
    WORKDIR /app
    COPY . .
    RUN npm install
    EXPOSE 3000
    CMD ["npm", "start"]

#### 2.Build and Run Docker Container:

    docker build -t my-app .
    docker run -p 3000:3000 my-app

### 6. Set Up Jenkins Pipeline

#### 1.Install Plugins:

- Docker
- Git
- Pipeline
- Ansible

#### 2.Create Pipeline Job:

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

### 7. Create Ansible Playbooks

#### 1.Inventory File (inventory):

    [web]
    3.25.114.20 ansible_ssh_user=ubuntu

#### 2.Playbook (deploy.yml):

    - hosts: web
    become: yes
    tasks:
    - name: Stop existing container
      docker_container:
        name: my-app
        state: absent
        force_kill: yes

    - name: Remove old image
      docker_image:
        name: my-app
        state: absent

    - name: Pull new image
      docker_image:
        name: my-app
        source: pull

    - name: Start new container
      docker_container:
        name: my-app
        image: my-app
        state: started
        ports:
          - "3000:3000"

### 8. Configure Jenkins to Use Ansible

#### 1.Generate SSH Key:

    ssh-keygen -t rsa -b 4096 -C "sivan7863@gmail.com"
    ssh-copy-id -i ~/.ssh/id_rsa.pub ubuntu@3.25.114.20

#### 2.Add SSH Key to Jenkins:

    Go to "Manage Jenkins" -> "Manage Credentials".
    Add new credentials with ID my-ssh-credentials.

### 9. Test Pipeline

#### 1.Commit and Push Changes:
    git add inventory deploy.yml
    git commit -m "Add Ansible inventory and playbook files"
    git push origin main
    
#### 2.Trigger a Build in Jenkins:

- Go to Jenkins job and click "Build Now".



    






