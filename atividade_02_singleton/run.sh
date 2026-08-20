#!/usr/bin/env bash
# Compila e executa a demonstracao do Sistema Central de Alertas.
set -e
cd "$(dirname "$0")"
javac -d out src/*.java
java -cp out Main
