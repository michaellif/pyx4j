/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 6, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.propertvista.generator.LeaseAdjustmentReasonGenerator;
import com.propertvista.generator.GLCodeGenerator;
import com.propertvista.generator.ProductItemTypesGenerator;
import com.propertvista.generator.TaxGenerator;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;

import com.propertyvista.domain.financial.GlCode;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductItemType;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.tax.Tax;
import com.propertyvista.domain.tenant.lease.LeaseAdjustmentReason;

public class ProductCatalogPreloader extends AbstractDataPreloader {

    private static final int TAX_QUANTITY = 10;

    private static final int GLCODE_QUANTITY = 15;

    private static final List<String> reasons = Arrays.asList("Admin Expenses", "Commisions", "Maintenance Fees", "Maintenance Labour", "Legal Expences",
            "Security", "Repairs Material", "Management Fee", "Rental Deposit", "Late Fee", "NSF Fees", "Application Fee", "Tenant Improvements",
            "Good Will - general", "Move In Charges", "Move Out Charges", "Deposit Forfeit", "Marketing And Promotion", "Billing Adjustment", "Misc Labour",
            "Misc Material", "Misc Generic(no tax)", "Misc Generic(tax included)");

    public ProductCatalogPreloader() {

    }

    @SuppressWarnings("unchecked")
    @Override
    public String delete() {
        if (ApplicationMode.isDevelopment()) {
            return deleteAll(ProductItemType.class, Service.class, Feature.class, Concession.class);
        } else {
            return "This is production";
        }
    }

    @Override
    public String create() {

        List<Tax> taxes = new ArrayList<Tax>();
        List<GlCode> glCodes = new ArrayList<GlCode>();

        for (int i = 0; i < TAX_QUANTITY; i++) {
            Tax tax = TaxGenerator.createTax(i);
            taxes.add(tax);
            Persistence.service().persist(tax);
        }
        for (int j = 0; j < GLCODE_QUANTITY; j++) {
            GlCode glCode = GLCodeGenerator.createGlCode();
            glCodes.add(glCode);
            Persistence.service().persist(glCode);
        }

        ProductItemTypesGenerator generator = new ProductItemTypesGenerator(glCodes);
        Persistence.service().persist(generator.getServiceItemTypes());
        Persistence.service().persist(generator.getFeatureItemTypes());

        for (int l = 0; l < reasons.size(); l++) {
            LeaseAdjustmentReason lar = LeaseAdjustmentReasonGenerator.createLeaseAdjustmentReason(l, taxes, glCodes);
            lar.name().setValue(reasons.get(l));
            Persistence.service().persist(lar);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Created ").append(generator.getServiceItemTypes().size() + generator.getFeatureItemTypes().size()).append(" ChargeItemType");
        return sb.toString();
    }

}
