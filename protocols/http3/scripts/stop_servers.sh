#!/usr/bin/env sh

set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
RUN_DIR="$SCRIPT_DIR/.run"

stop_server() {
  name="$1"
  pid_file="$RUN_DIR/$name.pid"

  if [ ! -f "$pid_file" ]; then
    printf "%s not running: missing %s\n" "$name" "$pid_file"
    return
  fi

  pid=$(cat "$pid_file")
  if ! kill -0 "$pid" 2>/dev/null; then
    printf "%s not running: stale pid=%s\n" "$name" "$pid"
    rm -f "$pid_file"
    return
  fi

  printf "stopping %s: pid=%s\n" "$name" "$pid"
  kill "$pid"

  attempts=0
  while kill -0 "$pid" 2>/dev/null; do
    attempts=$((attempts + 1))
    if [ "$attempts" -ge 30 ]; then
      printf "%s still running after TERM: pid=%s\n" "$name" "$pid" >&2
      return 1
    fi
    sleep 1
  done

  rm -f "$pid_file"
  printf "%s stopped\n" "$name"
}

stop_server "http1"
stop_server "http2"
stop_server "http3"

printf "\nchecking server ports...\n"
if ! command -v lsof >/dev/null 2>&1; then
  printf "lsof not found; skipped port check\n" >&2
  exit 0
fi

set +e
PORTS=$(lsof -nP -iUDP:8443 -iTCP:8443 -iTCP:8080 2>/dev/null)
STATUS=$?
set -e

if [ "$STATUS" -eq 0 ]; then
  printf "%s\n" "$PORTS"
  printf "\nserver ports are still in use; stop may be incomplete\n" >&2
  exit 1
fi

printf "server ports are clear: UDP 8443, TCP 8443, TCP 8080\n"
