--
-- PostgreSQL database dump (MODIFICADO para Query Tool)
--

-- **1. Configuración de Sesión**
SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';
SET default_table_access_method = heap;

--
-- 2. Creación de Tablas y Secuencias
--

-- addresses
CREATE TABLE public.addresses (
    address_id integer NOT NULL,
    line_1_number_building character varying(100) NOT NULL,
    line_2_number_street character varying(100),
    line_3_area_locality character varying(100),
    city character varying(50) NOT NULL,
    zip_postcode character varying(20) NOT NULL,
    state_province_county character varying(50),
    country character varying(50) NOT NULL,
    is_primary_address boolean DEFAULT false NOT NULL,
    geolocation_code character varying(50)
);
ALTER TABLE public.addresses OWNER TO developer;

-- addresses_address_id_seq
CREATE SEQUENCE public.addresses_address_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE public.addresses_address_id_seq OWNER TO developer;
ALTER SEQUENCE public.addresses_address_id_seq OWNED BY public.addresses.address_id;


-- staff
CREATE TABLE public.staff (
    staff_id integer NOT NULL,
    staff_category_code character varying(10) NOT NULL,
    staff_job_title character varying(100) NOT NULL,
    gender character(1),
    staff_first_name character varying(50) NOT NULL,
    staff_middle_name character varying(50),
    staff_last_name character varying(50) NOT NULL,
    staff_qualifications text,
    staff_birth_date date,
    is_active boolean DEFAULT true NOT NULL,
    emergency_contact_name character varying(100),
    CONSTRAINT staff_gender_check CHECK ((gender = ANY (ARRAY['M'::bpchar, 'F'::bpchar, 'O'::bpchar])))
);
ALTER TABLE public.staff OWNER TO developer;

-- staff_staff_id_seq
CREATE SEQUENCE public.staff_staff_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE public.staff_staff_id_seq OWNER TO developer;
ALTER SEQUENCE public.staff_staff_id_seq OWNED BY public.staff.staff_id;


-- patients
CREATE TABLE public.patients (
    patient_id integer NOT NULL,
    outpatient_yn boolean DEFAULT true NOT NULL,
    hospital_number character varying(20),
    nhs_number character varying(20),
    gender character(1),
    date_of_birth date NOT NULL,
    patient_first_name character varying(50) NOT NULL,
    patient_middle_name character varying(50),
    patient_last_name character varying(50) NOT NULL,
    height numeric(5,2),
    weight numeric(5,2),
    next_of_kin character varying(100),
    home_phone character varying(20),
    work_phone character varying(20),
    cell_mobile_phone character varying(20),
    insurance_provider character varying(100),
    known_allergies text,
    CONSTRAINT patients_gender_check CHECK ((gender = ANY (ARRAY['M'::bpchar, 'F'::bpchar, 'O'::bpchar])))
);
ALTER TABLE public.patients OWNER TO developer;

-- patients_patient_id_seq
CREATE SEQUENCE public.patients_patient_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE public.patients_patient_id_seq OWNER TO developer;
ALTER SEQUENCE public.patients_patient_id_seq OWNED BY public.patients.patient_id;


-- patient_payment_methods
CREATE TABLE public.patient_payment_methods (
    patient_method_id integer NOT NULL,
    patient_id integer NOT NULL,
    payment_method_code character varying(20) NOT NULL,
    payment_method_details text
);
ALTER TABLE public.patient_payment_methods OWNER TO developer;

-- patient_payment_methods_patient_method_id_seq
CREATE SEQUENCE public.patient_payment_methods_patient_method_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE public.patient_payment_methods_patient_method_id_seq OWNER TO developer;
ALTER SEQUENCE public.patient_payment_methods_patient_method_id_seq OWNED BY public.patient_payment_methods.patient_method_id;


-- patient_bills
CREATE TABLE public.patient_bills (
    patient_bill_id integer NOT NULL,
    patient_id integer NOT NULL,
    date_bill_paid date,
    total_amount_due numeric(10,2) NOT NULL,
    payment_status character varying(20) DEFAULT 'Pendiente'::character varying NOT NULL,
    CONSTRAINT check_payment_status CHECK (((payment_status)::text = ANY ((ARRAY['Pendiente'::character varying, 'Pagado'::character varying, 'Cancelado'::character varying])::text[]))),
    CONSTRAINT patient_bills_total_amount_due_check CHECK ((total_amount_due >= (0)::numeric))
);
ALTER TABLE public.patient_bills OWNER TO developer;

-- patient_bills_patient_bill_id_seq
CREATE SEQUENCE public.patient_bills_patient_bill_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE public.patient_bills_patient_bill_id_seq OWNER TO developer;
ALTER SEQUENCE public.patient_bills_patient_bill_id_seq OWNED BY public.patient_bills.patient_bill_id;


