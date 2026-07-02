CREATE TABLE report_reasons (
    id          VARCHAR(50)  NOT NULL,
    name        VARCHAR(100) NOT NULL,
    description TEXT         NOT NULL,
    priority    VARCHAR(20)  NOT NULL,
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_report_reasons PRIMARY KEY (id)
);

INSERT INTO report_reasons (id, name, description, priority) VALUES
    (
        'rr-fraud',
        'Fraude o posible estafa',
        'Piden dinero por adelantado sin garantías, transferencias por fuera de la plataforma o la identidad del arrendador parece falsa.',
        'ALTA'
    ),
    (
        'rr-misleading',
        'Información falsa o engañosa',
        'Las fotografías no corresponden a la realidad, el precio publicado es incorrecto o las características como el número de habitaciones están alteradas.',
        'ALTA'
    ),
    (
        'rr-unavailable',
        'Propiedad no disponible',
        'El lugar ya fue rentado o vendido, pero el anuncio sigue activo en la plataforma para atraer usuarios.',
        'MEDIA'
    ),
    (
        'rr-behavior',
        'Comportamiento inadecuado del arrendador',
        'El dueño o agente fue ofensivo, discriminatorio o poco profesional durante el contacto.',
        'MEDIA'
    ),
    (
        'rr-spam',
        'Spam o contenido irrelevante',
        'La publicación no es una propiedad, contiene enlaces sospechosos o es publicidad no relacionada.',
        'BAJA'
    ),
    (
        'rr-other',
        'Otro',
        'Caso atípico que no encaja en las categorías anteriores. El usuario podrá explicarlo en el campo de comentarios.',
        'BAJA'
    );
