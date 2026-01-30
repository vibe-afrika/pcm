#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

LOG_DIR="logs"

echo -e "${YELLOW}🛑 Stopping PCM Platform Services...${NC}"

# 1. Stop Java Services
for pid_file in $LOG_DIR/*.pid; do
    if [ -f "$pid_file" ]; then
        SERVICE_NAME=$(basename "$pid_file" .pid)
        PID=$(cat "$pid_file")
        
        if ps -p $PID > /dev/null; then
             echo "Stopping $SERVICE_NAME (PID: $PID)..."
             kill $PID
             rm "$pid_file"
        else
             echo "$SERVICE_NAME (PID: $PID) is not running. Cleaning up..."
             rm "$pid_file"
        fi
    fi
done

echo -e "${GREEN}Java services stopped.${NC}"

# 2. Stop Docker (Optional)
read -p "Do you want to stop the Docker infrastructure? (y/N) " response
if [[ "$response" =~ ^[Yy]$ ]]; then
    echo "Stopping Docker containers..."
    docker-compose stop
    echo -e "${GREEN}Infrastructure stopped.${NC}"
else
    echo "Infrastructure left running."
fi