-- patient_bill_items
CREATE TABLE public.patient_bill_items (
    patient_bill_id integer NOT NULL,
    item_seq_nr integer NOT NULL,
    quantity integer NOT NULL,
    total_cost numeric(10,2) NOT NULL,
    item_code character varying(20),
    CONSTRAINT patient_bill_items_quantity_check CHECK ((quantity > 0)),
    CONSTRAINT patient_bill_items_total_cost_check CHECK ((total_cost >= (0)::numeric))
);
ALTER TABLE public.patient_bill_items OWNER TO developer;


-- record_components
CREATE TABLE public.record_components (
    component_code character varying(10) NOT NULL,
    component_description text NOT NULL
);
ALTER TABLE public.record_components OWNER TO developer;


-- patient_records
CREATE TABLE public.patient_records (
    patient_record_id integer NOT NULL,
    patient_id integer NOT NULL,
    billable_item_code character varying(20),
    component_code character varying(10),
    updated_by_staff_id integer,
    admission_datetime timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    medical_condition text NOT NULL,
    record_notes text,
    is_confidential boolean DEFAULT false NOT NULL,
    CONSTRAINT patient_records_admission_datetime_check CHECK ((admission_datetime <= CURRENT_TIMESTAMP))
);
ALTER TABLE public.patient_records OWNER TO developer;

-- patient_records_patient_record_id_seq
CREATE SEQUENCE public.patient_records_patient_record_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE public.patient_records_patient_record_id_seq OWNER TO developer;
ALTER SEQUENCE public.patient_records_patient_record_id_seq OWNED BY public.patient_records.patient_record_id;


-- patient_rooms
CREATE TABLE public.patient_rooms (
    patient_id integer NOT NULL,
    room_id character varying(10) NOT NULL,
    date_stay_from date NOT NULL,
    date_stay_to date,
    CONSTRAINT patient_rooms_check CHECK (((date_stay_to IS NULL) OR (date_stay_to >= date_stay_from)))
);
ALTER TABLE public.patient_rooms OWNER TO developer;


-- staff_addresses
CREATE TABLE public.staff_addresses (
    staff_id integer NOT NULL,
    address_id integer NOT NULL,
    date_address_from date NOT NULL,
    date_address_to date,
    CONSTRAINT staff_addresses_check CHECK (((date_address_to IS NULL) OR (date_address_to >= date_address_from)))
);
ALTER TABLE public.staff_addresses OWNER TO developer;


-- patient_addresses
CREATE TABLE public.patient_addresses (
    patient_id integer NOT NULL,
    address_id integer NOT NULL,
    date_address_from date NOT NULL,
    date_address_to date,
    CONSTRAINT patient_addresses_check CHECK (((date_address_to IS NULL) OR (date_address_to >= date_address_from)))
);
ALTER TABLE public.patient_addresses OWNER TO developer;


--
-- 3. Definición de Valores por Defecto (DEFAULT) para las Secuencias
--
ALTER TABLE ONLY public.addresses ALTER COLUMN address_id SET DEFAULT nextval('public.addresses_address_id_seq'::regclass);
ALTER TABLE ONLY public.patient_bills ALTER COLUMN patient_bill_id SET DEFAULT nextval('public.patient_bills_patient_bill_id_seq'::regclass);
ALTER TABLE ONLY public.patient_payment_methods ALTER COLUMN patient_method_id SET DEFAULT nextval('public.patient_payment_methods_patient_method_id_seq'::regclass);
ALTER TABLE ONLY public.patient_records ALTER COLUMN patient_record_id SET DEFAULT nextval('public.patient_records_patient_record_id_seq'::regclass);
ALTER TABLE ONLY public.patients ALTER COLUMN patient_id SET DEFAULT nextval('public.patients_patient_id_seq'::regclass);
ALTER TABLE ONLY public.staff ALTER COLUMN staff_id SET DEFAULT nextval('public.staff_staff_id_seq'::regclass);


--
-- 4. Inserción de Datos (Reemplazando COPY FROM stdin)
--

