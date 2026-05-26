CREATE TABLE messages (
    id UUID PRIMARY KEY,
    room_id UUID NOT NULL REFERENCES rooms(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    sent_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_messages_room ON messages(room_id);
CREATE INDEX idx_messages_sent_at ON messages(sent_at);

CREATE TABLE connections (
    id UUID PRIMARY KEY,
    session_id VARCHAR(255) NOT NULL UNIQUE,
    room_id UUID NOT NULL REFERENCES rooms(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE SET NULL,
    connected_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_connections_session ON connections(session_id);
CREATE INDEX idx_connections_user ON connections(user_id);