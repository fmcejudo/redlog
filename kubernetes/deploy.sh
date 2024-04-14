alias k='kubectl'
export KUBECONFIG=~/development/kubernetes/microk8s.config
k apply -f mongo
k apply -f vault
k apply -f loki