-- addresses
INSERT INTO public.addresses (address_id, line_1_number_building, line_2_number_street, line_3_area_locality, city, zip_postcode, state_province_county, country, is_primary_address, geolocation_code) VALUES
(1, '101 Main St', NULL, NULL, 'Ciudad A', '10001', NULL, 'Pais A', true, '40.7128,-74.0060'),
(2, '22 Oak Ave', NULL, NULL, 'Ciudad B', '20002', NULL, 'Pais A', true, '34.0522,-118.2437'),
(3, 'Piso 5, Torre Medica', NULL, NULL, 'Ciudad C', '30003', NULL, 'Pais B', false, '19.4326,-99.1332'),
(4, '45 Vía Principal', NULL, NULL, 'Ciudad D', '40004', NULL, 'Pais C', true, '51.5074,-0.1278'),
(5, '33 Camino del Sol', NULL, NULL, 'Ciudad E', '50005', NULL, 'Pais C', false, '48.8566,2.3522'),
(6, 'Calle Larga 123', NULL, NULL, 'Ciudad F', '60006', NULL, 'Pais D', true, '35.6895,139.6917'),
(7, 'Av. Central 50', NULL, NULL, 'Ciudad G', '70007', NULL, 'Pais D', true, '55.7558,37.6176'),
(8, 'Ruta 9, Km 10', NULL, NULL, 'Ciudad H', '80008', NULL, 'Pais E', false, '-33.4489,-70.6693'),
(9, 'Edificio Este, Of. 2', NULL, NULL, 'Ciudad I', '90009', NULL, 'Pais E', true, '25.7617,-80.1918'),
(10, '1A Maple Lane', NULL, NULL, 'Ciudad J', '10010', NULL, 'Pais F', true, '43.6532,-79.3832'),
(11, 'Bulevar del Río 7', NULL, NULL, 'Ciudad K', '11011', NULL, 'Pais F', false, '45.4215,-75.6972'),
(12, 'Urbanización El Faro', NULL, NULL, 'Ciudad L', '12012', NULL, 'Pais G', true, '-23.5505,-46.6333'),
(13, 'Apt. 301, Sky Tower', NULL, NULL, 'Ciudad M', '13013', NULL, 'Pais G', true, '-34.6037,-58.3816'),
(14, '77 Western Road', NULL, NULL, 'Ciudad N', '14014', NULL, 'Pais H', false, '53.4808,-2.2426'),
(15, 'Apartado Postal 15', NULL, NULL, 'Ciudad O', '15015', NULL, 'Pais H', true, '52.3676,4.9041'),
(16, 'Casa Blanca, Sector 3', NULL, NULL, 'Ciudad P', '16016', NULL, 'Pais I', true, '41.9028,12.4964'),
(17, 'Zona Industrial 4', NULL, NULL, 'Ciudad Q', '17017', NULL, 'Pais I', false, '40.4168,-3.7038'),
(18, '200 Park Ave', NULL, NULL, 'Ciudad R', '18018', NULL, 'Pais J', true, '39.9526,-75.1652'),
(19, 'Loft 8B', NULL, NULL, 'Ciudad S', '19019', NULL, 'Pais J', true, '34.0522,-118.2437'),
(20, 'C/ Pez, número 1', NULL, NULL, 'Ciudad T', '20020', NULL, 'Pais K', false, '41.3851,2.1734');


-- staff
INSERT INTO public.staff (staff_id, staff_category_code, staff_job_title, gender, staff_first_name, staff_middle_name, staff_last_name, staff_qualifications, staff_birth_date, is_active, emergency_contact_name) VALUES
(1, 'DOC', 'Cirujano Senior', 'M', 'Andrés', NULL, 'Gómez', NULL, '1975-05-15', true, 'Marta Gómez'),
(2, 'NUR', 'Enfermera Jefe', 'F', 'Beatriz', NULL, 'López', NULL, '1988-11-20', true, 'Carlos Ruiz'),
(3, 'ADM', 'Administrativo', 'F', 'Carla', NULL, 'Díaz', NULL, '1995-03-01', true, 'Elena Díaz'),
(4, 'DOC', 'Pediatra', 'M', 'David', NULL, 'Fernández', NULL, '1980-08-10', true, 'Laura Fernández'),
(5, 'NUR', 'Auxiliar de Enfermería', 'F', 'Eva', NULL, 'Morales', NULL, '1998-01-22', true, 'Javier Morales'),
(6, 'TEC', 'Técnico de Laboratorio', 'O', 'Félix', NULL, 'Castro', NULL, '1992-12-05', true, 'Sofía Castro'),
(7, 'ADM', 'Recepcionista', 'F', 'Gloria', NULL, 'Vargas', NULL, '1985-06-30', false, 'Pedro Vargas'),
(8, 'DOC', 'Cardiólogo', 'M', 'Héctor', NULL, 'Reyes', NULL, '1970-04-18', true, 'Ana Reyes'),
(9, 'NUR', 'Enfermera UCI', 'F', 'Irene', NULL, 'Soto', NULL, '1990-09-03', true, 'Daniel Soto'),
(10, 'TEC', 'Radiólogo', 'M', 'Jorge', NULL, 'Molina', NULL, '1978-02-14', true, 'Rosa Molina'),
(11, 'DOC', 'Nefrólogo', 'F', 'Karina', NULL, 'Pérez', NULL, '1983-10-25', true, 'Luis Pérez'),
(12, 'NUR', 'Enfermero Quirúrgico', 'M', 'Lucas', NULL, 'Herrera', NULL, '1993-07-07', true, 'Mónica Herrera'),
(13, 'ADM', 'Contador', 'M', 'Mario', NULL, 'Navarro', NULL, '1965-11-11', true, 'Natalia Navarro'),
(14, 'DOC', 'Ginecólogo', 'F', 'Nora', NULL, 'Quintero', NULL, '1977-03-28', true, 'Omar Quintero'),
(15, 'NUR', 'Enfermera de Planta', 'F', 'Olga', NULL, 'Ramírez', NULL, '1996-05-02', true, 'Pablo Ramírez'),
(16, 'TEC', 'Fisioterapeuta', 'M', 'Pablo', NULL, 'Silva', NULL, '1989-01-19', true, 'Queta Silva'),
(17, 'ADM', 'Jefe de Compras', 'M', 'Raúl', NULL, 'Torres', NULL, '1973-12-01', false, 'Susana Torres'),
(18, 'DOC', 'Oftalmólogo', 'M', 'Sergio', NULL, 'Uribe', NULL, '1981-06-08', true, 'Teresa Uribe'),
(19, 'NUR', 'Asistente Médico', 'F', 'Tania', NULL, 'Vela', NULL, '1991-04-16', true, 'Víctor Vela'),
(20, 'DOC', 'Psiquiatra', 'F', 'Úrsula', NULL, 'Wong', NULL, '1968-09-29', true, 'Xavier Wong');


