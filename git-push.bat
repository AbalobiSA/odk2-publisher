@echo off
echo Switching to master branch
git checkout master
echo Pushing to git...
git push origin master
echo Pushing to heroku...
git push heroku master
