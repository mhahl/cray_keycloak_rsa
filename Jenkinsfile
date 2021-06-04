@Library('dst-shared@master') _

pipeline {

  agent { node { label 'dstbuild' } }

  environment {
    PRODUCT = 'csm'
    RELEASE_TAG = setReleaseTag()
  }

  stages {
    stage('Push to github') {
        when { allOf {
            expression { BRANCH_NAME ==~ /(release\/.*|master)/ }
        }}
        steps {
            script {
                pushToGithub(
                    githubRepo: "Cray-HPE/cray_keycloak_rsa",
                    pemSecretId: "githubapp-stash-sync",
                    githubAppId: "91129",
                    githubAppInstallationId: "13313749"
                )
            }
        }
    }
  }
}
