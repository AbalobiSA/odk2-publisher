# ODK 2 Publisher
A Java script to pull new entries from ODK 2 Aggregate, and publish to OpenFn.

Desktop for this app is at Heroku:
https://dashboard.heroku.com/apps/odk2-publisher

### Technical information
- Run using: `heroku run "sh target/bin/testpost"``

### Repo Syncing
This repo is stored in two locations.
1. [Heroku](https://dashboard.heroku.com/apps/odk2-publisher)
2. [Github](https://github.com/AbalobiSA/odk2-publisher)

### Getting Started

Clone the repo from heroku:

    $ heroku git:clone -a odk2-publisher

Add the github repo as origin

    $ git remote add origin https://github.com/AbalobiSA/odk2-publisher.git

You will now have two remotes:
- Heroku
- Origin
