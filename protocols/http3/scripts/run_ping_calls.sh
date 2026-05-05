#!/usr/bin/env bash

set -euo pipefail

export PATH="$HOME/.cargo/bin:$PATH"

if [ "${NO_COLOR:-}" = "1" ]; then
  export NO_COLOR=true
fi

TARGET="http1"
OHA_ARGS=()

usage() {
  printf "Usage: %s [-target http1|http2|http3] [oha options]\n" "$0" >&2
  printf "Default oha options: -n 100 -c 10 -t 5s --no-tui\n" >&2
  printf "Example: %s -target http3 -n 1000 -c 100 -t 10s\n" "$0" >&2
}

while [ "$#" -gt 0 ]; do
  case "$1" in
    -target|--target)
      if [ "$#" -lt 2 ]; then
        usage
        exit 1
      fi
      TARGET="$2"
      shift 2
      ;;
    -h|--help)
      usage
      printf "\nPass any oha option after -target. For full options:\n" >&2
      oha --help >&2
      exit 0
      ;;
    *)
      OHA_ARGS+=("$1")
      shift
      ;;
  esac
done

case "$TARGET" in
  http1)
    URL="http://127.0.0.1:8080/ping"
    TARGET_ARGS=(--http-version 1.1)
    ;;
  http2)
    URL="https://127.0.0.1:8443/ping"
    TARGET_ARGS=(--http2 --insecure)
    ;;
  http3)
    URL="https://127.0.0.1:8443/ping"
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

printf "\n== %s ping: %s ==\n" "$TARGET" "$URL"
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
