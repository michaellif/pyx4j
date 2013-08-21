--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = _dba_, pg_catalog;

DROP TABLE IF EXISTS _dba_.email_template;
SET search_path = _dba_, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: email_template; Type: TABLE; Schema: _dba_; Owner: psql_dba; Tablespace: 
--

CREATE TABLE email_template (
    order_in_policy integer,
    subject character varying(500),
    use_header boolean,
    use_footer boolean,
    content character varying(20845),
    template_type character varying(50)
);


-- ALTER TABLE _dba_.email_template OWNER TO psql_dba;

--
-- Data for Name: email_template; Type: TABLE DATA; Schema: _dba_; Owner: psql_dba
--

COPY email_template (order_in_policy, subject, use_header, use_footer, content, template_type) FROM stdin;
11	Maintenance Request Updated	t	t	<h3>Dear ${MaintenanceRequest.reporterName},</h3><br/><br/>This is to inform You that the Maintenance Request below has been updated:<br/><br/>Building: ${Building.PropertyMarketingName}<br/>Address: ${Building.Address}<br/>Unit: ${MaintenanceRequest.unitNo}<br/>Tenant: ${MaintenanceRequest.reporterName}<br/><br/>Summary: ${MaintenanceRequest.summary}<br/><br/>Issue: ${MaintenanceRequest.category}<br/>Priority: ${MaintenanceRequest.priority}<br/><br/>Description: ${MaintenanceRequest.description}<br/><br/>Permission to enter: ${MaintenanceRequest.permissionToEnter}<br/>Preferred Times:<br/> 1 - ${MaintenanceRequest.preferredDateTime1}<br/> 2 - ${MaintenanceRequest.preferredDateTime2}<br/><br/><a href="${MaintenanceRequest.requestViewUrl}">Request ID: ${MaintenanceRequest.requestId}</a><br/>Request Submitted: ${MaintenanceRequest.submitted}<br/>Current Status: ${MaintenanceRequest.status}<br/>	MaintenanceRequestUpdated
12	Maintenance Request Completed	t	t	<h3>Dear ${MaintenanceRequest.reporterName},</h3><br/><br/>This is to inform You that the Maintenance Request below has been completed and closed. If requested work has not been done to your satisfaction, please call the office to advise us accordingly.<br/>Please complete the survey to rate your experience <a href={16}>here</a>.<br/><br/>The following Maintenance Request has been closed:<br/><br/>Building: ${Building.PropertyMarketingName}<br/>Address: ${Building.Address}<br/>Unit: ${MaintenanceRequest.unitNo}<br/>Tenant: ${MaintenanceRequest.reporterName}<br/><br/>Summary: ${MaintenanceRequest.summary}<br/><br/>Issue: ${MaintenanceRequest.category}<br/>Priority: ${MaintenanceRequest.priority}<br/><br/>Description: ${MaintenanceRequest.description}<br/><br/>Permission to enter: ${MaintenanceRequest.permissionToEnter}<br/>Preferred Times:<br/> 1 - ${MaintenanceRequest.preferredDateTime1}<br/> 2 - ${MaintenanceRequest.preferredDateTime2}<br/><br/><a href="${MaintenanceRequest.requestViewUrl}">Request ID: ${MaintenanceRequest.requestId}</a><br/>Request Submitted: ${MaintenanceRequest.submitted}<br/>Current Status: ${MaintenanceRequest.status}<br/>	MaintenanceRequestCompleted
13	Maintenance Request Cancelled	t	t	<h3>Dear ${MaintenanceRequest.reporterName},</h3><br/><br/>This is to inform You that the Maintenance Request below has been cancelled for the following reason: <br/>${MaintenanceRequest.cancellationNote}<br/><br/>Building: ${Building.PropertyMarketingName}<br/>Address: ${Building.Address}<br/>Unit: ${MaintenanceRequest.unitNo}<br/>Tenant: ${MaintenanceRequest.reporterName}<br/><br/>Summary: ${MaintenanceRequest.summary}<br/><br/>Issue: ${MaintenanceRequest.category}<br/>Priority: ${MaintenanceRequest.priority}<br/><br/>Description: ${MaintenanceRequest.description}<br/><br/>Permission to enter: ${MaintenanceRequest.permissionToEnter}<br/>Preferred Times:<br/> 1 - ${MaintenanceRequest.preferredDateTime1}<br/> 2 - ${MaintenanceRequest.preferredDateTime2}<br/><br/><a href="${MaintenanceRequest.requestViewUrl}">Request ID: ${MaintenanceRequest.requestId}</a><br/>Request Submitted: ${MaintenanceRequest.submitted}<br/>Current Status: ${MaintenanceRequest.status}<br/>	MaintenanceRequestCancelled
9	New Work Order - ${MaintenanceRequest.propertyCode}, $${MaintenanceRequest.unitNo}	t	t	Building: ${MaintenanceRequest.propertyCode}<br/>Unit: ${MaintenanceRequest.unitNo}<br/>Tenant: ${MaintenanceRequest.reporterName}<br/>Summary: ${MaintenanceRequest.summary}<br/>Issue: ${MaintenanceRequest.category}<br/>Priority: ${MaintenanceRequest.priority}<br/>Description: ${MaintenanceRequest.description}<br/>Permission to enter: ${MaintenanceRequest.permissionToEnter}<br/>Preferred Times:<br/> 1 - ${MaintenanceRequest.preferredDateTime1}<br/> 2 - ${MaintenanceRequest.preferredDateTime2}<br/><a href="${MaintenanceRequest.requestViewUrl}">Request ID: ${MaintenanceRequest.requestId}</a><br/>Request Submitted: ${MaintenanceRequest.submitted}<br/>Current Status: ${MaintenanceRequest.status}<br/>	MaintenanceRequestCreatedPMC
10	Maintenance Request Received!	t	t	<h3>Dear ${MaintenanceRequest.reporterName},</h3><br/><br/>We are in receipt of your maintenance request. <br/>If you are experiencing one of the EMERGENCY situations listed below, in addition to submitting the online request, please call the emergency number provided to you or see your site staff immediately.<br/><br/><b>EMERGENCY</b> Maintenance Issues:<br/>  1. No heat in your unit<br/>  2. Lock out, lost keys, or lock problem<br/>  3. Leaking roof/ceiling<br/>  4. Sink that will not drain/is backing up.<br/><br/><b><u>Time To Attend</u></b><br/>While we endeavor to respond to every work order as soon as possible, please allow at least 2 business days to receive your Notice of Entry (indicating when staff will attend to your request) from the date you submit. Work orders are prioritized for attention.<br/><br/><b>PLEASE NOTE:</b>  Maintenance staff will review received requests and address them as required on a priority basis. Generally items related to safety, heat, etc. are addressed first. We thank you for your patience.<br/>To review your request please login to your account at:<br/>  ${MaintenanceRequest.requestViewUrl}<br/>The following Maintenance Request has been registered:<br/><br/>Building: ${Building.PropertyMarketingName}<br/>Address: ${Building.Address}<br/>Unit: ${MaintenanceRequest.unitNo}<br/>Tenant: ${MaintenanceRequest.reporterName}<br/><br/>Summary: ${MaintenanceRequest.summary}<br/><br/>Issue: ${MaintenanceRequest.category}<br/>Priority: ${MaintenanceRequest.priority}<br/><br/>Description: ${MaintenanceRequest.description}<br/><br/>Permission to enter: ${MaintenanceRequest.permissionToEnter}<br/>Preferred Times:<br/> 1 - ${MaintenanceRequest.preferredDateTime1}<br/> 2 - ${MaintenanceRequest.preferredDateTime2}<br/><br/><a href="${MaintenanceRequest.requestViewUrl}">Request ID: ${MaintenanceRequest.requestId}</a><br/>Request Submitted: ${MaintenanceRequest.submitted}<br/>Current Status: ${MaintenanceRequest.status}<br/>	MaintenanceRequestCreatedTenant
14	NOTICE OF ENTRY	t	t	<h3>Dear ${MaintenanceRequest.reporterName},</h3><br/><br/>This is to inform You that your landlord/agent will be entering your rental unit on ${MaintenanceRequestWO.scheduledDate} ${MaintenanceRequestWO.scheduledTimeSlot} to perform maintenance or repair (${MaintenanceRequestWO.workDescription}) in accordance with the following Maintenance Request:<br/><br/>Building: ${Building.PropertyMarketingName}<br/>Address: ${Building.Address}<br/>Unit: ${MaintenanceRequest.unitNo}<br/>Tenant: ${MaintenanceRequest.reporterName}<br/><br/>Summary: ${MaintenanceRequest.summary}<br/><br/>Issue: ${MaintenanceRequest.category}<br/>Priority: ${MaintenanceRequest.priority}<br/><br/>Description: ${MaintenanceRequest.description}<br/><br/>Permission to enter: ${MaintenanceRequest.permissionToEnter}<br/>Preferred Times:<br/> 1 - ${MaintenanceRequest.preferredDateTime1}<br/> 2 - ${MaintenanceRequest.preferredDateTime2}<br/><br/><a href="${MaintenanceRequest.requestViewUrl}">Request ID: ${MaintenanceRequest.requestId}</a><br/>Request Submitted: ${MaintenanceRequest.submitted}<br/>Current Status: ${MaintenanceRequest.status}<br/>	MaintenanceRequestEntryNotice
\.


--
-- PostgreSQL database dump complete
--

