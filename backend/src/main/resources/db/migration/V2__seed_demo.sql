-- La contraseña de ambas cuentas demo es: password
INSERT INTO users (id, full_name, email, password_hash, role) VALUES
 (1, 'Javier Mendoza', 'paciente@glucontrol.pe', '$2a$10$bgtmTLbbzQ3mU5Z0AeMkVeG5ab4SovuS3CARtJcAa4fbN58Dw.Mwq', 'PATIENT'),
 (2, 'Dra. Elena Rojas', 'medico@glucontrol.pe', '$2a$10$bgtmTLbbzQ3mU5Z0AeMkVeG5ab4SovuS3CARtJcAa4fbN58Dw.Mwq', 'DOCTOR'),
 (3, 'María Valenzuela', 'maria@glucontrol.pe', '$2a$10$bgtmTLbbzQ3mU5Z0AeMkVeG5ab4SovuS3CARtJcAa4fbN58Dw.Mwq', 'PATIENT');
SELECT setval('users_id_seq', 3);
INSERT INTO patients (id,user_id,document_number,birth_date,phone,diabetes_type,glucose_target_min,glucose_target_max,emergency_contact) VALUES
 (1,1,'72649182','1986-04-19','+51 987 654 321','Tipo 2',70,180,'Ana Mendoza · +51 999 111 222'),
 (2,3,'41928731','1972-09-08','+51 966 123 987','Tipo 1',70,180,'Carlos Valenzuela · +51 955 222 111');
SELECT setval('patients_id_seq', 2);
INSERT INTO glucose_measurements (patient_id,value_mg_dl,measured_at,context,notes) VALUES
 (1,105,NOW()-INTERVAL '40 minutes','AFTER_MEAL','Después del desayuno'),
 (1,96,NOW()-INTERVAL '8 hours','FASTING','Al despertar'),
 (1,118,NOW()-INTERVAL '1 day','AFTER_MEAL','Después de la cena'),
 (1,108,NOW()-INTERVAL '2 days','BEFORE_MEAL','Antes del almuerzo'),
 (2,248,NOW()-INTERVAL '12 minutes','AFTER_MEAL','Lectura elevada');
INSERT INTO meals (patient_id,name,meal_type,eaten_at,carbohydrates_grams,calories,photo_url,notes) VALUES
 (1,'Pollo a la plancha con ensalada','LUNCH',NOW()-INTERVAL '2 hours',32,485,'https://images.unsplash.com/photo-1543362906-acfc16c67564?auto=format&fit=crop&w=900&q=80','Porción equilibrada'),
 (1,'Avena con frutas','BREAKFAST',NOW()-INTERVAL '8 hours',45,340,NULL,'Sin azúcar añadida');
INSERT INTO medications (patient_id,name,dose,frequency,reminder_time,start_date,active,instructions) VALUES
 (1,'Metformina','850 mg','2 veces al día','08:00',CURRENT_DATE-30,TRUE,'Tomar con alimentos'),
 (1,'Losartán','50 mg','1 vez al día','20:00',CURRENT_DATE-90,TRUE,'Tomar con agua'),
 (2,'Insulina Glargina','18 UI','1 vez al día','21:00',CURRENT_DATE-120,TRUE,'Aplicación subcutánea');
INSERT INTO alerts (patient_id,type,severity,title,message,occurred_at,acknowledged) VALUES
 (1,'MEDICATION','INFO','Próximo medicamento','Metformina 850 mg a las 20:00',NOW()+INTERVAL '2 hours',FALSE),
 (1,'HIGH_GLUCOSE','WARNING','Tendencia al alza','Dos lecturas recientes aumentaron respecto al promedio.',NOW()-INTERVAL '1 day',FALSE),
 (2,'HIGH_GLUCOSE','CRITICAL','Hiperglucemia crítica','Lectura de 248 mg/dL. Requiere seguimiento.',NOW()-INTERVAL '12 minutes',FALSE);
INSERT INTO medical_reports (patient_id,period_start,period_end,average_glucose,readings_in_range_percent,summary) VALUES
 (1,CURRENT_DATE-30,CURRENT_DATE,108,92,'Control estable. Mantener alimentación, actividad y adherencia a la medicación.');
INSERT INTO user_settings (user_id,glucose_alerts,medication_reminders,meal_reminders) VALUES (1,TRUE,TRUE,TRUE),(2,TRUE,FALSE,FALSE),(3,TRUE,TRUE,TRUE);
