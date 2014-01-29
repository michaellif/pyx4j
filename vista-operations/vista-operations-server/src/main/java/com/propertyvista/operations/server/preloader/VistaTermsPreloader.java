/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-08-25
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.operations.server.preloader;

import java.io.IOException;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IVersionedEntity.SaveAction;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.operations.domain.legal.LegalDocument;
import com.propertyvista.operations.domain.legal.VistaTerms;
import com.propertyvista.operations.domain.legal.VistaTerms.Target;
import com.propertyvista.shared.i18n.CompiledLocale;

public class VistaTermsPreloader extends AbstractDataPreloader {

    @Override
    public String create() {

        /*****************
         * N.B. While adding the new file of change file here please open defect for support to update the same datA in production. immediately or post version
         * installation.
         * 
         * The change in policy text is not covered by DBA migration script.
         ****************/

        createTerms(Target.PmcPropertyVistaService, "PmcVistaTerms.html", "TODO name it PMC terms");
        createTerms(Target.PmcCaledonTemplate, "PmcCaledonTemplateVistaTerms.html", "TODO name it CaledonTemplate");
        createTerms(Target.PmcCaledonSoleProprietorshipSection, "PmcCaledonSoleProprietorshipVistaTerms.html", "TODO name it SoleProprietorship");
        createTerms(Target.PmcPaymentPad, "PmcPaymentPadVistaTerms.html", "TODO name it PaymentPad");

        createTerms(Target.ResidentPortalTermsAndConditions, "VistaPortalTermsAndConditions.html", "TODO name it portal");

        createTerms(Target.TenantBillingTerms, "TenantBillingAndRefundPolicy.html", "TODO name it billing");
        createTerms(Target.TenantPreAuthorizedPaymentECheckTerms, "TenantPreAuthorizedPaymentECheckTerms.html", "TODO name it, ECheck");
        createTerms(Target.TenantPreAuthorizedPaymentCardTerms, "TenantPreAuthorizedPaymentCardTerms.html", "TODO name it, Cards");
        createTerms(Target.TenantPaymentWebPaymentFeeTerms, "TenantPaymentWebPaymentFeeTerms.html", "Web Payment Fee - Terms and Conditions");

        createTerms(Target.ProspectPortalTermsAndConditions, "ProspectPortalTermsAndConditions.html", "TODO Prospect Portal T&C");
        createTerms(Target.ProspectPortalPrivacyPolicy, "ProspectPortalPrivacyPolicy.html", "TODO Prospect Portal Privacy Policy");
        createTerms(Target.ResidentPortalTermsAndConditions, "ResidentPortalTermsAndConditions.html", "TODO Resident Portal T&C");
        createTerms(Target.ResidentPortalPrivacyPolicy, "ResidentPortalPrivacyPolicy.html", "TODO Resident Portal Privacy Policy");

        createTerms(Target.TenantSurePreAuthorizedPaymentsAgreement, "TenantSurePreAuthorizedPaymentsAgreement.html", "TODO TenantSure");

        return null;
    }

    @Override
    public String delete() {
        return null;
    }

    public void createTerms(VistaTerms.Target target, String termsSourceFile, String caption) {
        String termsContent;
        try {
            termsContent = IOUtils.getUTF8TextResource(termsSourceFile, VistaTermsPreloader.class);
        } catch (IOException e) {
            throw new Error(e);
        }
        if (termsContent == null) {
            throw new Error("Resource " + termsSourceFile + " not found to populate document " + target);
        }

        LegalDocument legalDocument = EntityFactory.create(LegalDocument.class);
        legalDocument.locale().setValue(CompiledLocale.en);
        legalDocument.content().setValue(termsContent);

        VistaTerms vistaTerms = EntityFactory.create(VistaTerms.class);
        vistaTerms.target().setValue(target);
        vistaTerms.version().caption().setValue(caption);
        vistaTerms.version().document().add(legalDocument);
        vistaTerms.saveAction().setValue(SaveAction.saveAsFinal);
        Persistence.service().persist(vistaTerms);
    }

}
