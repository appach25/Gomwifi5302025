-- Create admin user with password: admin
INSERT INTO users (username, password, role, enabled)
VALUES ('admin', '$2a$10$WIcj9.sO/SxDxOTL2PMPkOYxHDEKMR4BYojVcXxUQAMxYPvTiLKYm', 'ADMIN', true);
