/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 13, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.ptapp.services.steps.welcome;

import java.math.BigDecimal;
import java.util.Date;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.EmergencyContact;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.Concession.Term;
import com.propertyvista.domain.financial.offering.ProductItemType;
import com.propertyvista.domain.person.Name.Prefix;
import com.propertyvista.domain.person.Person.Sex;
import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.policies.LeaseTermsPolicy;
import com.propertyvista.domain.policy.policies.domain.LegalTermsDescriptor;
import com.propertyvista.domain.tenant.PersonRelationship;
import com.propertyvista.domain.tenant.Tenant.Role;
import com.propertyvista.domain.tenant.ptapp.DigitalSignature;
import com.propertyvista.dto.TenantInLeaseDTO;
import com.propertyvista.dto.TenantInfoDTO;
import com.propertyvista.portal.domain.ptapp.IAgree;
import com.propertyvista.portal.rpc.ptapp.dto.ApartmentInfoSummaryDTO;
import com.propertyvista.portal.rpc.ptapp.dto.LegalTermsDescriptorDTO;
import com.propertyvista.portal.rpc.ptapp.services.steps.welcome.LeaseReviewService;
import com.propertyvista.portal.rpc.ptapp.welcomewizard.LeaseReviewDTO;
import com.propertyvista.portal.server.ptapp.services.util.LegalStuffUtils;
import com.propertyvista.server.common.policy.PolicyManager;

public class LeaseReviewServiceImpl implements LeaseReviewService {

    @Override
    public void retrieve(AsyncCallback<LeaseReviewDTO> callback, Key tenantId) {
        LeaseReviewDTO mockupLeaseReview = EntityFactory.create(LeaseReviewDTO.class);

        ApartmentInfoSummaryDTO appartmentSummary = EntityFactory.create(ApartmentInfoSummaryDTO.class);
        appartmentSummary.floorplan().setValue("4-bedroom + den");
        appartmentSummary.bedrooms().setValue("4");
        appartmentSummary.dens().setValue("1");
        appartmentSummary.landlordName().setValue("");
        appartmentSummary.address().setValue("1 Yonge Street, unit 999, Toronto ON");
        mockupLeaseReview.apartmentSummary().add(appartmentSummary);

        mockupLeaseReview.selectedUnit().leaseFrom().setValue(new LogicalDate(2012 - 1900, 5, 1));
        mockupLeaseReview.selectedUnit().leaseTo().setValue(new LogicalDate(2015 - 1900, 5, 1));
        mockupLeaseReview.selectedUnit().unitRent().setValue(new BigDecimal("999.99"));

        Concession consession = mockupLeaseReview.selectedUnit().concessions().$();
        consession.version().type().setValue(Concession.Type.promotionalItem);
        consession.version().value().setValue(new BigDecimal("100"));
        consession.version().term().setValue(Term.firstMonth);
        consession.version().description().setValue("spring discount");
        mockupLeaseReview.selectedUnit().concessions().add(consession);

        ProductItemType product = mockupLeaseReview.selectedUnit().includedUtilities().$();
        product.name().setValue("hot water");
        mockupLeaseReview.selectedUnit().includedUtilities().add(product);

        TenantInLeaseDTO tenant = mockupLeaseReview.tenantList().tenants().$();
        tenant.customer().person().name().namePrefix().setValue(Prefix.Mr);
        tenant.customer().person().name().firstName().setValue("Frodo");
        tenant.customer().person().name().lastName().setValue("Baggins");
        tenant.customer().person().email().setValue("frodob@shire.net");
        tenant.customer().person().birthDate().setValue(new LogicalDate(1997 - 1900, 1, 1));
        tenant.customer().person().sex().setValue(Sex.Male);
        tenant.relationship().setValue(PersonRelationship.Son);
        tenant.role().setValue(Role.Applicant);
        mockupLeaseReview.tenantList().tenants().add(tenant);

        TenantInfoDTO tenantInfo = mockupLeaseReview.tenantsWithInfo().$();
        tenantInfo.person().set(tenant.customer().person());
        EmergencyContact emergencyContact = tenantInfo.emergencyContacts().$();
        emergencyContact.name().namePrefix().setValue(Prefix.Dr);
        emergencyContact.name().firstName().setValue("Gandalf");
        emergencyContact.name().lastName().setValue("the White");
        emergencyContact.email().setValue("gandalf@whitecouncil.org");
        emergencyContact.mobilePhone().setValue("911");
        tenantInfo.emergencyContacts().add(emergencyContact);
        mockupLeaseReview.tenantsWithInfo().add(tenantInfo);

        IAgree agreeHolder = EntityFactory.create(IAgree.class);
        agreeHolder.person().set(tenant.customer().person());
        LeaseTermsPolicy leaseTermsPolicy = PolicyManager.obtainEffectivePolicy(EntityFactory.create(OrganizationPoliciesNode.class), LeaseTermsPolicy.class);
        for (LegalTermsDescriptor legalTerms : leaseTermsPolicy.tenantSummaryTerms()) {
            LegalTermsDescriptorDTO legalTermsDTO = LegalStuffUtils.formLegalTerms(legalTerms);
            legalTermsDTO.agrees().add(agreeHolder.duplicate(IAgree.class));
            mockupLeaseReview.leaseTerms().add(legalTermsDTO);
        }

        {
            LegalTermsDescriptorDTO legalTermsDTO = LegalStuffUtils.formLegalTerms(leaseTermsPolicy.oneTimePaymentTerms());
            legalTermsDTO.agrees().add(agreeHolder.duplicate(IAgree.class));
            mockupLeaseReview.leaseTerms().add(legalTermsDTO);
        }

        {
            LegalTermsDescriptorDTO legalTermsDTO = LegalStuffUtils.formLegalTerms(leaseTermsPolicy.recurrentPaymentTerms());
            legalTermsDTO.agrees().add(agreeHolder.duplicate(IAgree.class));
            mockupLeaseReview.leaseTerms().add(legalTermsDTO);
        }

        DigitalSignature signature = mockupLeaseReview.application().signatures().$();
        signature.person().set(tenant.customer());
        signature.timestamp().setValue(new Date());
        signature.ipAddress().setValue(com.pyx4j.server.contexts.Context.getRequestRemoteAddr());
        mockupLeaseReview.application().signatures().add(signature);

        callback.onSuccess(mockupLeaseReview);
    }

    @Override
    public void save(AsyncCallback<LeaseReviewDTO> callback, LeaseReviewDTO editableEntity) {
        callback.onSuccess(editableEntity);
    }

}
