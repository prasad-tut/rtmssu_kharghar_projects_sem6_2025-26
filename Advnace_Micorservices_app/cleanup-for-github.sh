#!/bin/bash
echo "=========================================="
echo "  Cleaning up repository for GitHub      "
echo "=========================================="

# Remove all Maven target directories
echo "[1/6] Removing Maven target directories..."
find . -type d -name "target" -exec rm -rf {} + 2>/dev/null
echo "✓ Target directories removed"

# Remove all .log files
echo "[2/6] Removing log files..."
find . -type f -name "*.log" -delete 2>/dev/null
echo "✓ Log files removed"

# Remove all .pid files
echo "[3/6] Removing PID files..."
find . -type f -name "*.pid" -delete 2>/dev/null
echo "✓ PID files removed"

# Remove .DS_Store files (macOS)
echo "[4/6] Removing .DS_Store files..."
find . -type f -name ".DS_Store" -delete 2>/dev/null
echo "✓ .DS_Store files removed"

# Remove .class files
echo "[5/6] Removing compiled .class files..."
find . -type f -name "*.class" -delete 2>/dev/null
echo "✓ Class files removed"

# Remove IDE files
echo "[6/6] Removing IDE files..."
rm -rf .idea/ 2>/dev/null
find . -type f -name "*.iml" -delete 2>/dev/null
echo "✓ IDE files removed"

echo "=========================================="
echo "  Cleanup Complete! ✓                    "
echo "=========================================="
echo ""
echo "Repository is now ready for GitHub!"
echo "Removed:"
echo "  - Maven target/ directories"
echo "  - Log files (*.log)"
echo "  - PID files (*.pid)"
echo "  - .DS_Store files"
echo "  - Compiled .class files"
echo "  - IDE configuration files"
