CREATE TABLE users (
  id BIGSERIAL PRIMARY KEY, full_name VARCHAR(150) NOT NULL, email VARCHAR(180) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL, role VARCHAR(20) NOT NULL, active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE TABLE patients (
  id BIGSERIAL PRIMARY KEY, user_id BIGINT NOT NULL UNIQUE REFERENCES users(id), document_number VARCHAR(30) NOT NULL UNIQUE,
  birth_date DATE, phone VARCHAR(40), diabetes_type VARCHAR(40), glucose_target_min INTEGER, glucose_target_max INTEGER,
  emergency_contact VARCHAR(180), created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE TABLE glucose_measurements (
  id BIGSERIAL PRIMARY KEY, patient_id BIGINT NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
  value_mg_dl INTEGER NOT NULL CHECK (value_mg_dl BETWEEN 20 AND 600), measured_at TIMESTAMPTZ NOT NULL,
  context VARCHAR(30) NOT NULL, notes VARCHAR(500), created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_glucose_patient_time ON glucose_measurements(patient_id, measured_at DESC);
CREATE TABLE meals (
  id BIGSERIAL PRIMARY KEY, patient_id BIGINT NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
  name VARCHAR(180) NOT NULL, meal_type VARCHAR(30) NOT NULL, eaten_at TIMESTAMPTZ NOT NULL,
  carbohydrates_grams NUMERIC(8,2), calories INTEGER, photo_url VARCHAR(500), notes VARCHAR(500),
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_meals_patient_time ON meals(patient_id, eaten_at DESC);
CREATE TABLE medications (
  id BIGSERIAL PRIMARY KEY, patient_id BIGINT NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
  name VARCHAR(180) NOT NULL, dose VARCHAR(100) NOT NULL, frequency VARCHAR(100), reminder_time TIME,
  start_date DATE, end_date DATE, active BOOLEAN NOT NULL DEFAULT TRUE, instructions VARCHAR(500),
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE TABLE alerts (
  id BIGSERIAL PRIMARY KEY, patient_id BIGINT NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
  type VARCHAR(40) NOT NULL, severity VARCHAR(20) NOT NULL, title VARCHAR(180) NOT NULL, message VARCHAR(1000) NOT NULL,
  occurred_at TIMESTAMPTZ NOT NULL, acknowledged BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_alerts_patient_time ON alerts(patient_id, occurred_at DESC);
CREATE TABLE medical_reports (
  id BIGSERIAL PRIMARY KEY, patient_id BIGINT NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
  period_start DATE NOT NULL, period_end DATE NOT NULL, average_glucose INTEGER, readings_in_range_percent INTEGER,
  summary VARCHAR(2000), document_url VARCHAR(500), created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE TABLE user_settings (
  id BIGSERIAL PRIMARY KEY, user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
  glucose_alerts BOOLEAN NOT NULL DEFAULT TRUE, medication_reminders BOOLEAN NOT NULL DEFAULT TRUE,
  meal_reminders BOOLEAN NOT NULL DEFAULT TRUE, locale VARCHAR(20) NOT NULL DEFAULT 'es-PE',
  timezone VARCHAR(80) NOT NULL DEFAULT 'America/Lima', created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