-- patients
INSERT INTO public.patients (patient_id, outpatient_yn, hospital_number, nhs_number, gender, date_of_birth, patient_first_name, patient_middle_name, patient_last_name, height, weight, next_of_kin, home_phone, work_phone, cell_mobile_phone, insurance_provider, known_allergies) VALUES
(1, false, 'H1000', NULL, 'M', '1960-01-10', 'Eduardo', NULL, 'Martínez', NULL, NULL, NULL, NULL, NULL, NULL, 'Aseguradora Global', 'Penicilina'),
(2, true, 'H1001', NULL, 'F', '1992-07-25', 'Fernanda', NULL, 'Ruiz', NULL, NULL, NULL, NULL, NULL, NULL, 'Salud Primero', 'Ninguna'),
(3, false, 'H1002', NULL, 'O', '2005-04-04', 'Gabriel', NULL, 'Soto', NULL, NULL, NULL, NULL, NULL, NULL, 'Aseguradora Global', 'Látex'),
(4, true, 'H1003', NULL, 'F', '1985-11-12', 'Hilda', NULL, 'Ibarra', NULL, NULL, NULL, NULL, NULL, NULL, 'Seguro Total', 'Ibuprofeno'),
(5, false, 'H1004', NULL, 'M', '1972-02-29', 'Ignacio', NULL, 'Juárez', NULL, NULL, NULL, NULL, NULL, NULL, 'Vida Plena', 'Mariscos'),
(6, false, 'H1005', NULL, 'M', '1955-08-01', 'Javier', NULL, 'King', NULL, NULL, NULL, NULL, NULL, NULL, 'Aseguradora Global', 'Polen'),
(7, true, 'H1006', NULL, 'F', '2010-06-19', 'Laura', NULL, 'Luna', NULL, NULL, NULL, NULL, NULL, NULL, 'Salud Primero', 'Pelo de gato'),
(8, false, 'H1007', NULL, 'M', '1945-03-03', 'Miguel', NULL, 'Mora', NULL, NULL, NULL, NULL, NULL, NULL, 'Seguro Total', 'Aspirina'),
(9, true, 'H1008', NULL, 'F', '1999-12-31', 'Natalia', NULL, 'Nieto', NULL, NULL, NULL, NULL, NULL, NULL, 'Vida Plena', 'Ninguna'),
(10, false, 'H1009', NULL, 'O', '1980-09-09', 'Octavio', NULL, 'Ochoa', NULL, NULL, NULL, NULL, NULL, NULL, 'Aseguradora Global', 'Sulfa'),
(11, true, 'H1010', NULL, 'M', '1967-05-17', 'Pedro', NULL, 'Paz', NULL, NULL, NULL, NULL, NULL, NULL, 'Salud Primero', 'Ninguna'),
(12, false, 'H1011', NULL, 'F', '1994-01-28', 'Queta', NULL, 'Quiroz', NULL, NULL, NULL, NULL, NULL, NULL, 'Seguro Total', 'Cacahuetes'),
(13, false, 'H1012', NULL, 'M', '2001-07-04', 'Ricardo', NULL, 'Ríos', NULL, NULL, NULL, NULL, NULL, NULL, 'Vida Plena', 'Ninguna'),
(14, true, 'H1013', NULL, 'F', '1950-10-05', 'Susana', NULL, 'Sáenz', NULL, NULL, NULL, NULL, NULL, NULL, 'Aseguradora Global', 'Yodo'),
(15, false, 'H1014', NULL, 'M', '1987-04-14', 'Tito', NULL, 'Tapia', NULL, NULL, NULL, NULL, NULL, NULL, 'Salud Primero', 'Ninguna'),
(16, true, 'H1015', NULL, 'F', '1979-08-20', 'Úrsula', NULL, 'Vega', NULL, NULL, NULL, NULL, NULL, NULL, 'Seguro Total', 'Ninguna'),
(17, false, 'H1016', NULL, 'M', '1962-11-23', 'Víctor', NULL, 'Vidal', NULL, NULL, NULL, NULL, NULL, NULL, 'Vida Plena', 'Latex'),
(18, false, 'H1017', NULL, 'F', '1997-03-09', 'Wendy', NULL, 'Wong', NULL, NULL, NULL, NULL, NULL, NULL, 'Aseguradora Global', 'Menta'),
(19, true, 'H1018', NULL, 'M', '1984-01-01', 'Xavier', NULL, 'Ximénez', NULL, NULL, NULL, NULL, NULL, NULL, 'Salud Primero', 'Ninguna'),
(20, false, 'H1019', NULL, 'F', '1970-12-12', 'Yara', NULL, 'Yáñez', NULL, 0.00, NULL, NULL, NULL, NULL, 'Seguro Total', 'Diclofenaco');


