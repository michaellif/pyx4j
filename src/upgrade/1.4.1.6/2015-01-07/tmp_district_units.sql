--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = _dba_, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: tmp_apt_unit; Type: TABLE; Schema: _dba_; Owner: -; Tablespace: 
--

CREATE TABLE tmp_apt_unit (
    id bigint,
    building bigint,
    floorplan bigint,
    info_economic_status character varying(50),
    info_economic_status_description character varying(250),
    info_floor integer,
    info_unit_number character varying(20),
    info_legal_address_override boolean,
    info_legal_address_suite_number character varying(500),
    info_legal_address_street_number character varying(500),
    info_legal_address_street_name character varying(500),
    info_legal_address_city character varying(500),
    info_legal_address_postal_code character varying(500),
    info_area double precision,
    info_area_units character varying(50),
    info__bedrooms integer,
    info__bathrooms integer,
    financial__unit_rent numeric(18,2),
    financial__market_rent numeric(18,2),
    updated timestamp without time zone,
    info_number_s character varying(32),
    info_legal_address_country character varying(50),
    info_legal_address_province character varying(500),
    info_legal_address_street_direction character varying(500),
    info_legal_address_street_type character varying(500)
);


--
-- Name: tmp_apt_unit_effective_availability; Type: TABLE; Schema: _dba_; Owner: -; Tablespace: 
--

CREATE TABLE tmp_apt_unit_effective_availability (
    id bigint,
    unit bigint,
    available_for_rent date,
    updated timestamp without time zone
);


--
-- Name: tmp_apt_unit_occupancy_segment; Type: TABLE; Schema: _dba_; Owner: -; Tablespace: 
--

CREATE TABLE tmp_apt_unit_occupancy_segment (
    id bigint,
    unit bigint,
    date_from date,
    date_to date,
    status character varying(50),
    off_market character varying(50),
    lease bigint,
    description character varying(500)
);


--
-- Name: tmp_unit_availability_status; Type: TABLE; Schema: _dba_; Owner: -; Tablespace: 
--

CREATE TABLE tmp_unit_availability_status (
    id bigint,
    unit bigint,
    building bigint,
    floorplan bigint,
    complex bigint,
    status_from date,
    status_until date,
    vacancy_status character varying(50),
    rented_status character varying(50),
    scoping character varying(50),
    rent_readiness_status character varying(50),
    unit_rent numeric(18,2),
    market_rent numeric(18,2),
    rent_delta_absolute numeric(18,2),
    rent_delta_relative numeric(18,2),
    rent_end_day date,
    vacant_since date,
    rented_from_day date,
    move_in_day date
);


--
-- Data for Name: tmp_apt_unit; Type: TABLE DATA; Schema: _dba_; Owner: -
--

