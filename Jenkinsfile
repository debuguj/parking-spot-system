node{
  stage('SCM Checkout'){
    git 'https://github.com/debuguj/parking-spot-system'
  }
  stage('Compile-Package'){
    sh 'mvn package'
  }
}
