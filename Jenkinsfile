node{
  stage('SCM Checkout'){
    git 'https://github.com/debuguj/parking-spot-system'
  }
  stage('Compile-Package'){
    sh 'mvn package'
  }
  stage('Email Notification'){
    mail bcc: '', body: 'Simple info from jenkins', cc: '', from: '', replyTo: '', subject: 'Test email', to: 'debuguj.pl@gmail.com'
  }
}
