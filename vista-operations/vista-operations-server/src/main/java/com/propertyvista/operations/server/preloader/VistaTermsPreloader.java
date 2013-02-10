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

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IVersionedEntity.SaveAction;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.operations.domain.legal.LegalDocument;
import com.propertyvista.operations.domain.legal.VistaTerms;
import com.propertyvista.operations.domain.legal.VistaTerms.Target;
import com.propertyvista.shared.i18n.CompiledLocale;

public class VistaTermsPreloader extends AbstractDataPreloader {

    @Override
    public String create() {

        createTerms(Target.Tenant, "TenantVistaTerms.html");
        createTerms(Target.PMC, "PmcVistaTerms.html");
        createTerms(Target.PmcCaledonTemplate, "PmcCaledonTemplateVistaTerms.html");
        createTerms(Target.PmcCaldedonSolePropetorshipSection, "PmcCaledonSoleProprietorshipVistaTerms.html");
        createTerms(Target.PmcPaymentPad, "PmcPaymentPadVistaTerms.html");
        createTerms(Target.TenantSurePreAuthorizedPaymentsAgreement, "TenantSurePreAuthorizedPaymentsAgreement.html");

        return null;
    }

    @Override
    public String delete() {
        return null;
    }

    public void createTerms(VistaTerms.Target target, String termsSourceFile) {

        String termsContent;
        try {
            termsContent = IOUtils.getUTF8TextResource(termsSourceFile, VistaTermsPreloader.class);
        } catch (IOException e) {
            throw new Error(e);
        }

        LegalDocument legalDocument = EntityFactory.create(LegalDocument.class);
        legalDocument.locale().setValue(CompiledLocale.en);
        legalDocument.content().setValue(termsContent);

        VistaTerms vistaTerms = EntityFactory.create(VistaTerms.class);
        vistaTerms.target().setValue(target);
        vistaTerms.version().document().add(legalDocument);
        vistaTerms.saveAction().setValue(SaveAction.saveAsFinal);
        Persistence.service().persist(vistaTerms);
        Persistence.service().commit();

    }

}
