CREATE DATABASE svrms_customer;
CREATE DATABASE svrms_infrastructure;
CREATE DATABASE svrms_auth_db;
CREATE DATABASE svrms_notification_db;
CREATE DATABASE svrms_company_db;
CREATE DATABASE svrms_booking_db;

GRANT ALL PRIVILEGES ON DATABASE svrms_customer TO svrms;
GRANT ALL PRIVILEGES ON DATABASE svrms_infrastructure TO svrms;
GRANT ALL PRIVILEGES ON DATABASE svrms_auth_db TO svrms;
GRANT ALL PRIVILEGES ON DATABASE svrms_notification_db TO svrms;
GRANT ALL PRIVILEGES ON DATABASE svrms_company_db TO svrms;
GRANT ALL PRIVILEGES ON DATABASE svrms_booking_db TO svrms;
