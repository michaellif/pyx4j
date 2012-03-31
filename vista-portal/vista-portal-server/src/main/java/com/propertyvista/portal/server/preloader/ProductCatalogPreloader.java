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

import java.math.BigDecimal;
import java.util.List;

import com.propertvista.generator.ProductItemTypesGenerator;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.server.csv.EntityCSVReciver;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.domain.financial.GlCode;
import com.propertyvista.domain.financial.GlCodeCategory;
import com.propertyvista.domain.financial.offering.FeatureItemType;
import com.propertyvista.domain.financial.offering.ServiceItemType;
import com.propertyvista.domain.financial.tax.Tax;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.domain.tenant.lease.LeaseAdjustmentReason;

public class ProductCatalogPreloader extends AbstractDataPreloader {

    private void createGl() {
        List<GlCodeCategory> glcodeCategories = EntityCSVReciver.create(GlCodeCategory.class).loadFile(
                IOUtils.resourceFileName("glcode-categories.csv", ProductCatalogPreloader.class));
        List<GlCode> glcodes = EntityCSVReciver.create(GlCode.class).loadFile(IOUtils.resourceFileName("glcodes.csv", ProductCatalogPreloader.class));

        // Place Codes to categories
        for (GlCode code : glcodes) {
            categoryLoop: for (GlCodeCategory category : glcodeCategories) {
                if (category.categoryId().equals(code.glCodeCategory().categoryId())) {
                    category.glCodes().add(code);
                    break categoryLoop;
                }
            }
        }
        Persistence.service().persist(glcodeCategories);
    }

    private void createTaxes() {
        createTax("HST", "ON", new BigDecimal("0.13"), false);
    }

    private Tax createTax(String name, String authority, BigDecimal rate, Boolean compound) {
        Tax tax = EntityFactory.create(Tax.class);
        tax.name().setValue(name);
        tax.authority().setValue(authority);

        EntityQueryCriteria<Province> criteria = EntityQueryCriteria.create(Province.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().code(), authority));
        tax.policyNode().set(Persistence.service().retrieve(criteria));
        tax.rate().setValue(rate);
        tax.compound().setValue(compound);
        Persistence.service().persist(tax);
        return tax;
    }

    private void createProductItemTypes() {
        ProductItemTypesGenerator generator = new ProductItemTypesGenerator();

        // Assignee GL references
        for (ServiceItemType i : generator.getServiceItemTypes()) {
            EntityQueryCriteria<GlCode> criteria = EntityQueryCriteria.create(GlCode.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().codeId(), i.glCode().codeId()));
            i.glCode().set(Persistence.service().retrieve(criteria));
        }
        for (FeatureItemType i : generator.getFeatureItemTypes()) {
            EntityQueryCriteria<GlCode> criteria = EntityQueryCriteria.create(GlCode.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().codeId(), i.glCode().codeId()));
            i.glCode().set(Persistence.service().retrieve(criteria));
        }

        Persistence.service().persist(generator.getServiceItemTypes());
        Persistence.service().persist(generator.getFeatureItemTypes());
    }

    private void createLeaseAdjustmentReasons() {
        List<LeaseAdjustmentReason> reasons = EntityCSVReciver.create(LeaseAdjustmentReason.class).loadFile(
                IOUtils.resourceFileName("leaseAdjustmentReason.csv", ProductCatalogPreloader.class));

        // Assignee GL references
        for (LeaseAdjustmentReason i : reasons) {
            EntityQueryCriteria<GlCode> criteria = EntityQueryCriteria.create(GlCode.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().codeId(), i.glCode().codeId()));
            i.glCode().set(Persistence.service().retrieve(criteria));
        }
        Persistence.service().persist(reasons);
    }

    @Override
    public String create() {

        createGl();
        createTaxes();
        createProductItemTypes();
        createLeaseAdjustmentReasons();

        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String delete() {
        if (ApplicationMode.isDevelopment()) {
            return deleteAll(GlCodeCategory.class, GlCode.class, Tax.class, LeaseAdjustmentReason.class, ServiceItemType.class, FeatureItemType.class);
        } else {
            return "This is production";
        }
    }

}
