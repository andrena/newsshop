-- Insert initial data for newsletters
INSERT INTO newsletter (email, name, source, confirmed, mail_properties) VALUES
('john.doe@example.com', 'John Doe', 'Website', true, '{"header": "<h1>Welcome</h1>", "footer": "<p>Thank you for subscribing!</p>"}'),
('jane.smith@example.com', 'Jane Smith', 'Social Media', false, '{"header": "<h1>Welcome</h1>", "footer": "<p>Thank you for subscribing!</p>"}'),
('alice.jones@example.com', 'Alice Jones', 'Friend', true, '{"header": "<h1>Welcome</h1>", "footer": "<p>Thank you for subscribing!</p>"}');

-- Insert initial data for passwords
INSERT INTO password (username, password) VALUES
('admin', 'admin'),
('king.roland', '12345'),
('user', 'password');
