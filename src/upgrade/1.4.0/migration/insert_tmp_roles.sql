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
-- Name: tmp_roles; Type: TABLE; Schema: _dba_; Owner: -; Tablespace: 
--

CREATE TABLE tmp_roles (
    name character varying(55),
    value character varying(50),
    require_security_question_for_password_reset boolean
);


--
-- Data for Name: tmp_roles; Type: TABLE DATA; Schema: _dba_; Owner: -
--

COPY tmp_roles (name, value, require_security_question_for_password_reset) FROM stdin;
All	BuildingBasic	t
All	BuildingFinancial	t
All	BuildingAccounting	t
All	BuildingProperty	t
All	BuildingMarketing	t
All	BuildingMechanicals	t
All	BuildingAdministrator	t
All	BuildingLeasing	t
All	YardiLoads	t
All	MaintenanceBasic	t
All	MaintenanceAdvanced	t
All	MaintenanceFull	t
All	LeaseBasic	t
All	LeaseAdvanced	t
All	LeaseFull	t
All	ApplicationBasic	t
All	ApplicationVerifyDoc	t
All	ApplicationFull	t
All	ApplicationDecisionReserveUnit	t
All	ApplicationDecisionStartOnline	t
All	ApplicationDecisionSubmit	t
All	ApplicationDecisionVerify	t
All	ApplicationDecisionApprove	t
All	ApplicationDecisionFull	t
All	CreditCheckBasic	t
All	CreditCheckFull	t
All	TenantBasic	t
All	TenantAdvanced	t
All	TenantFull	t
All	PotentialTenantBasic	t
All	PotentialTenantAdvanced	t
All	PotentialTenantScreening	t
All	PotentialTenantFull	t
All	GuarantorBasic	t
All	GuarantorAdvanced	t
All	GuarantorFull	t
All	FinancialMoneyIN	t
All	FinancialAggregatedTransfer	t
All	FinancialPayments	t
All	FinancialFull	t
All	AccountSelf	t
All	EmployeeBasic	t
All	EmployeeFull	t
All	PortfolioBasic	t
All	PortfolioFull	t
All	LegalCollectionsBasic	t
All	LegalCollectionsFull	t
All	DashboardsGadgetsBasic	t
All	DashboardsGadgetsFull	t
All	AdminGeneral	t
All	AdminFinancial	t
All	AdminContent	t
\.


--
-- PostgreSQL database dump complete
--

