ALTER TABLE tickets
    ADD COLUMN recovery_code_hash VARCHAR(255);

ALTER TABLE tickets
    ADD COLUMN subject VARCHAR(255);