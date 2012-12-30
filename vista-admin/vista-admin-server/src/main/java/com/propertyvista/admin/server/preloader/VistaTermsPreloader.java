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
package com.propertyvista.admin.server.preloader;

import java.io.IOException;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IVersionedEntity.SaveAction;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.admin.domain.legal.LegalDocument;
import com.propertyvista.admin.domain.legal.VistaTerms;
import com.propertyvista.admin.domain.legal.VistaTerms.Target;
import com.propertyvista.shared.i18n.CompiledLocale;

public class VistaTermsPreloader extends AbstractDataPreloader {

    @Override
    public String create() {

        String tenantVistaTerms = "Tenant VistaTerms preload failed";
        String pmcVistaTerms = "Pmc VistaTerms preload failed";
        try {
            tenantVistaTerms = IOUtils.getTextResource("TenantVistaTerms.html", VistaTermsPreloader.class);
            pmcVistaTerms = IOUtils.getTextResource("PmcVistaTerms.html", VistaTermsPreloader.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LegalDocument tenantTermsDoc = EntityFactory.create(LegalDocument.class);
        tenantTermsDoc.locale().setValue(CompiledLocale.en);
        tenantTermsDoc.content().setValue(tenantVistaTerms);
        VistaTerms tenantTerms = EntityFactory.create(VistaTerms.class);
        tenantTerms.target().setValue(Target.Tenant);
        tenantTerms.version().document().add(tenantTermsDoc);
        tenantTerms.saveAction().setValue(SaveAction.saveAsFinal);
        Persistence.service().persist(tenantTerms);
        Persistence.service().commit();

        VistaTerms pmcTerms = EntityFactory.create(VistaTerms.class);
        LegalDocument pmcTermsDoc = EntityFactory.create(LegalDocument.class);
        pmcTermsDoc.locale().setValue(CompiledLocale.en);
        pmcTermsDoc.content().setValue(pmcVistaTerms);
        pmcTerms.target().setValue(Target.PMC);
        pmcTerms.version().document().add(pmcTermsDoc);
        pmcTerms.saveAction().setValue(SaveAction.saveAsFinal);
        Persistence.service().persist(pmcTerms);
        Persistence.service().commit();

        return null;
    }

    @Override
    public String delete() {
        return null;
    }

}
