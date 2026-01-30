#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

LOG_DIR="logs"
mkdir -p $LOG_DIR

echo -e "${GREEN}🚀 PCM Platform Launcher${NC}"
echo "-----------------------------------"

# 1. Check Prerequisites
if ! command -v docker &> /dev/null; then
    echo -e "${RED}Error: docker is not installed.${NC}"
    exit 1
fi

if ! command -v java &> /dev/null; then
    echo -e "${RED}Error: java is not installed.${NC}"
    exit 1
fi

# 2. Start Infrastructure
echo -e "${YELLOW}Starting Infrastructure (Docker)...${NC}"
docker-compose up -d
if [ $? -ne 0 ]; then
    echo -e "${RED}Failed to start docker-compose infrastructure.${NC}"
    exit 1
fi

echo "Waiting for core infrastructure to warm up (15s)..."
# Simple wait, in a real script we might check ports
sleep 15

# 3. Start Microservices

# Function to start a service
start_service() {
    SERVICE_NAME="$1"
    SERVICE_DIR="$2"
    PORT="$3"
    
    echo -e "${YELLOW}Starting $SERVICE_NAME on port $PORT...${NC}"
    
    # Use absolute path for log and pid files to avoid cd issues
    ROOT_DIR="$(pwd)"
    
    cd "$SERVICE_DIR" || exit
    nohup mvn spring-boot:run > "$ROOT_DIR/$LOG_DIR/$SERVICE_NAME.log" 2>&1 &
    PID=$!
    echo "$PID" > "$ROOT_DIR/$LOG_DIR/$SERVICE_NAME.pid"
    cd "$ROOT_DIR" || exit
    
    echo "  -> Logs: $LOG_DIR/$SERVICE_NAME.log"
}

# Function to check health
check_health() {
    SERVICE_NAME=$1
    URL=$2
    MAX_RETRIES=30
    COUNT=0
    
    echo -n "  -> Waiting for $SERVICE_NAME to be healthy..."
    
    while [ $COUNT -lt $MAX_RETRIES ]; do
        STATUS=$(curl -s -o /dev/null -w "%{http_code}" $URL)
        if [ "$STATUS" == "200" ]; then
            echo -e " ${GREEN}OK${NC}"
            return 0
        fi
        echo -n "."
        sleep 5
        COUNT=$((COUNT+1))
    done
    
    echo -e " ${RED}FAILED${NC}"
    return 1
}

# Start Config Service first
start_service "config-service" "config-service" 8888

echo "Waiting for Config Service to be ready..."
check_health "Config Service" "http://localhost:8888/actuator/health"

# Start all other services concurrently
start_service "profile-service" "profile-service" 18081
start_service "preference-service" "preference-service" 18082
start_service "consent-service" "consent-service" 18083
start_service "segment-service" "segment-service" 18084
start_service "api-gateway" "api-gateway" 9880

echo -e "\n${YELLOW}Verifying deployments...${NC}"

# Verify Health
check_health "Profile Service" "http://localhost:18081/actuator/health"
check_health "Preference Service" "http://localhost:18082/actuator/health"
check_health "Consent Service" "http://localhost:18083/actuator/health"
check_health "Segment Service" "http://localhost:18084/actuator/health"
check_health "API Gateway" "http://localhost:9880/actuator/health"

echo -e "\n${GREEN}✅ Launch Sequence Complete!${NC}"
echo -e "Unified API Gateway is available at: ${GREEN}http://localhost:9880${NC}"
echo -e "Config Server is available at: ${GREEN}http://localhost:8888${NC}"
