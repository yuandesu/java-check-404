#!/bin/bash

# Test script for the Java Check 404 API
# Make sure the application is running before executing this script

BASE_URL="http://localhost:8080/api"

echo "Testing Java Check 404 API"
echo "=============================="

# Test greeting endpoint
echo -e "\n1. Testing Greeting Endpoint:"
curl -s "$BASE_URL/greeting"

# Test 404 endpoint
echo -e "\n\n2. Testing 404 Endpoint:"
curl -s -w "HTTP Status: %{http_code}\n" "$BASE_URL/not-found" | jq '.' 2>/dev/null || curl -s -w "HTTP Status: %{http_code}\n" "$BASE_URL/not-found"

echo -e "\n\n API testing completed!"
echo "Check your Datadog dashboard for traces and metrics." 