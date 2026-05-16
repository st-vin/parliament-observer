-- V2: pgvector extension + semantic search chunks (SRS §5, TDD §7)

CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE proceeding_chunks (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    proceeding_id   UUID NOT NULL REFERENCES proceedings (id) ON DELETE CASCADE,
    chunk_text      TEXT NOT NULL,
    chunk_index     INTEGER NOT NULL,
    source_page     INTEGER,
    embedding       vector(768),
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_proceeding_chunks_proceeding_id ON proceeding_chunks (proceeding_id);

CREATE INDEX idx_proceeding_chunks_embedding
    ON proceeding_chunks
    USING ivfflat (embedding vector_cosine_ops)
    WITH (lists = 100);