COPY tmp_apt_unit (id, building, floorplan, info_economic_status, info_economic_status_description, info_floor, info_unit_number, info_legal_address_override, info_legal_address_suite_number, info_legal_address_street_number, info_legal_address_street_name, info_legal_address_city, info_legal_address_postal_code, info_area, info_area_units, info__bedrooms, info__bathrooms, financial__unit_rent, financial__market_rent, updated, info_number_s, info_legal_address_country, info_legal_address_province, info_legal_address_street_direction, info_legal_address_street_type) FROM stdin;
88273	1747	6324	residential	\N	1	01	\N	\N	\N	\N	\N	\N	\N	\N	2	1	\N	1215.00	2014-12-03 13:43:22	0000001	\N	\N	\N	\N
91964	1747	6325	residential	\N	\N	16	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-03 16:11:33	0000016	\N	\N	\N	\N
91968	1747	6326	residential	\N	\N	24	\N	\N	\N	\N	\N	\N	1662	sqFeet	4	2	\N	\N	2014-12-03 16:23:42	0000024	\N	\N	\N	\N
91965	1747	6325	residential	\N	\N	18	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-03 16:11:53	0000018	\N	\N	\N	\N
91966	1747	6325	residential	\N	\N	20	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-03 16:12:05	0000020	\N	\N	\N	\N
93274	1747	6326	residential	\N	\N	31	\N	\N	\N	\N	\N	\N	1662	sqFeet	4	2	\N	\N	2014-12-03 16:08:03	0000031	\N	\N	\N	\N
93290	1747	6326	residential	\N	\N	53	\N	\N	\N	\N	\N	\N	1662	sqFeet	4	2	\N	\N	2014-12-03 16:08:33	0000053	\N	\N	\N	\N
93291	1747	6326	residential	\N	\N	55	\N	\N	\N	\N	\N	\N	1662	sqFeet	4	2	\N	\N	2014-12-03 16:08:43	0000055	\N	\N	\N	\N
93292	1747	6325	residential	\N	\N	57	\N	\N	\N	\N	\N	\N	1662	sqFeet	3	2	\N	\N	2014-12-03 16:08:54	0000057	\N	\N	\N	\N
93279	1747	6325	residential	\N	\N	36	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-03 16:25:03	0000036	\N	\N	\N	\N
91958	1747	6325	residential	\N	\N	4	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-03 16:10:06	0000004	\N	\N	\N	\N
91957	1747	6326	residential	\N	\N	2	\N	\N	\N	\N	\N	\N	1662	sqFeet	4	2	\N	\N	2014-12-03 16:10:19	0000002	\N	\N	\N	\N
91959	1747	6325	residential	\N	\N	6	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-03 16:10:30	0000006	\N	\N	\N	\N
91962	1747	6326	residential	\N	\N	12	\N	\N	\N	\N	\N	\N	1662	sqFeet	4	2	\N	\N	2014-12-03 16:11:02	0000012	\N	\N	\N	\N
91963	1747	6326	residential	\N	\N	14	\N	\N	\N	\N	\N	\N	1662	sqFeet	4	2	\N	\N	2014-12-03 16:11:22	0000014	\N	\N	\N	\N
91967	1747	6325	residential	\N	\N	22	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-03 16:12:30	0000022	\N	\N	\N	\N
93284	1747	6325	residential	\N	\N	41	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-03 16:22:57	0000041	\N	\N	\N	\N
93280	1747	6325	residential	\N	\N	37	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-03 16:25:20	0000037	\N	\N	\N	\N
93272	1747	6325	residential	\N	\N	28	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-03 16:23:19	0000028	\N	\N	\N	\N
93273	1747	6325	residential	\N	\N	30	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-03 16:23:31	0000030	\N	\N	\N	\N
93271	1747	6326	residential	\N	\N	26	\N	\N	\N	\N	\N	\N	1662	sqFeet	4	2	\N	\N	2014-12-03 16:23:54	0000026	\N	\N	\N	\N
93275	1747	6325	residential	\N	\N	32	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-03 16:24:20	0000032	\N	\N	\N	\N
93276	1747	6325	residential	\N	\N	33	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-03 16:24:30	0000033	\N	\N	\N	\N
93277	1747	6325	residential	\N	\N	34	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-03 16:24:41	0000034	\N	\N	\N	\N
93278	1747	6325	residential	\N	\N	35	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-03 16:24:51	0000035	\N	\N	\N	\N
93281	1747	6325	residential	\N	\N	38	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-03 16:25:41	0000038	\N	\N	\N	\N
93282	1747	6325	residential	\N	\N	39	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-03 16:25:53	0000039	\N	\N	\N	\N
93283	1747	6326	residential	\N	\N	40	\N	\N	\N	\N	\N	\N	1662	sqFeet	4	2	\N	\N	2014-12-03 16:26:03	0000040	\N	\N	\N	\N
93286	1747	6326	residential	\N	\N	45	\N	\N	\N	\N	\N	\N	1662	sqFeet	4	2	\N	\N	2014-12-03 16:26:31	0000045	\N	\N	\N	\N
93287	1747	6326	residential	\N	\N	47	\N	\N	\N	\N	\N	\N	1662	sqFeet	4	2	\N	\N	2014-12-03 16:26:43	0000047	\N	\N	\N	\N
88274	1747	6325	residential	\N	1	02	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	1447.00	2014-12-03 16:27:27	0000002	\N	\N	\N	\N
88275	1747	6326	residential	\N	1	03	\N	\N	\N	\N	\N	\N	1662	sqFeet	4	2	\N	1547.00	2014-12-03 16:27:44	0000003	\N	\N	\N	\N
93293	1747	6325	residential	\N	\N	59	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-03 16:09:45	0000059	\N	\N	\N	\N
91960	1747	6325	residential	\N	\N	8	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-03 16:10:42	0000008	\N	\N	\N	\N
91961	1747	6325	residential	\N	\N	10	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-03 16:10:52	0000010	\N	\N	\N	\N
93285	1747	6325	residential	\N	\N	43	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-03 16:26:19	0000043	\N	\N	\N	\N
93288	1747	6325	residential	\N	\N	49	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-03 16:26:55	0000049	\N	\N	\N	\N
93289	1747	6325	residential	\N	\N	51	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-03 16:27:10	0000051	\N	\N	\N	\N
93294	1747	6326	residential	\N	\N	61	\N	\N	\N	\N	\N	\N	1662	sqFeet	4	2	\N	\N	2014-12-03 16:28:10	0000061	\N	\N	\N	\N
93295	1747	6326	residential	\N	\N	102	\N	\N	\N	\N	\N	\N	1662	sqFeet	4	2	\N	\N	2014-12-03 16:28:31	0000102	\N	\N	\N	\N
93296	1747	6325	residential	\N	\N	104	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-03 16:28:57	0000104	\N	\N	\N	\N
93297	1747	6325	residential	\N	\N	106	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-03 16:29:14	0000106	\N	\N	\N	\N
93298	1747	6325	residential	\N	\N	108	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-03 16:29:31	0000108	\N	\N	\N	\N
93299	1747	6325	residential	\N	\N	110	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-03 16:29:49	0000110	\N	\N	\N	\N
93300	1747	6326	residential	\N	\N	112	\N	\N	\N	\N	\N	\N	1662	sqFeet	4	2	\N	\N	2014-12-03 16:30:15	0000112	\N	\N	\N	\N
93301	1747	6326	residential	\N	\N	114	\N	\N	\N	\N	\N	\N	1662	sqFeet	4	2	\N	\N	2014-12-03 16:30:45	0000114	\N	\N	\N	\N
93302	1747	6325	\N	\N	\N	116	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-03 16:31:02	0000116	\N	\N	\N	\N
93303	1747	6325	residential	\N	\N	118	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-03 16:31:22	0000118	\N	\N	\N	\N
93304	1747	6325	residential	\N	\N	120	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-03 16:31:54	0000120	\N	\N	\N	\N
93305	1747	6325	residential	\N	\N	122	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-03 16:32:14	0000122	\N	\N	\N	\N
93306	1747	6326	residential	\N	\N	124	\N	\N	\N	\N	\N	\N	1662	sqFeet	4	2	\N	\N	2014-12-03 16:34:17	0000124	\N	\N	\N	\N
93307	1747	6326	residential	\N	\N	126	\N	\N	\N	\N	\N	\N	1662	sqFeet	4	2	\N	\N	2014-12-03 16:34:44	0000126	\N	\N	\N	\N
93308	1747	6325	residential	\N	\N	128	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-03 16:35:04	0000128	\N	\N	\N	\N
93309	1747	6325	residential	\N	\N	130	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-03 16:35:32	0000130	\N	\N	\N	\N
93310	1747	6326	residential	\N	\N	131	\N	\N	\N	\N	\N	\N	1662	sqFeet	4	2	\N	\N	2014-12-03 16:35:50	0000131	\N	\N	\N	\N
93311	1747	6325	residential	\N	\N	132	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-03 16:36:06	0000132	\N	\N	\N	\N
93312	1747	6325	residential	\N	\N	133	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-03 16:36:25	0000133	\N	\N	\N	\N
93313	1747	6325	residential	\N	\N	134	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-03 16:36:47	0000134	\N	\N	\N	\N
93314	1747	6325	residential	\N	\N	135	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-03 16:37:16	0000135	\N	\N	\N	\N
93315	1747	6325	residential	\N	\N	136	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-03 16:37:37	0000136	\N	\N	\N	\N
93316	1747	6325	residential	\N	\N	137	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-03 16:38:24	0000137	\N	\N	\N	\N
93318	1747	6325	residential	\N	\N	138	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-04 12:56:02	0000138	\N	\N	\N	\N
93319	1747	6325	residential	\N	\N	139	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-04 12:56:20	0000139	\N	\N	\N	\N
93320	1747	6326	residential	\N	\N	140	\N	\N	\N	\N	\N	\N	1662	sqFeet	4	2	\N	\N	2014-12-04 12:56:37	0000140	\N	\N	\N	\N
93321	1747	6325	residential	\N	\N	141	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-04 12:56:55	0000141	\N	\N	\N	\N
93322	1747	6325	residential	\N	\N	143	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-04 12:57:09	0000143	\N	\N	\N	\N
93323	1747	6326	residential	\N	\N	145	\N	\N	\N	\N	\N	\N	1662	sqFeet	4	2	\N	\N	2014-12-04 12:57:37	0000145	\N	\N	\N	\N
93324	1747	6325	\N	\N	\N	147	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-04 12:57:52	0000147	\N	\N	\N	\N
93325	1747	6325	residential	\N	\N	149	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-04 12:58:29	0000149	\N	\N	\N	\N
93326	1747	6325	residential	\N	\N	151	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-04 12:58:47	0000151	\N	\N	\N	\N
93327	1747	6326	residential	\N	\N	153	\N	\N	\N	\N	\N	\N	1662	sqFeet	4	2	\N	\N	2014-12-04 12:59:16	0000153	\N	\N	\N	\N
93328	1747	6326	residential	\N	\N	155	\N	\N	\N	\N	\N	\N	1662	sqFeet	4	2	\N	\N	2014-12-04 12:59:38	0000155	\N	\N	\N	\N
93329	1747	6325	residential	\N	\N	157	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-04 12:59:56	0000157	\N	\N	\N	\N
93330	1747	6325	residential	\N	\N	159	\N	\N	\N	\N	\N	\N	1400	sqFeet	3	2	\N	\N	2014-12-04 13:00:20	0000159	\N	\N	\N	\N
93331	1747	6326	residential	\N	\N	161	\N	\N	\N	\N	\N	\N	1662	sqFeet	4	2	\N	\N	2014-12-04 13:12:21	0000161	\N	\N	\N	\N
\.


