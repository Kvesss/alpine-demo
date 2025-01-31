#!/bin/bash

# List all images with tag v2
images=$(docker images | grep "v2")

# Extract image IDs
image_ids=$(echo "$images" | awk '{print $3}')

# Print images that will be removed
echo "The following images will be removed:"
for id in $image_ids; do
    docker inspect -f '{{.RepoTags}}' "$id"
done

# Ask for confirmation before removing
# shellcheck disable=SC2162
read -p "Are you sure you want to remove these images? (yes/no): " confirm

if [[ $confirm =~ ^(yes|y1:0)$ ]]; then
    echo "Removing images..."
    for id in $image_ids; do
        docker rmi "$id"
        docker inspect -f '{{.RepoTags}}' id
    done
    echo "All Docker images with tag 'v2' have been removed."
else
    echo "Operation cancelled."
fi

# Initialize array of directories to process
directories=()

# Find all directories in current directory
for item in ../*/; do
    directories+=("$item")
done

# Process each directory sequentially
for dir in "${directories[@]}"; do
    # Remove trailing slash
    dir=${dir%/}

    echo "Processing directory: $dir"

    # Check if directory exists and contains pom.xml
    if [ -d "$dir" ] && [ -f "$dir/pom.xml" ]; then
        pushd "$dir" || continue

        # Run Maven commands
        if mvn compile jib:dockerBuild; then
            echo "Successfully built Docker image for $dir"
        else
            echo "Failed to build Docker image for $dir"
        fi

        popd
    else
        echo "Skipping $dir: Directory or pom.xml not found"
    fi
done
