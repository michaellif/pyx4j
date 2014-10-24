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
-- Name: tmp_emails; Type: TABLE; Schema: _dba_; Owner: -; Tablespace: 
--

CREATE TABLE tmp_emails (
    order_in_policy integer,
    subject character varying(500),
    use_header boolean,
    use_footer boolean,
    content character varying(20845),
    template_type character varying(50)
);


--
-- Data for Name: tmp_emails; Type: TABLE DATA; Schema: _dba_; Owner: -
--

COPY tmp_emails (order_in_policy, subject, use_header, use_footer, content, template_type) FROM stdin;
22	${PortalLinks.CompanyName} - YOUR PAYMENT WAS NOT PROCESSED	t	t	Dear ${Tenant.FirstName},<br/><br/>Your payment of <b>${Payment.Amount}</b> on <b>${Payment.Date}</b> was <b>not</b> successfully processed for the following reason:<br/><br/><div style="margin-left:80px"><b>${Payment.RejectReason}</b></div><br/><br/>Where applicable, an administrative fee has been added to your account for this payment reversal as per your agreement.<br/><br/><b>Please sign in to your myCommunity account [[${PortalLinks.TenantPortalUrl}|here]] to resubmit your payment to avoid any legal consequences.</b> <br/><br/>For your reference, your payment Reference number for this transaction is:<br/><br/><div style="margin-left:80px">#<b>${Payment.ReferenceNumber}</b></div><br/><br/>You can review the status of your arrears on your myCommunity portal at anytime. To access your myCommunity Resident Portal click <b>[[${PortalLinks.TenantPortalUrl}|here]]</b><br/><br/>Thank you for choosing ${PortalLinks.CompanyName}.	PaymentReturned
23	Direct Debit Account Changed	t	t	Dear ${Tenant.FirstName},<br/><br/>Our Record indicate that you have made a Direct Debit Rent Payment in the past through your bank directly.As a result of the Building Ownership changing, your Account Number has been updated accordingly.<br/><br/>Your new Account Number is: ${Lease.BillingAccount}<br/><br/>Please ensure that you update your ACCOUNT NUMBER with your bank accordingly prior to making any future Bill Payments.<br/><br/><b><u>Failure to update the account number may result in the payment not being processed and delays in refunding your payment via your bank.</u></b><br/><br/>If you have set your bank payment to automatically schedule your monthly rent payments, you must cancel this scheduled transaction and set up a new schedule with the new account number information.<br/><br/>You may click <b>[[${PortalLinks.DirectBankingHelpUrl}|here]]</b> for further detailed payment instructions.<br/><br/>If you have any questions around your balance and/or available payment methods you can review them in your Resident Portal <b>[[${PortalLinks.TenantPortalUrl}|here]]</b>.<br/><br/>Thank you for choosing ${PortalLinks.CompanyName}.	DirectDebitAccountChanged
\.


--
-- PostgreSQL database dump complete
--