--
-- Data for Name: tmp_apt_unit_effective_availability; Type: TABLE DATA; Schema: _dba_; Owner: -
--

COPY tmp_apt_unit_effective_availability (id, unit, available_for_rent, updated) FROM stdin;
63484	88273	\N	2014-10-01 09:07:01
63485	88274	\N	2014-10-01 09:07:25
63486	88275	\N	2014-10-01 09:07:48
67150	91957	\N	2014-11-28 15:34:04
67151	91958	\N	2014-11-28 15:49:24
67152	91959	\N	2014-11-28 15:49:44
67153	91960	\N	2014-11-28 15:50:00
67154	91961	\N	2014-11-28 15:50:15
67155	91962	\N	2014-11-28 15:50:33
67156	91963	\N	2014-11-28 15:50:48
67157	91964	\N	2014-11-28 16:06:52
67158	91965	\N	2014-11-28 16:07:20
67159	91966	\N	2014-11-28 16:08:16
67160	91967	\N	2014-11-28 16:09:01
67161	91968	\N	2014-11-28 16:09:38
68464	93271	\N	2014-12-03 13:38:04
68465	93272	\N	2014-12-03 13:44:36
68466	93273	\N	2014-12-03 13:44:55
68467	93274	\N	2014-12-03 13:45:15
68468	93275	\N	2014-12-03 13:45:34
68469	93276	\N	2014-12-03 13:45:56
68470	93277	\N	2014-12-03 13:46:16
68471	93278	\N	2014-12-03 13:46:34
68472	93279	\N	2014-12-03 13:46:53
68473	93280	\N	2014-12-03 13:47:11
68474	93281	\N	2014-12-03 13:47:30
68475	93282	\N	2014-12-03 13:47:49
68476	93283	\N	2014-12-03 13:48:06
68477	93284	\N	2014-12-03 13:48:35
68478	93285	\N	2014-12-03 13:49:12
68479	93286	\N	2014-12-03 13:49:50
68480	93287	\N	2014-12-03 13:50:08
68481	93288	\N	2014-12-03 14:15:20
68482	93289	\N	2014-12-03 14:15:41
68483	93290	\N	2014-12-03 15:55:40
68484	93291	\N	2014-12-03 16:00:30
68485	93292	\N	2014-12-03 16:06:09
68486	93293	\N	2014-12-03 16:09:45
68487	93294	\N	2014-12-03 16:28:10
68488	93295	\N	2014-12-03 16:28:27
68489	93296	\N	2014-12-03 16:28:57
68490	93297	\N	2014-12-03 16:29:14
68491	93298	\N	2014-12-03 16:29:31
68492	93299	\N	2014-12-03 16:29:49
68493	93300	\N	2014-12-03 16:30:15
68494	93301	\N	2014-12-03 16:30:45
68495	93302	\N	2014-12-03 16:31:02
68496	93303	\N	2014-12-03 16:31:22
68497	93304	\N	2014-12-03 16:31:54
68498	93305	\N	2014-12-03 16:32:14
68499	93306	\N	2014-12-03 16:34:17
68500	93307	\N	2014-12-03 16:34:44
68501	93308	\N	2014-12-03 16:35:04
68502	93309	\N	2014-12-03 16:35:32
68503	93310	\N	2014-12-03 16:35:50
68504	93311	\N	2014-12-03 16:36:06
68505	93312	\N	2014-12-03 16:36:25
68508	93315	\N	2014-12-03 16:37:37
68506	93313	\N	2014-12-03 16:36:47
68509	93316	\N	2014-12-03 16:38:24
68507	93314	\N	2014-12-03 16:37:16
68511	93318	\N	2014-12-04 12:56:02
68512	93319	\N	2014-12-04 12:56:20
68513	93320	\N	2014-12-04 12:56:37
68514	93321	\N	2014-12-04 12:56:55
68515	93322	\N	2014-12-04 12:57:09
68516	93323	\N	2014-12-04 12:57:37
68517	93324	\N	2014-12-04 12:57:52
68518	93325	\N	2014-12-04 12:58:29
68519	93326	\N	2014-12-04 12:58:47
68520	93327	\N	2014-12-04 12:59:16
68521	93328	\N	2014-12-04 12:59:38
68522	93329	\N	2014-12-04 12:59:56
68523	93330	\N	2014-12-04 13:00:20
68524	93331	\N	2014-12-04 13:12:21
\.


