#!/bin/bash

mysqldump -ubkup_usr -pgr8expect# --compatible=postgresql --no-create-info --complete_insert --skip-add-locks --skip-opt --databases pangroup_migration_admin > pangroup_inserts_admin.sql
