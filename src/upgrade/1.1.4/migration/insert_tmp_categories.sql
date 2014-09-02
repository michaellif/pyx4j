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
-- Name: tmp_categories; Type: TABLE; Schema: _dba_; Owner: -; Tablespace: 
--

CREATE TABLE tmp_categories (
    category character varying(500),
    category_type character varying(50),
    ticket_type character varying(50),
    deleted boolean
);


--
-- Data for Name: tmp_categories; Type: TABLE DATA; Schema: _dba_; Owner: -
--

COPY tmp_categories (category, category_type, ticket_type, deleted) FROM stdin;
Tenant	Ticket	Tenant	f
Landlord	Ticket	Landlord	f
Vendor	Ticket	Vendor	f
General Message	Message	NotTicket	f
\.


--
-- PostgreSQL database dump complete
--

