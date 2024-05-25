#!/bin/bash

cd ..

docker-compose -f docker-compose.dev.yaml up --build -d

docker-compose -f docker-compose.dev.yaml ps
