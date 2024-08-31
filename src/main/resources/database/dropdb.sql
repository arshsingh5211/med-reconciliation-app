-- **************************************************************
-- This script destroys the database and associated users
-- **************************************************************

-- The following line terminates any active connections to the database so that it can be destroyed
SELECT pg_terminate_backend(pid)
FROM pg_stat_activity
WHERE datname = 'med_reconciliation_app';

-- Drop the database if it exists
DROP DATABASE IF EXISTS med_reconciliation_app;

-- Drop the associated users if they exist
DROP ROLE IF EXISTS med_reconciliation_owner;
DROP ROLE IF EXISTS med_reconciliation_appuser;
