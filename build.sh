#!/bin/bash
echo "Building Nemesis..."
mvn clean
mvn test
mvn compile
mvn package
echo "Done."