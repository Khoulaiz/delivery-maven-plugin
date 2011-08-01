Delivery-Maven-Plugin
=====================

Provides the following features:

* copies an artifact from a repository to a remote server using different protocols
* executes a number of commands on the remote server
* supports at least scp via jsch _and_ an external ssh (to support ssh agents)
* the goals should be callable independently from a build phase (delivery without build)
* several staging servers should be supported

In short: the goal is to automate the usual "scp xyz.jar server:/var/www/webapps; ssh server; tomcat.sh restart"
