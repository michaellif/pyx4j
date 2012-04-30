#!/bin/bash

mysqldump -ubkup_usr -pgr8expect# --compatible=postgresql --no-create-info --complete_insert --skip-add-locks --skip-opt --order_by_primary --databases pangroup_migration_crm > pangroup_inserts_crm.sql