--
-- Data for Name: tmp_apt_unit_occupancy_segment; Type: TABLE DATA; Schema: _dba_; Owner: -
--

COPY tmp_apt_unit_occupancy_segment (id, unit, date_from, date_to, status, off_market, lease, description) FROM stdin;
1409	88273	2014-10-01	3000-01-01	pending	\N	\N	\N
1410	88274	2014-10-01	3000-01-01	pending	\N	\N	\N
1411	88275	2014-10-01	3000-01-01	pending	\N	\N	\N
1867	91957	2014-11-28	3000-01-01	pending	\N	\N	\N
1868	91958	2014-11-28	3000-01-01	pending	\N	\N	\N
1869	91959	2014-11-28	3000-01-01	pending	\N	\N	\N
1870	91960	2014-11-28	3000-01-01	pending	\N	\N	\N
1871	91961	2014-11-28	3000-01-01	pending	\N	\N	\N
1872	91962	2014-11-28	3000-01-01	pending	\N	\N	\N
1873	91963	2014-11-28	3000-01-01	pending	\N	\N	\N
1874	91964	2014-11-28	3000-01-01	pending	\N	\N	\N
1875	91965	2014-11-28	3000-01-01	pending	\N	\N	\N
1876	91966	2014-11-28	3000-01-01	pending	\N	\N	\N
1877	91967	2014-11-28	3000-01-01	pending	\N	\N	\N
1878	91968	2014-11-28	3000-01-01	pending	\N	\N	\N
1898	93271	2014-12-03	3000-01-01	pending	\N	\N	\N
1899	93272	2014-12-03	3000-01-01	pending	\N	\N	\N
1900	93273	2014-12-03	3000-01-01	pending	\N	\N	\N
1901	93274	2014-12-03	3000-01-01	pending	\N	\N	\N
1902	93275	2014-12-03	3000-01-01	pending	\N	\N	\N
1903	93276	2014-12-03	3000-01-01	pending	\N	\N	\N
1904	93277	2014-12-03	3000-01-01	pending	\N	\N	\N
1905	93278	2014-12-03	3000-01-01	pending	\N	\N	\N
1906	93279	2014-12-03	3000-01-01	pending	\N	\N	\N
1907	93280	2014-12-03	3000-01-01	pending	\N	\N	\N
1908	93281	2014-12-03	3000-01-01	pending	\N	\N	\N
1909	93282	2014-12-03	3000-01-01	pending	\N	\N	\N
1910	93283	2014-12-03	3000-01-01	pending	\N	\N	\N
1911	93284	2014-12-03	3000-01-01	pending	\N	\N	\N
1912	93285	2014-12-03	3000-01-01	pending	\N	\N	\N
1913	93286	2014-12-03	3000-01-01	pending	\N	\N	\N
1914	93287	2014-12-03	3000-01-01	pending	\N	\N	\N
1915	93288	2014-12-03	3000-01-01	pending	\N	\N	\N
1916	93289	2014-12-03	3000-01-01	pending	\N	\N	\N
1917	93290	2014-12-03	3000-01-01	pending	\N	\N	\N
1918	93291	2014-12-03	3000-01-01	pending	\N	\N	\N
1919	93292	2014-12-03	3000-01-01	pending	\N	\N	\N
1920	93293	2014-12-03	3000-01-01	pending	\N	\N	\N
1921	93294	2014-12-03	3000-01-01	pending	\N	\N	\N
1922	93295	2014-12-03	3000-01-01	pending	\N	\N	\N
1923	93296	2014-12-03	3000-01-01	pending	\N	\N	\N
1924	93297	2014-12-03	3000-01-01	pending	\N	\N	\N
1925	93298	2014-12-03	3000-01-01	pending	\N	\N	\N
1926	93299	2014-12-03	3000-01-01	pending	\N	\N	\N
1927	93300	2014-12-03	3000-01-01	pending	\N	\N	\N
1928	93301	2014-12-03	3000-01-01	pending	\N	\N	\N
1929	93302	2014-12-03	3000-01-01	pending	\N	\N	\N
1930	93303	2014-12-03	3000-01-01	pending	\N	\N	\N
1931	93304	2014-12-03	3000-01-01	pending	\N	\N	\N
1932	93305	2014-12-03	3000-01-01	pending	\N	\N	\N
1933	93306	2014-12-03	3000-01-01	pending	\N	\N	\N
1934	93307	2014-12-03	3000-01-01	pending	\N	\N	\N
1935	93308	2014-12-03	3000-01-01	pending	\N	\N	\N
1936	93309	2014-12-03	3000-01-01	pending	\N	\N	\N
1937	93310	2014-12-03	3000-01-01	pending	\N	\N	\N
1938	93311	2014-12-03	3000-01-01	pending	\N	\N	\N
1939	93312	2014-12-03	3000-01-01	pending	\N	\N	\N
1940	93313	2014-12-03	3000-01-01	pending	\N	\N	\N
1941	93314	2014-12-03	3000-01-01	pending	\N	\N	\N
1942	93315	2014-12-03	3000-01-01	pending	\N	\N	\N
1943	93316	2014-12-03	3000-01-01	pending	\N	\N	\N
1949	93318	2014-12-04	3000-01-01	pending	\N	\N	\N
1950	93319	2014-12-04	3000-01-01	pending	\N	\N	\N
1951	93320	2014-12-04	3000-01-01	pending	\N	\N	\N
1952	93321	2014-12-04	3000-01-01	pending	\N	\N	\N
1953	93322	2014-12-04	3000-01-01	pending	\N	\N	\N
1954	93323	2014-12-04	3000-01-01	pending	\N	\N	\N
1955	93324	2014-12-04	3000-01-01	pending	\N	\N	\N
1956	93325	2014-12-04	3000-01-01	pending	\N	\N	\N
1957	93326	2014-12-04	3000-01-01	pending	\N	\N	\N
1958	93327	2014-12-04	3000-01-01	pending	\N	\N	\N
1959	93328	2014-12-04	3000-01-01	pending	\N	\N	\N
1960	93329	2014-12-04	3000-01-01	pending	\N	\N	\N
1961	93330	2014-12-04	3000-01-01	pending	\N	\N	\N
1962	93331	2014-12-04	3000-01-01	pending	\N	\N	\N
\.


--
-- Data for Name: tmp_unit_availability_status; Type: TABLE DATA; Schema: _dba_; Owner: -
--

COPY tmp_unit_availability_status (id, unit, building, floorplan, complex, status_from, status_until, vacancy_status, rented_status, scoping, rent_readiness_status, unit_rent, market_rent, rent_delta_absolute, rent_delta_relative, rent_end_day, vacant_since, rented_from_day, move_in_day) FROM stdin;
\.


--
-- PostgreSQL database dump complete
--

