-- Insert catalog data for property types and amenities

-- Property Types
INSERT INTO property_type (id, name) VALUES
    ('550e8400-e29b-41d4-a716-446655440001', 'Casa'),
    ('550e8400-e29b-41d4-a716-446655440002', 'Departamento'),
    ('550e8400-e29b-41d4-a716-446655440003', 'Terreno'),
    ('550e8400-e29b-41d4-a716-446655440004', 'Local Comercial'),
    ('550e8400-e29b-41d4-a716-446655440005', 'Oficina'),
    ('550e8400-e29b-41d4-a716-446655440006', 'Bodega');

-- Amenities
INSERT INTO amenity (id, name) VALUES
    ('660e8400-e29b-41d4-a716-446655440001', 'Alberca'),
    ('660e8400-e29b-41d4-a716-446655440002', 'Gimnasio'),
    ('660e8400-e29b-41d4-a716-446655440003', 'Roof Garden'),
    ('660e8400-e29b-41d4-a716-446655440004', 'Seguridad 24h'),
    ('660e8400-e29b-41d4-a716-446655440005', 'Estacionamiento Techado'),
    ('660e8400-e29b-41d4-a716-446655440006', 'Área de Juegos Infantiles'),
    ('660e8400-e29b-41d4-a716-446655440007', 'Jardín'),
    ('660e8400-e29b-41d4-a716-446655440008', 'Terraza'),
    ('660e8400-e29b-41d4-a716-446655440009', 'Salón de Eventos'),
    ('660e8400-e29b-41d4-a716-446655440010', 'Cancha Deportiva'),
    ('660e8400-e29b-41d4-a716-446655440011', 'Circuito Cerrado (CCTV)'),
    ('660e8400-e29b-41d4-a716-446655440012', 'Acceso Controlado'),
    ('660e8400-e29b-41d4-a716-446655440013', 'Elevador'),
    ('660e8400-e29b-41d4-a716-446655440014', 'Área de BBQ'),
    ('660e8400-e29b-41d4-a716-446655440015', 'Pet Friendly'),
    ('660e8400-e29b-41d4-a716-446655440016', 'Cuarto de Servicio'),
    ('660e8400-e29b-41d4-a716-446655440017', 'Cocina Integral'),
    ('660e8400-e29b-41d4-a716-446655440018', 'Closets'),
    ('660e8400-e29b-41d4-a716-446655440019', 'Calentador de Agua'),
    ('660e8400-e29b-41d4-a716-446655440020', 'Aire Acondicionado'),
    ('660e8400-e29b-41d4-a716-446655440021', 'Amueblado'),
    ('660e8400-e29b-41d4-a716-446655440022', 'Semi-Amueblado');

-- Total: 6 property types, 22 amenities
