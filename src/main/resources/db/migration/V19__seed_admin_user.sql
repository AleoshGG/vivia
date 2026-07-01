INSERT INTO users (id, name, paternal_surname, maternal_surname, email, photo_url, created_at, updated_at)
VALUES ('00000000-0000-0000-0000-000000000001', 'Admin', 'Vivia', '', 'admin@vivia.com', 'No photo', NOW(), NOW())
ON CONFLICT DO NOTHING;

INSERT INTO credentials (id, user_id, credential_type, secret_data)
VALUES ('00000000-0000-0000-0000-000000000002',
        '00000000-0000-0000-0000-000000000001',
        'PASSWORD',
        '$2b$10$zrmy59mYeBFfGyQZyerAzeunrh.gioo9AO7MitAKoqtstPKls1EHy')
ON CONFLICT DO NOTHING;

INSERT INTO admins (user_id) VALUES ('00000000-0000-0000-0000-000000000001')
ON CONFLICT DO NOTHING;
