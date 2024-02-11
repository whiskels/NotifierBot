ALTER TABLE load_audit RENAME COLUMN loader TO report_type;

UPDATE load_audit
SET report_type = CASE report_type
                      WHEN 'FINANCIAL_OPERATION' THEN 'CUSTOMER_PAYMENT'
                      WHEN 'EMPLOYEE' THEN 'EMPLOYEE_BIRTHDAY'
                      WHEN 'DEBT' THEN 'CUSTOMER_DEBT'
                      ELSE report_type
    END
WHERE report_type IN ('FINANCIAL_OPERATION', 'EMPLOYEE', 'DEBT');