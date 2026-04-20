#!/bin/bash
# Generate Self-Signed SSL Certificates for Local Development (Nginx)
CERT_DIR="./infrastructure/nginx/certs"
mkdir -p $CERT_DIR

openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
  -keyout $CERT_DIR/nginx.key \
  -out $CERT_DIR/nginx.crt \
  -subj "/C=VN/ST=HCM/L=HCM/O=YamateeClub/OU=IT/CN=localhost"

echo "SSL Certificates generated in $CERT_DIR"
