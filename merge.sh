#!/bin/bash
find . -type f | grep .java | xargs cat > code.txt
