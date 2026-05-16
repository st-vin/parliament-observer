-- V1: Core relational schema (SRS §5)
-- proceeding_chunks + pgvector live in V2 (T-005)

CREATE TABLE sittings (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    date                DATE NOT NULL UNIQUE,
    chamber             VARCHAR(50) NOT NULL DEFAULT 'NATIONAL_ASSEMBLY',
    session_number      INTEGER,
    parliament_number   INTEGER,
    raw_pdf_path        TEXT NOT NULL,
    ingestion_status    VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    ingested_at         TIMESTAMP,
    created_at          TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE proceedings (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sitting_id          UUID NOT NULL REFERENCES sittings (id) ON DELETE CASCADE,
    title               TEXT NOT NULL,
    proceeding_type     VARCHAR(50) NOT NULL,
    stage               VARCHAR(50),
    outcome             VARCHAR(50),
    plain_summary       TEXT,
    raw_text            TEXT NOT NULL,
    source_page_start   INTEGER,
    source_page_end     INTEGER,
    sequence_in_sitting INTEGER,
    confidence_score    DECIMAL(3, 2),
    needs_review        BOOLEAN NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_proceedings_sitting_id ON proceedings (sitting_id);
CREATE INDEX idx_proceedings_created_at ON proceedings (created_at DESC);

CREATE TABLE topics (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(100) NOT NULL UNIQUE,
    slug        VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

CREATE TABLE proceeding_topics (
    proceeding_id     UUID NOT NULL REFERENCES proceedings (id) ON DELETE CASCADE,
    topic_id          UUID NOT NULL REFERENCES topics (id) ON DELETE CASCADE,
    confidence_score  DECIMAL(3, 2),
    PRIMARY KEY (proceeding_id, topic_id)
);

CREATE INDEX idx_proceeding_topics_topic_id ON proceeding_topics (topic_id);

CREATE TABLE members (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    full_name     VARCHAR(200) NOT NULL,
    display_name  VARCHAR(200),
    constituency  VARCHAR(200),
    county        VARCHAR(100),
    party         VARCHAR(100),
    chamber       VARCHAR(50) NOT NULL,
    term_start    INTEGER,
    term_end      INTEGER,
    is_active     BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_members_full_name ON members (full_name);
CREATE INDEX idx_members_chamber ON members (chamber);

CREATE TABLE contributions (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    proceeding_id           UUID NOT NULL REFERENCES proceedings (id) ON DELETE CASCADE,
    member_id               UUID NOT NULL REFERENCES members (id) ON DELETE RESTRICT,
    summary                 TEXT,
    stance                  VARCHAR(20),
    stance_confidence       DECIMAL(3, 2),
    verbatim_excerpt        TEXT,
    excerpt_page            INTEGER,
    sequence_in_proceeding  INTEGER,
    created_at              TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_contributions_proceeding_id ON contributions (proceeding_id);
CREATE INDEX idx_contributions_member_id ON contributions (member_id);

CREATE TABLE users (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email                   VARCHAR(255) NOT NULL UNIQUE,
    password_hash           TEXT NOT NULL,
    email_verified          BOOLEAN NOT NULL DEFAULT FALSE,
    notification_frequency  VARCHAR(20) NOT NULL DEFAULT 'DAILY',
    created_at              TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE user_topic_subscriptions (
    user_id       UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    topic_id      UUID NOT NULL REFERENCES topics (id) ON DELETE CASCADE,
    subscribed_at TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (user_id, topic_id)
);

CREATE TABLE ingestion_log (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sitting_id              UUID REFERENCES sittings (id) ON DELETE SET NULL,
    started_at              TIMESTAMP,
    completed_at            TIMESTAMP,
    status                  VARCHAR(20),
    proceedings_extracted   INTEGER,
    tokens_used             INTEGER,
    error_message           TEXT
);

CREATE INDEX idx_ingestion_log_sitting_id ON ingestion_log (sitting_id);