-- patient_payment_methods
INSERT INTO public.patient_payment_methods (patient_method_id, patient_id, payment_method_code, payment_method_details) VALUES
(1, 1, 'VISA', 'Últimos 4: 1234, Exp: 12/28'),
(2, 2, 'MC', 'Últimos 4: 5678, Exp: 05/26'),
(3, 3, 'CASH', 'En efectivo'),
(4, 4, 'AMEX', 'Últimos 4: 9012, Exp: 11/27'),
(5, 5, 'TRANSF', 'Cuenta B: 345'),
(6, 6, 'VISA', 'Últimos 4: 4321, Exp: 01/25'),
(7, 7, 'MC', 'Últimos 4: 8765, Exp: 03/29'),
(8, 8, 'CASH', 'En efectivo'),
(9, 9, 'AMEX', 'Últimos 4: 0987, Exp: 07/26'),
(10, 10, 'TRANSF', 'Cuenta B: 678'),
(11, 11, 'VISA', 'Últimos 4: 2345, Exp: 09/25'),
(12, 12, 'MC', 'Últimos 4: 6789, Exp: 10/28'),
(13, 13, 'CASH', 'En efectivo'),
(14, 14, 'AMEX', 'Últimos 4: 1098, Exp: 02/27'),
(15, 15, 'TRANSF', 'Cuenta B: 901'),
(16, 16, 'VISA', 'Últimos 4: 3456, Exp: 06/29'),
(17, 17, 'MC', 'Últimos 4: 7890, Exp: 12/24'),
(18, 18, 'CASH', 'En efectivo'),
(19, 19, 'AMEX', 'Últimos 4: 2109, Exp: 04/28'),
(20, 20, 'TRANSF', 'Cuenta B: 234');


-- patient_bills
INSERT INTO public.patient_bills (patient_bill_id, patient_id, date_bill_paid, total_amount_due, payment_status) VALUES
(2, 2, NULL, 850.50, 'Pagado'),
(3, 3, NULL, 250.00, 'Pagado'),
(4, 4, NULL, 4500.00, 'Pendiente'),
(5, 5, NULL, 1200.75, 'Pendiente'),
(6, 6, NULL, 50.00, 'Pagado'),
(7, 7, NULL, 3000.00, 'Pendiente'),
(8, 8, NULL, 650.00, 'Pagado'),
(9, 9, NULL, 900.00, 'Pendiente'),
(10, 10, NULL, 2200.00, 'Pendiente'),
(11, 11, NULL, 150.00, 'Pagado'),
(12, 12, NULL, 1800.00, 'Pendiente'),
(13, 13, NULL, 75.00, 'Pagado'),
(14, 14, NULL, 550.00, 'Pagado'),
(15, 15, NULL, 1000.00, 'Pendiente'),
(16, 16, NULL, 25.00, 'Pagado'),
(17, 17, NULL, 350.00, 'Pagado'),
(18, 18, NULL, 480.00, 'Pendiente'),
(19, 19, NULL, 1300.00, 'Pendiente'),
(20, 20, NULL, 200.00, 'Pagado'),
(22, 1, '2025-11-01', 456.00, 'Pendiente'),
(1, 1, NULL, 45.00, 'Pendiente'),
(24, 1, '2025-11-18', 44.00, 'Pagado');


