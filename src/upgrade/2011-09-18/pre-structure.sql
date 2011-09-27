-- #$Id$

RENAME TABLE Phone TO PropertyPhone;

RENAME TABLE BuildingContactInfocontacts_phones TO Building$contacts_phones;

DROP TABLE BuildingContactInfocontacts_contacts;
DROP TABLE ChargeItemserviceAgreement_serviceItem_adjustments;
DROP TABLE Warrantywarranty_items;
DROP TABLE Marketingmarketing_adBlurbs;
DROP TABLE Companycompany_contacts;
DROP TABLE Companycompany_emails;
DROP TABLE Companycompany_phones;
DROP TABLE ServiceAgreementserviceAgreement_concessions;
DROP TABLE ServiceAgreementserviceAgreement_featureItems;


