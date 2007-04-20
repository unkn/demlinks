#!/bin/bash
sudo -u postgres psql -f initdb.sql
sudo -u postgres createlang -e plpgsql demlinks_db
psql -U demlinks_user demlinks_db -f initdbstructure.sql