-- patient_bill_items
INSERT INTO public.patient_bill_items (patient_bill_id, item_seq_nr, quantity, total_cost, item_code) VALUES
(1, 2, 5, 500.00, NULL),
(2, 1, 1, 500.00, NULL),
(2, 2, 10, 350.50, NULL),
(3, 1, 1, 250.00, NULL),
(4, 1, 1, 4000.00, NULL),
(4, 2, 1, 500.00, NULL),
(5, 1, 1, 1000.00, NULL),
(5, 2, 8, 200.75, NULL),
(6, 1, 1, 50.00, NULL),
(7, 1, 1, 3000.00, NULL),
(8, 1, 1, 650.00, NULL),
(9, 1, 2, 900.00, NULL),
(10, 1, 1, 2000.00, NULL),
(10, 2, 1, 200.00, NULL),
(11, 1, 1, 150.00, NULL),
(12, 1, 1, 1800.00, NULL),
(13, 1, 1, 75.00, NULL),
(14, 1, 1, 550.00, NULL),
(15, 1, 1, 1000.00, NULL),
(1, 1, 5, 5.00, '5');


-- record_components
INSERT INTO public.record_components (component_code, component_description) VALUES
('DIAG', 'Diagnóstico Médico'),
('MED', 'Medicación Administrada'),
('PROC', 'Procedimiento Quirúrgico'),
('LAB', 'Resultados de Laboratorio'),
('RAD', 'Resultados de Radiología/Imágenes'),
('FIS', 'Reporte de Fisioterapia'),
('NUT', 'Evaluación Nutricional'),
('ALTA', 'Resumen de Alta Médica'),
('CON', 'Consulta con Especialista'),
('EVO', 'Nota de Evolución Diaria'),
('ANEST', 'Reporte de Anestesia'),
('CONSENT', 'Consentimiento Informado'),
('SIGNOS', 'Registro de Signos Vitales'),
('TRANS', 'Registro de Transfusión'),
('IMUN', 'Registro de Inmunizaciones'),
('HIST', 'Historial Clínico Completo'),
('URG', 'Nota de Urgencias'),
('PSI', 'Evaluación Psiquiátrica'),
('PAT', 'Reporte de Patología'),
('ADM', 'Administración');


-- patient_records (Solo incluí las columnas con valores no nulos o no por defecto)
INSERT INTO public.patient_records (patient_record_id, patient_id, billable_item_code, component_code, updated_by_staff_id, admission_datetime, medical_condition, record_notes, is_confidential) VALUES
(1, 1, NULL, 'ADM', 1, '2025-11-07 11:52:28.182102', 'Fractura compleja de fémur', 'Paciente en condición estable al ingreso.', false),
(2, 1, NULL, 'DIAG', 1, '2025-11-07 11:52:28.182102', 'Necesidad de cirugía de emergencia', 'Confirmado por radiografía y resonancia.', false),
(3, 2, NULL, 'ADM', 2, '2025-11-07 11:52:28.182102', 'Infección respiratoria grave', 'Ingreso por urgencias, fiebre alta.', false),
(4, 2, NULL, 'MED', 2, '2025-11-07 11:52:28.182102', 'Prescripción de antibióticos', 'Iniciado tratamiento con Amoxicilina.', false),
(5, 3, NULL, 'ADM', 3, '2025-11-07 11:52:28.182102', 'Chequeo anual', 'Paciente sin síntomas, solo rutina.', true),
(6, 3, NULL, 'LAB', 4, '2025-11-07 11:52:28.182102', 'Resultados de sangre normales', 'Revisión por Dr. Pérez.', false),
(7, 4, NULL, 'ADM', 1, '2025-11-07 11:52:28.182102', 'Dolor abdominal agudo', 'Se sospecha apendicitis.', false),
(8, 4, NULL, 'PROC', 1, '2025-11-07 11:52:28.182102', 'Apéndicectomía', 'Procedimiento exitoso.', false),
(9, 5, NULL, 'ADM', 2, '2025-11-07 11:52:28.182102', 'Rehabilitación post-infarto', 'Ingreso a programa de rehabilitación.', false),
(10, 5, NULL, 'FIS', 3, '2025-11-07 11:52:28.182102', 'Sesión de fisioterapia #1', 'Movilidad limitada, mejorando.', false),
(11, 6, NULL, 'ADM', 4, '2025-11-07 11:52:28.182102', 'Control pediátrico', 'Vacunación de rutina.', false),
(12, 6, NULL, 'IMUN', 4, '2025-11-07 11:52:28.182102', 'Vacuna DTPa y Polio', 'Sin reacciones adversas.', false),
(13, 7, NULL, 'ADM', 1, '2025-11-07 11:52:28.182102', 'Caída con golpe en la cabeza', 'Estado de alerta normal.', true),
(14, 7, NULL, 'RAD', 2, '2025-11-07 11:52:28.182102', 'TAC cerebral sin lesiones', 'Observación por 24 horas.', false),
(15, 8, NULL, 'ADM', 3, '2025-11-07 11:52:28.182102', 'Diabetes descompensada', 'Niveles de glucosa muy altos.', false),
(16, 8, NULL, 'NUT', 4, '2025-11-07 11:52:28.182102', 'Dieta baja en carbohidratos', 'Consejería nutricional.', false),
(17, 9, NULL, 'ADM', 1, '2025-11-07 11:52:28.182102', 'Ansiedad severa', 'Evaluación psiquiátrica inicial.', true),
(18, 9, NULL, 'PSI', 1, '2025-11-07 11:52:28.182102', 'Inicio de terapia', 'Se recomienda seguimiento semanal.', true),
(19, 10, NULL, 'ADM', 2, '2025-11-07 11:52:28.182102', 'Cálculos renales', 'Dolor lumbar intenso.', false),
(20, 10, NULL, 'CON', 3, '2025-11-07 11:52:28.182102', 'Consulta Urología', 'Se programa litotricia.', false);


