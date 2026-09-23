CREATE TABLE users (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    minecraft_uuid CHAR(36) NOT NULL,
    minecraft_username VARCHAR(16) NOT NULL,
    discord_user_id BIGINT UNSIGNED NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    preexisting BOOLEAN NOT NULL DEFAULT FALSE,

    PRIMARY KEY (id),
    UNIQUE KEY uq_users_minecraft_uuid (minecraft_uuid),
    UNIQUE KEY uq_users_discord_user_id (discord_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE application_questionnaires (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE applications (
      id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
      user_id BIGINT UNSIGNED NOT NULL,
      questionnaire_id BIGINT UNSIGNED NOT NULL,
      status VARCHAR(32) NOT NULL,
      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
      submitted_at TIMESTAMP NULL,
      reviewed_at TIMESTAMP NULL,
      reviewed_by BIGINT UNSIGNED NULL,
      decision_reason TEXT NULL,

      PRIMARY KEY (id),

      KEY idx_applications_user_id (user_id),
      KEY idx_applications_status (status),

      CONSTRAINT fk_applications_user FOREIGN KEY (user_id) REFERENCES users(id),
      CONSTRAINT fk_applications_questionnaire FOREIGN KEY (questionnaire_id) REFERENCES application_questionnaires(id),
      CONSTRAINT fk_applications_reviewer FOREIGN KEY (reviewed_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE memberships (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id BIGINT UNSIGNED NOT NULL,
    started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ended_at TIMESTAMP NULL,
    reason_started TEXT NULL,
    reason_ended TEXT NULL,
    application_id BIGINT UNSIGNED NULL,

    PRIMARY KEY (id),

    KEY idx_memberships_user_id (user_id),
    KEY idx_memberships_application_id (application_id),

    CONSTRAINT fk_memberships_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_memberships_application FOREIGN KEY (application_id) REFERENCES applications(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE application_questions (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    question_key VARCHAR(64) NOT NULL,
    revision INT UNSIGNED NOT NULL,
    question TEXT NOT NULL,
    minimum_length INT UNSIGNED NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE KEY uq_application_questions_key_revision (question_key, revision)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE application_questionnaire_questions (
    questionnaire_id BIGINT UNSIGNED NOT NULL,
    question_id BIGINT UNSIGNED NOT NULL,
    position INT UNSIGNED NOT NULL,

    PRIMARY KEY (questionnaire_id, question_id),

    UNIQUE KEY uq_questionnaire_questions_position (questionnaire_id, position),

    KEY idx_questionnaire_questions_question (question_id),

    CONSTRAINT fk_questionnaire_questions_questionnaire FOREIGN KEY (questionnaire_id) REFERENCES application_questionnaires(id),
    CONSTRAINT fk_questionnaire_questions_question FOREIGN KEY (question_id) REFERENCES application_questions(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE application_answers (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    application_id BIGINT UNSIGNED NOT NULL,
    question_id BIGINT UNSIGNED NOT NULL,
    answer TEXT NOT NULL,

    PRIMARY KEY (id),
    UNIQUE KEY uq_application_answers_application_question (application_id, question_id),

    KEY idx_application_answers_question (question_id),

    CONSTRAINT fk_application_answers_application FOREIGN KEY (application_id) REFERENCES applications(id),
    CONSTRAINT fk_application_answers_question FOREIGN KEY (question_id) REFERENCES application_questions(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE bans (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id BIGINT UNSIGNED NOT NULL,
    reason TEXT NULL,
    banned_at TIMESTAMP NULL,
    banned_by BIGINT UNSIGNED NULL,
    expires_at TIMESTAMP NULL,
    revoked_at TIMESTAMP NULL,
    revoked_by BIGINT UNSIGNED NULL,
    revoked_reason TEXT NULL,

    PRIMARY KEY (id),

    KEY idx_bans_user (user_id),

    CONSTRAINT fk_bans_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_bans_banned_by FOREIGN KEY (banned_by) REFERENCES users(id),
    CONSTRAINT fk_bans_revoked_by FOREIGN KEY (revoked_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE change_events (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id BIGINT UNSIGNED NOT NULL,
    membership_id BIGINT UNSIGNED NULL,
    ban_id BIGINT UNSIGNED NULL,
    application_id BIGINT UNSIGNED NULL,
    event VARCHAR(32) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    KEY idx_change_events_user (user_id),
    KEY idx_change_events_event (event),

    CONSTRAINT fk_change_events_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_change_events_membership FOREIGN KEY (membership_id) REFERENCES memberships(id),
    CONSTRAINT fk_change_events_ban FOREIGN KEY (ban_id) REFERENCES bans(id),
    CONSTRAINT fk_change_events_application FOREIGN KEY (application_id) REFERENCES applications(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;