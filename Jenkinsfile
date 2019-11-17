node {
    properties([
        pipelineTriggers([
            [$class: "SCMTrigger", scmpoll_spec: "*/3 * * * *"],
        ]),
        buildDiscarder(logRotator(daysToKeepStr: '3', numToKeepStr: '3'))
    ])

    stage("BuildProcess"){
        /* Requires the Docker Pipeline plugin to be installed */
        docker.image('maven:3-alpine')
            .inside('-v $HOME/.m2:/root/.m2 -v /var/run/docker.sock:/var/run/docker.sock --privileged') {
                SOURCE_REPOSITORY_URL = ""
                SOURCE_REPOSITORY_CREDENTIALS_ID = ""
                stage('Checkout') {
                    def scmVars = checkout([$class: 'GitSCM',
                                branches: [[name: '*/master']],
                                doGenerateSubmoduleConfigurations: false,
                                extensions: [],
                                submoduleCfg: [],
                                userRemoteConfigs:
                                [[credentialsId: SOURCE_REPOSITORY_CREDENTIALS_ID, url: SOURCE_REPOSITORY_URL]]])
                    /* Detecting commit parameters */
                    GIT_COMMIT_ID = scmVars.GIT_COMMIT
                    PROJECT_SOURCE_BRANCH = scmVars.GIT_BRANCH
                }

                stage('PrepareParameters'){
                    /* Preparing GKE parameters */
                    GKE_PROJECT_ID = ""
                    GKE_CLUSTER_NAME = ""
                    GKE_LOCATION = ""
                    GKE_CREDENTIALS_ID = ""
                    DOCKER_HOST_IP = ""
                    DOCKER_HOST_PORT = ""
                    DOCKER_REGISTRY_CREDENTIALS_ID = ""
                    DOCKER_IMAGE_DEFAULT_NAME = "ddemirel/simple-spring-boot-k8s-app:latest"
                    DOCKER_IMAGE_REGISTY_NAME_PREFIX = "gcr.io/simple-cloud-project/simple-spring-boot-k8s-app"

                    def matcher = readFile('pom.xml') =~ '<version>(.+?)</version>'
                    PROJECT_MAVEN_VERSION = matcher ? matcher[0][1] : null
                    echo "GIT_COMMIT_ID : ${GIT_COMMIT_ID}"
                    echo "PROJECT_SOURCE_BRANCH : ${PROJECT_SOURCE_BRANCH}"
                    echo "PROJECT_MAVEN_VERSION : ${PROJECT_MAVEN_VERSION}"
                }

                stage('Build') {
                    sh 'mvn -DskipTests clean install'
                }

                stage('Test') {
                    sh 'mvn test'
                }

                stage('BuildDockerImage'){
                   sh label: 'Build',
                      script: 'mvn "-Dmaven.test.skip" "-Ddocker.host=http://${DOCKER_HOST_IP}:${DOCKER_HOST_PORT}" docker:build'
                }

           }
    }

    stage('PushDockerImage'){
       withDockerRegistry([credentialsId: DOCKER_REGISTRY_CREDENTIALS_ID, url: "https://gcr.io"]) {
         sh "docker tag ${DOCKER_IMAGE_DEFAULT_NAME} ${DOCKER_IMAGE_REGISTY_NAME_PREFIX}-${PROJECT_SOURCE_BRANCH}:${PROJECT_MAVEN_VERSION}.${GIT_COMMIT_ID}"
         sh "docker push ${DOCKER_IMAGE_REGISTY_NAME_PREFIX}-${PROJECT_SOURCE_BRANCH}:${PROJECT_MAVEN_VERSION}.${GIT_COMMIT_ID}"
       }
    }

    stage("Deploy to GKE"){
        path = "${workspace}/k8s-definitions.yml"
        def fileContent = readFile(path)
        IMAGE_NAME_VAR = "${DOCKER_IMAGE_REGISTY_NAME_PREFIX}-${PROJECT_SOURCE_BRANCH}:${PROJECT_MAVEN_VERSION}.${GIT_COMMIT_ID}"
        /* Preparing k8s deployment script */
        fileContent = fileContent.replaceAll("#IMAGE_NAME", IMAGE_NAME_VAR)

        DEPLOYNEMT_FILE_NAME='k8s-deployment-temp.yml'

        writeFile file: DEPLOYNEMT_FILE_NAME, text: fileContent

        def newFileContent = readFile(DEPLOYNEMT_FILE_NAME)
        echo newFileContent

        /* Deploying to GKE */
        step([
            $class: 'KubernetesEngineBuilder',
            projectId: GKE_PROJECT_ID,
            clusterName: GKE_CLUSTER_NAME,
            location: GKE_LOCATION,
            manifestPattern: DEPLOYNEMT_FILE_NAME,
            credentialsId: GKE_CREDENTIALS_ID,
            namespace : "default",
            verifyDeployments: true]
            )
    }
}