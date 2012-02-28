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
import com.propertvista.generator.ProductItemTypesGenerator;
import com.propertvista.generator.TaxGenerator;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.essentials.server.csv.EntityCSVReciver;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.domain.financial.GlCode;
import com.propertyvista.domain.financial.GlCodeCategory;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductItemType;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.tax.Tax;
import com.propertyvista.domain.tenant.lease.LeaseAdjustmentReason;

public class ProductCatalogPreloader extends AbstractDataPreloader {

    private static final int TAX_QUANTITY = 10;

    private static final int GLCODE_QUANTITY = 15;

    private List<GlCodeCategory> glcodeCategories;

    private List<GlCode> glcodes;

    private List<String> reasons;

    private void addGlCategoryDescriptions() {
        int i = 0;
        for (GlCodeCategory category : glcodeCategories) {
            for (GlCode code : glcodes) {

                int id = code.glCodeCategory().glCategoryId().getValue();
                if (category.glCategoryId().getValue() == id) {
                    category.glCodes().add(code);
                }
            }
            i++;
        }
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

        glcodeCategories = EntityCSVReciver.create(GlCodeCategory.class).loadFile(
                IOUtils.resourceFileName("glcode-categories.csv", ProductCatalogPreloader.class));
        glcodes = EntityCSVReciver.create(GlCode.class).loadFile(IOUtils.resourceFileName("glcodes.csv", ProductCatalogPreloader.class));
        reasons = Arrays.asList("Admin Expenses", "Commissions", "Maintenance Fees", "Maintenance Labor", "Legal Expenses", "Security", "Repairs Material",
                "Management Fee", "Rental Deposit", "Late Fee", "NSF Fees", "Application Fee", "Tenant Improvements", "Good Will - general", "Move In Charges",
                "Move Out Charges", "Deposit Forfeit", "Marketing And Promotion", "Billing Adjustment", "Misc Labor", "Misc Material", "Misc Generic(no tax)",
                "Misc Generic(tax included)");

        addGlCategoryDescriptions(); //extracts glcategory info from csv file, attached to glcodes
        List<Tax> taxes = new ArrayList<Tax>();

        for (int i = 0; i < TAX_QUANTITY; i++) {
            Tax tax = TaxGenerator.createTax(i);
            taxes.add(tax);
            Persistence.service().persist(tax);
        }

        Persistence.service().persist(glcodeCategories);

        EntityQueryCriteria<GlCode> glcodeCriteria = EntityQueryCriteria.create(GlCode.class);
        glcodes = Persistence.service().query(glcodeCriteria);

        ProductItemTypesGenerator generator = new ProductItemTypesGenerator(glcodes);
        Persistence.service().persist(generator.getServiceItemTypes());
        Persistence.service().persist(generator.getFeatureItemTypes());

        for (int l = 0; l < reasons.size(); l++) {
            LeaseAdjustmentReason lar = LeaseAdjustmentReasonGenerator.createLeaseAdjustmentReason(l, taxes, glcodes);
            lar.name().setValue(reasons.get(l));
            Persistence.service().persist(lar);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Created ").append(generator.getServiceItemTypes().size() + generator.getFeatureItemTypes().size()).append(" ChargeItemType");
        return sb.toString();
    }

}
