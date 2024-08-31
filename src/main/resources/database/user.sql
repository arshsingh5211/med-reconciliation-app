-- ********************************************************************************
-- This script creates the database users and grants them the necessary permissions
-- ********************************************************************************

CREATE USER med_reconciliation_owner
WITH PASSWORD 'medreconowner';

GRANT ALL
ON ALL TABLES IN SCHEMA public
TO med_reconciliation_owner;

GRANT ALL
ON ALL SEQUENCES IN SCHEMA public
TO med_reconciliation_owner;

CREATE USER med_reconciliation_appuser
WITH PASSWORD 'medreconuser';

GRANT SELECT, INSERT, UPDATE, DELETE
ON ALL TABLES IN SCHEMA public
TO med_reconciliation_appuser;

GRANT USAGE, SELECT
ON ALL SEQUENCES IN SCHEMA public
TO med_reconciliation_appuser;