#!/bin/bash
sudo -u postgres psql -f initdb.sql
sudo -u postgres createlang -e plpgsql demlinks_db
sudo -u postgres psql -f initdbstructure.sql