-- patient_rooms
INSERT INTO public.patient_rooms (patient_id, room_id, date_stay_from, date_stay_to) VALUES
(1, 'R101', '2025-10-25', '2025-11-01'),
(2, 'R205', '2025-11-05', NULL),
(4, 'R102', '2025-11-06', NULL),
(5, 'R303', '2025-10-01', '2025-10-20'),
(7, 'R105', '2025-11-07', NULL),
(8, 'R210', '2025-10-15', '2025-10-22'),
(10, 'R301', '2025-11-04', NULL),
(13, 'R401', '2025-09-01', '2025-09-10'),
(16, 'R405', '2025-08-10', '2025-08-15'),
(1, 'R103', '2025-11-01', NULL),
(3, 'R501', '2025-11-01', '2025-11-02'),
(6, 'R502', '2025-11-03', '2025-11-04'),
(9, 'R503', '2025-11-05', '2025-11-06'),
(11, 'R601', '2025-10-28', '2025-11-01'),
(12, 'R602', '2025-11-02', '2025-11-03'),
(14, 'R603', '2025-11-04', NULL),
(15, 'R701', '2025-10-05', '2025-10-10'),
(17, 'R702', '2025-10-11', '2025-10-12'),
(18, 'R703', '2025-11-06', NULL);


-- staff_addresses
INSERT INTO public.staff_addresses (staff_id, address_id, date_address_from, date_address_to) VALUES
(1, 1, '2020-01-01', NULL),
(2, 2, '2018-06-15', NULL),
(3, 3, '2022-03-10', NULL),
(4, 4, '2021-05-20', NULL),
(5, 5, '2019-11-01', NULL),
(6, 6, '2023-08-25', NULL),
(7, 7, '2017-02-14', NULL),
(8, 8, '2020-10-10', NULL),
(9, 9, '2018-04-05', NULL),
(10, 10, '2022-01-01', NULL),
(11, 11, '2021-07-12', NULL),
(12, 12, '2019-09-30', NULL),
(13, 13, '2016-12-24', NULL),
(14, 14, '2023-02-18', NULL),
(15, 15, '2020-05-05', NULL),
(16, 16, '2024-01-29', NULL),
(17, 17, '2019-03-08', NULL),
(18, 18, '2022-11-03', NULL),
(19, 19, '2018-07-21', NULL),
(20, 20, '2021-04-04', NULL);


-- patient_addresses
INSERT INTO public.patient_addresses (patient_id, address_id, date_address_from, date_address_to) VALUES
(3, 5, '2023-01-01', NULL),
(4, 1, '2022-11-11', NULL),
(6, 19, '2019-07-07', NULL),
(7, 4, '2024-02-02', NULL),
(8, 12, '2016-09-09', NULL),
(9, 7, '2021-05-01', NULL),
(10, 14, '2017-10-10', NULL),
(11, 3, '2023-04-25', NULL),
(12, 18, '2014-12-12', NULL),
(13, 6, '2020-08-08', NULL),
(14, 11, '2022-09-01', NULL),
(15, 8, '2021-12-05', NULL),
(16, 16, '2018-03-17', NULL),
(17, 13, '2024-01-01', NULL),
(18, 9, '2015-05-05', NULL),
(19, 17, '2020-11-20', NULL),
(20, 20, '2019-04-04', NULL),
(2, 2, '2025-11-01', '2025-11-07'),
(2, 3, '2025-11-19', '2025-11-20'),
(3, 13, '2025-11-07', '2025-11-07'),
(5, 15, '2020-03-20', '2020-11-21');


