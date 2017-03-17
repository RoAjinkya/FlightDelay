#!/bin/bash
rm .gitignore
echo "gitPush.sh" >> .gitignore
echo ".gitignore" >> .gitignore
echo "*~" >> .gitignore
echo ".DS_Store" >> .gitignore
find * -size +50M -type f -print >> .gitignore
git add -A
git commit -m"$1"
git push
