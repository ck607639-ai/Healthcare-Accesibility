#!/usr/bin/env bash
# Script to push the AccessCare codebase to GitHub

if [ -z "$1" ]; then
  echo "Error: GitHub Personal Access Token required."
  echo "Usage: ./push_to_github.sh <YOUR_GITHUB_TOKEN>"
  exit 1
fi

TOKEN="$1"
REPO="https://${TOKEN}@github.com/ck607639-ai/Healthcare-Accesibility.git"

git branch -M main
git remote set-url origin "$REPO"
git push -u origin main

if [ $? -eq 0 ]; then
  echo "Successfully pushed AccessCare to https://github.com/ck607639-ai/Healthcare-Accesibility"
else
  echo "Failed to push. Please verify token permissions (repo scope)."
fi
