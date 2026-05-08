#!/usr/bin/env bash

set -euo pipefail

export PATH="$HOME/.cargo/bin:$PATH"

if [ "${NO_COLOR:-}" = "1" ]; then
  export NO_COLOR=true
fi

ENDPOINT="${ENDPOINT:-ping}"
TARGET="http1"
OHA_ARGS=()

usage() {
  printf "Usage: %s [-endpoint ping|payload|stream] [-target http1|http2|http3] [endpoint options] [oha options]\n" "$0" >&2
  printf "Endpoint options:\n" >&2
  printf "  -bytes N   payload response size, default 1024\n" >&2
  printf "  -count N   stream event count, default 10\n" >&2
  printf "Default oha options: -n 100 -c 10 -t 5s --no-tui\n" >&2
}

BYTES="1024"
COUNT="10"

while [ "$#" -gt 0 ]; do
  case "$1" in
    -endpoint|--endpoint)
      if [ "$#" -lt 2 ]; then
        usage
        exit 1
      fi
      ENDPOINT="$2"
      shift 2
      ;;
    -target|--target)
      if [ "$#" -lt 2 ]; then
        usage
        exit 1
      fi
      TARGET="$2"
      shift 2
      ;;
    -bytes|--bytes)
      if [ "$#" -lt 2 ]; then
        usage
        exit 1
      fi
      BYTES="$2"
      shift 2
      ;;
    -count|--count)
      if [ "$#" -lt 2 ]; then
        usage
        exit 1
      fi
      COUNT="$2"
      shift 2
      ;;
    -h|--help)
      usage
      printf "\nPass any oha option after script options. For full oha options:\n" >&2
      oha --help >&2
      exit 0
      ;;
    *)
      OHA_ARGS+=("$1")
      shift
      ;;
  esac
done

case "$ENDPOINT" in
  ping)
    PATH_AND_QUERY="/ping"
    ;;
  payload)
    PATH_AND_QUERY="/payload?bytes=$BYTES"
    ;;
  stream)
    PATH_AND_QUERY="/stream?count=$COUNT"
    ;;
  *)
    usage
    exit 1
    ;;
esac

case "$TARGET" in
  http1)
    URL="https://127.0.0.1:8080$PATH_AND_QUERY"
    TARGET_ARGS=(--http-version 1.1)
    TARGET_ARGS+=(--insecure)
    ;;
  http2)
    URL="https://127.0.0.1:8443$PATH_AND_QUERY"
    TARGET_ARGS=(--http2 --insecure)
    ;;
  http3)
    URL="https://127.0.0.1:8443$PATH_AND_QUERY"
    TARGET_ARGS=(--http-version 3 --insecure)
    ;;
  *)
    usage
    exit 1
    ;;
esac

has_oha_option() {
  short="$1"
  long="$2"

  if [ "${#OHA_ARGS[@]}" -eq 0 ]; then
    return 1
  fi

  for arg in "${OHA_ARGS[@]}"; do
    if [ -n "$short" ]; then
      case "$arg" in
        "$short"|"$short"*)
          return 0
          ;;
      esac
    fi

    if [ -n "$long" ]; then
      case "$arg" in
        "$long")
          return 0
          ;;
      esac
    fi
  done

  return 1
}

DEFAULT_OHA_ARGS=()
if ! has_oha_option "-n" ""; then
  DEFAULT_OHA_ARGS+=(-n 100)
fi
if ! has_oha_option "-c" ""; then
  DEFAULT_OHA_ARGS+=(-c 10)
fi
if ! has_oha_option "-t" "--timeout"; then
  DEFAULT_OHA_ARGS+=(-t 5s)
fi
if ! has_oha_option "" "--no-tui"; then
  DEFAULT_OHA_ARGS+=(--no-tui)
fi

printf "\n== %s %s: %s ==\n" "$TARGET" "$ENDPOINT" "$URL"
CMD=(oha)
CMD+=("${TARGET_ARGS[@]}")
if [ "${#DEFAULT_OHA_ARGS[@]}" -gt 0 ]; then
  CMD+=("${DEFAULT_OHA_ARGS[@]}")
fi
if [ "${#OHA_ARGS[@]}" -gt 0 ]; then
  CMD+=("${OHA_ARGS[@]}")
fi
CMD+=("$URL")

"${CMD[@]}"
