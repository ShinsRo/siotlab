#!/bin/sh

# Wait for Kafka Connect to start
echo "Waiting for Kafka Connect to start listening on localhost"
while : ; do
    curl_status=$(curl -s -o /dev/null -w %{http_code} http://localhost:8083/connectors)
    if [ $curl_status -eq 200 ] ; then
        break
    fi
    echo "Kafka Connect listener HTTP state: $curl_status (waiting for 200)"
    sleep 5
done
echo "Kafka Connect is ready"

# Function to create a connector
create_connector() {
    CONNECTOR_NAME=$1
    CONFIG_FILE=$2

    CONNECTOR_CHECK=$(curl -s -o /dev/null -w %{http_code} http://localhost:8083/connectors/${CONNECTOR_NAME})

    if [ $CONNECTOR_CHECK -eq 200 ]; then
        echo "Connector ${CONNECTOR_NAME} already exists. Skipping creation."
    else
        echo "Creating connector ${CONNECTOR_NAME}"
        curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" localhost:8083/connectors/ -d @${CONFIG_FILE}
        if [ $? -eq 0 ]; then
            echo "Connector ${CONNECTOR_NAME} created successfully"
        else
            echo "Failed to create connector ${CONNECTOR_NAME}"
        fi
    fi
}

# Create Debezium Source Connector
base_dir=$(dirname "$0")
create_connector "debezium-connector" "$base_dir/debezium-connector-config.json"

echo "Connector setup complete. Keeping the container running..."
tail -f /dev/null
