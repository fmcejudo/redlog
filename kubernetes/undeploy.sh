alias k='kubectl'
export KUBECONFIG=~/development/kubernetes/microk8s.config
k delete -f vault
k delete -f mongo
k delete -f loki
k delete -f redlog
