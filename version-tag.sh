#!/bin/bash

# Check if we're in a git repository
if ! git rev-parse --is-inside-work-tree > /dev/null 2>&1; then
    echo "This is not a git repository."
    exit 1
fi

# Get the most recent version tag
recent_tag=$(git describe --tags $(git rev-list --tags --max-count=1))

# Check if a tag was found
if [ -z "$recent_tag" ]; then
    echo "No tags found in the repository."
    exit 1
fi

# Remove the 'v' prefix from the tag
version_without_prefix=$(echo "$recent_tag" | sed 's/^v//')

# Output the recent tag without the 'v' prefix
echo "$version_without_prefix"
