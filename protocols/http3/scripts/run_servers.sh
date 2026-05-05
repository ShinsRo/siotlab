#!/usr/bin/env sh

set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
SERVER_DIR="$SCRIPT_DIR/../server"
RUN_DIR="$SCRIPT_DIR/.run"

mkdir -p "$RUN_DIR"

start_server() {
  name="$1"
  task="$2"
  pid_file="$RUN_DIR/$name.pid"
  log_file="$RUN_DIR/$name.log"

  if [ -f "$pid_file" ]; then
    pid=$(cat "$pid_file")
    if kill -0 "$pid" 2>/dev/null; then
      printf "%s already running: pid=%s\n" "$name" "$pid"
      return
    fi
    rm -f "$pid_file"
  fi

  printf "starting %s with %s\n" "$name" "$task"
  (
    cd "$SERVER_DIR"
    ./gradlew "$task" >"$log_file" 2>&1
  ) &

  pid=$!
  printf "%s\n" "$pid" >"$pid_file"
  printf "%s started: pid=%s log=%s\n" "$name" "$pid" "$log_file"
}

start_server "http1" ":bootstraps:http1:bootRun"
start_server "http2" ":bootstraps:http2:bootRun"
start_server "http3" ":bootstraps:http3:bootRun"

printf "\nHTTP/1.1: http://localhost:8080/ping\n"
printf "HTTP/2:   https://localhost:8443/ping\n"
printf "HTTP/3:   https://localhost:8443/ping\n"
