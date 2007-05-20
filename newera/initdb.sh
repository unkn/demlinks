#!/bin/bash
. ./head.sh

sudo -u postgres psql -f initdb.sql
sudo -u postgres createlang -e plpgsql "$dbase"
psql -U "$dbuser" "$dbase" -f initdbstructure.sql