--
-- 5. Actualización de Secuencias
--
SELECT pg_catalog.setval('public.addresses_address_id_seq', 25, true);
SELECT pg_catalog.setval('public.patient_bills_patient_bill_id_seq', 26, true);
SELECT pg_catalog.setval('public.patient_payment_methods_patient_method_id_seq', 20, true);
SELECT pg_catalog.setval('public.patient_records_patient_record_id_seq', 20, true);
SELECT pg_catalog.setval('public.patients_patient_id_seq', 31, true);
SELECT pg_catalog.setval('public.staff_staff_id_seq', 23, true);


--
-- 6. Adición de Restricciones (Primary Keys)
--
ALTER TABLE ONLY public.addresses ADD CONSTRAINT addresses_pkey PRIMARY KEY (address_id);
ALTER TABLE ONLY public.patient_addresses ADD CONSTRAINT patient_addresses_pkey PRIMARY KEY (patient_id, address_id, date_address_from);
ALTER TABLE ONLY public.patient_bill_items ADD CONSTRAINT patient_bill_items_pkey PRIMARY KEY (patient_bill_id, item_seq_nr);
ALTER TABLE ONLY public.patient_bills ADD CONSTRAINT patient_bills_pkey PRIMARY KEY (patient_bill_id);
ALTER TABLE ONLY public.patient_payment_methods ADD CONSTRAINT patient_payment_methods_pkey PRIMARY KEY (patient_method_id);
ALTER TABLE ONLY public.patient_records ADD CONSTRAINT patient_records_pkey PRIMARY KEY (patient_record_id);
ALTER TABLE ONLY public.patient_rooms ADD CONSTRAINT patient_rooms_pkey PRIMARY KEY (patient_id, room_id, date_stay_from);
--ALTER TABLE ONLY public.patients ADD CONSTRAINT patients_hospital_number_key UNIQUE (hospital_number);
ALTER TABLE ONLY public.patients ADD CONSTRAINT patients_nhs_number_key UNIQUE (nhs_number);
ALTER TABLE ONLY public.patients ADD CONSTRAINT patients_pkey PRIMARY KEY (patient_id);
ALTER TABLE ONLY public.record_components ADD CONSTRAINT record_components_pkey PRIMARY KEY (component_code);
ALTER TABLE ONLY public.staff_addresses ADD CONSTRAINT staff_addresses_pkey PRIMARY KEY (staff_id, address_id, date_address_from);
ALTER TABLE ONLY public.staff ADD CONSTRAINT staff_pkey PRIMARY KEY (staff_id);


--
-- 7. Adición de Restricciones (Foreign Keys)
--
ALTER TABLE ONLY public.patient_addresses ADD CONSTRAINT fk_patient_addresses_address_id FOREIGN KEY (address_id) REFERENCES public.addresses(address_id);
ALTER TABLE ONLY public.patient_addresses ADD CONSTRAINT fk_patient_addresses_patient_id FOREIGN KEY (patient_id) REFERENCES public.patients(patient_id);
ALTER TABLE ONLY public.patient_bill_items ADD CONSTRAINT fk_patient_bill_items_patient_bill_id FOREIGN KEY (patient_bill_id) REFERENCES public.patient_bills(patient_bill_id);
ALTER TABLE ONLY public.patient_bills ADD CONSTRAINT fk_patient_bills_patient_id FOREIGN KEY (patient_id) REFERENCES public.patients(patient_id);
ALTER TABLE ONLY public.patient_payment_methods ADD CONSTRAINT fk_patient_payment_methods_patient_id FOREIGN KEY (patient_id) REFERENCES public.patients(patient_id);
ALTER TABLE ONLY public.patient_records ADD CONSTRAINT fk_patient_records_component_code FOREIGN KEY (component_code) REFERENCES public.record_components(component_code);
ALTER TABLE ONLY public.patient_records ADD CONSTRAINT fk_patient_records_patient_id FOREIGN KEY (patient_id) REFERENCES public.patients(patient_id);
ALTER TABLE ONLY public.patient_records ADD CONSTRAINT fk_patient_records_staff_id FOREIGN KEY (updated_by_staff_id) REFERENCES public.staff(staff_id);
ALTER TABLE ONLY public.patient_rooms ADD CONSTRAINT fk_patient_rooms_patient_id FOREIGN KEY (patient_id) REFERENCES public.patients(patient_id);
ALTER TABLE ONLY public.staff_addresses ADD CONSTRAINT fk_staff_addresses_address_id FOREIGN KEY (address_id) REFERENCES public.addresses(address_id);
ALTER TABLE ONLY public.staff_addresses ADD CONSTRAINT fk_staff_addresses_staff_id FOREIGN KEY (staff_id) REFERENCES public.staff(staff_id);


ALTER TABLE public.patients
DROP CONSTRAINT patients_hospital_number_key;
--
-- Fin del Dump
--