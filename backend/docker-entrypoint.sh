#!/bin/sh
set -eu

mkdir -p /app/uploads
chown app:app /app/uploads

exec su-exec app "$@"
