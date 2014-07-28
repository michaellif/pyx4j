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
All	ApplicationDecisionRecommendationApprove	t
All	ApplicationDecisionRecommendationFurtherMoreInfo	t
All	ApplicationDecisionAll	t
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
All	Communication	t
All	PortfolioBasic	t
All	PortfolioFull	t
All	LegalCollectionsBasic	t
All	LegalCollectionsFull	t
All	DashboardsGadgetsBasic	t
All	DashboardsGadgetsFull	t
All	AdminGeneral	t
All	AdminFinancial	t
All	AdminContent	t
PropertyVista Support	PropertyVistaSupport	f
Test-OAPI_Properties	OAPI_Properties	f
Test-OAPI_ILS	OAPI_ILS	f
Test-PropertyVistaSupport	PropertyVistaSupport	f
Test-BuildingBasic	BuildingBasic	f
Test-BuildingFinancial	BuildingFinancial	f
Test-BuildingAccounting	BuildingAccounting	f
Test-BuildingProperty	BuildingProperty	f
Test-BuildingMarketing	BuildingMarketing	f
Test-BuildingMechanicals	BuildingMechanicals	f
Test-BuildingAdministrator	BuildingAdministrator	f
Test-BuildingLeasing	BuildingLeasing	f
Test-YardiLoads	YardiLoads	f
Test-MaintenanceBasic	MaintenanceBasic	f
Test-MaintenanceAdvanced	MaintenanceAdvanced	f
Test-MaintenanceFull	MaintenanceFull	f
Test-LeaseBasic	LeaseBasic	f
Test-LeaseAdvanced	LeaseAdvanced	f
Test-LeaseFull	LeaseFull	f
Test-ApplicationBasic	ApplicationBasic	f
Test-ApplicationVerifyDoc	ApplicationVerifyDoc	f
Test-ApplicationFull	ApplicationFull	f
Test-ApplicationDecisionRecommendationApprove	ApplicationDecisionRecommendationApprove	f
Test-ApplicationDecisionRecommendationFurtherMoreInfo	ApplicationDecisionRecommendationFurtherMoreInfo	f
Test-ApplicationDecisionAll	ApplicationDecisionAll	f
Test-CreditCheckBasic	CreditCheckBasic	f
Test-CreditCheckFull	CreditCheckFull	f
Test-TenantBasic	TenantBasic	f
Test-TenantAdvanced	TenantAdvanced	f
Test-TenantFull	TenantFull	f
Test-PotentialTenantBasic	PotentialTenantBasic	f
Test-PotentialTenantAdvanced	PotentialTenantAdvanced	f
Test-PotentialTenantScreening	PotentialTenantScreening	f
Test-PotentialTenantFull	PotentialTenantFull	f
Test-GuarantorBasic	GuarantorBasic	f
Test-GuarantorAdvanced	GuarantorAdvanced	f
Test-GuarantorFull	GuarantorFull	f
Test-FinancialMoneyIN	FinancialMoneyIN	f
Test-FinancialAggregatedTransfer	FinancialAggregatedTransfer	f
Test-FinancialPayments	FinancialPayments	f
Test-FinancialFull	FinancialFull	f
Test-AccountSelf	AccountSelf	f
Test-EmployeeBasic	EmployeeBasic	f
Test-EmployeeFull	EmployeeFull	f
Test-Communication	Communication	f
Test-PortfolioBasic	PortfolioBasic	f
Test-PortfolioFull	PortfolioFull	f
Test-LegalCollectionsBasic	LegalCollectionsBasic	f
Test-LegalCollectionsFull	LegalCollectionsFull	f
Test-DashboardsGadgetsBasic	DashboardsGadgetsBasic	f
Test-DashboardsGadgetsFull	DashboardsGadgetsFull	f
Test-AdminGeneral	AdminGeneral	f
Test-AdminFinancial	AdminFinancial	f
Test-AdminContent	AdminContent	f
\.


--
-- PostgreSQL database dump complete
--

