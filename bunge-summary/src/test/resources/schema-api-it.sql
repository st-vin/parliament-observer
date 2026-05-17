CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    notification_frequency VARCHAR(20) NOT NULL DEFAULT 'DAILY',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS sittings (
    id UUID PRIMARY KEY,
    date DATE NOT NULL UNIQUE,
    chamber VARCHAR(50) NOT NULL DEFAULT 'NATIONAL_ASSEMBLY',
    session_number INTEGER,
    parliament_number INTEGER,
    raw_pdf_path TEXT NOT NULL,
    ingestion_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    ingested_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS topics (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    slug VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

CREATE TABLE IF NOT EXISTS proceedings (
    id UUID PRIMARY KEY,
    sitting_id UUID NOT NULL REFERENCES sittings (id) ON DELETE CASCADE,
    title TEXT NOT NULL,
    proceeding_type VARCHAR(50) NOT NULL,
    stage VARCHAR(50),
    outcome VARCHAR(50),
    plain_summary TEXT,
    raw_text TEXT NOT NULL,
    source_page_start INTEGER,
    source_page_end INTEGER,
    sequence_in_sitting INTEGER,
    confidence_score DECIMAL(3, 2),
    needs_review BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS proceeding_topics (
    proceeding_id UUID NOT NULL REFERENCES proceedings (id) ON DELETE CASCADE,
    topic_id UUID NOT NULL REFERENCES topics (id) ON DELETE CASCADE,
    confidence_score DECIMAL(3, 2),
    PRIMARY KEY (proceeding_id, topic_id)
);

CREATE TABLE IF NOT EXISTS members (
    id UUID PRIMARY KEY,
    full_name VARCHAR(200) NOT NULL,
    display_name VARCHAR(200),
    constituency VARCHAR(200),
    county VARCHAR(100),
    party VARCHAR(100),
    chamber VARCHAR(50) NOT NULL,
    term_start INTEGER,
    term_end INTEGER,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS contributions (
    id UUID PRIMARY KEY,
    proceeding_id UUID NOT NULL REFERENCES proceedings (id) ON DELETE CASCADE,
    member_id UUID NOT NULL REFERENCES members (id),
    summary TEXT,
    stance VARCHAR(20),
    stance_confidence DECIMAL(3, 2),
    verbatim_excerpt TEXT,
    excerpt_page INTEGER,
    sequence_in_proceeding INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_topic_subscriptions (
    user_id UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    topic_id UUID NOT NULL REFERENCES topics (id) ON DELETE CASCADE,
    subscribed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, topic_id)
);

INSERT INTO topics (id, name, slug) VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa301', 'Health', 'health'),
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa302', 'Education', 'education');

INSERT INTO sittings (id, date, chamber, raw_pdf_path, ingestion_status) VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa101', '2024-03-15', 'NATIONAL_ASSEMBLY', '/data/test.pdf', 'COMPLETED'),
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa102', '2024-04-10', 'NATIONAL_ASSEMBLY', '/data/test2.pdf', 'COMPLETED');

INSERT INTO proceedings (
    id, sitting_id, title, proceeding_type, outcome, plain_summary, raw_text, sequence_in_sitting
) VALUES
    (
        'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa201',
        'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa101',
        'Health Budget Allocation',
        'MOTION',
        'PASSED',
        'The House debated increased health funding.',
        'Raw Hansard text for health motion.',
        1
    ),
    (
        'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa202',
        'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa102',
        'Education Reform Bill',
        'BILL',
        'REFERRED',
        'MPs discussed curriculum reforms.',
        'Raw Hansard text for education bill.',
        1
    );

INSERT INTO proceeding_topics (proceeding_id, topic_id, confidence_score) VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa201', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa301', 0.95),
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa202', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa302', 0.88);

INSERT INTO members (
    id, full_name, display_name, constituency, party, chamber
) VALUES (
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa401',
    'Jane Wanjiku',
    'Hon. Jane Wanjiku',
    'Nairobi West',
    'Example Party',
    'NATIONAL_ASSEMBLY'
);

INSERT INTO contributions (
    id, proceeding_id, member_id, summary, stance, verbatim_excerpt, sequence_in_proceeding
) VALUES (
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa501',
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa201',
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa401',
    'Supported increased health allocation.',
    'FOR',
    'I rise to support this motion for the health sector.',
    1
);
