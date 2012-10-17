/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.viewfactories;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.client.ui.crud.IView;

import com.propertyvista.crm.client.ui.crud.settings.content.gadgets.HomePageGadgetEditor;
import com.propertyvista.crm.client.ui.crud.settings.content.gadgets.HomePageGadgetEditorImpl;
import com.propertyvista.crm.client.ui.crud.settings.content.gadgets.HomePageGadgetViewer;
import com.propertyvista.crm.client.ui.crud.settings.content.gadgets.HomePageGadgetViewerImpl;
import com.propertyvista.crm.client.ui.crud.settings.content.page.PageEditor;
import com.propertyvista.crm.client.ui.crud.settings.content.page.PageEditorImpl;
import com.propertyvista.crm.client.ui.crud.settings.content.page.PageViewer;
import com.propertyvista.crm.client.ui.crud.settings.content.page.PageViewerImpl;
import com.propertyvista.crm.client.ui.crud.settings.content.site.SiteEditor;
import com.propertyvista.crm.client.ui.crud.settings.content.site.SiteEditorImpl;
import com.propertyvista.crm.client.ui.crud.settings.content.site.SiteViewer;
import com.propertyvista.crm.client.ui.crud.settings.content.site.SiteViewerImpl;
import com.propertyvista.crm.client.ui.crud.settings.creditchecks.PersonCreditCheckListerView;
import com.propertyvista.crm.client.ui.crud.settings.creditchecks.PersonCreditCheckListerViewImpl;
import com.propertyvista.crm.client.ui.crud.settings.creditchecks.PersonCreditCheckViewerView;
import com.propertyvista.crm.client.ui.crud.settings.creditchecks.PersonCreditCheckViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.settings.financial.glcode.GlCodeCategoryEditorView;
import com.propertyvista.crm.client.ui.crud.settings.financial.glcode.GlCodeCategoryEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.settings.financial.glcode.GlCodeCategoryListerView;
import com.propertyvista.crm.client.ui.crud.settings.financial.glcode.GlCodeCategoryListerViewImpl;
import com.propertyvista.crm.client.ui.crud.settings.financial.glcode.GlCodeCategoryViewerView;
import com.propertyvista.crm.client.ui.crud.settings.financial.glcode.GlCodeCategoryViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.settings.financial.leaseadjustmentreason.LeaseAdjustmentReasonEditorView;
import com.propertyvista.crm.client.ui.crud.settings.financial.leaseadjustmentreason.LeaseAdjustmentReasonEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.settings.financial.leaseadjustmentreason.LeaseAdjustmentReasonListerView;
import com.propertyvista.crm.client.ui.crud.settings.financial.leaseadjustmentreason.LeaseAdjustmentReasonListerViewImpl;
import com.propertyvista.crm.client.ui.crud.settings.financial.leaseadjustmentreason.LeaseAdjustmentReasonViewerView;
import com.propertyvista.crm.client.ui.crud.settings.financial.leaseadjustmentreason.LeaseAdjustmentReasonViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.settings.financial.producttype.FeatureTypeEditorView;
import com.propertyvista.crm.client.ui.crud.settings.financial.producttype.FeatureTypeEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.settings.financial.producttype.FeatureTypeViewerView;
import com.propertyvista.crm.client.ui.crud.settings.financial.producttype.FeatureTypeViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.settings.financial.producttype.ProductDictionaryView;
import com.propertyvista.crm.client.ui.crud.settings.financial.producttype.ProductDictionaryViewImpl;
import com.propertyvista.crm.client.ui.crud.settings.financial.producttype.ServiceTypeEditorView;
import com.propertyvista.crm.client.ui.crud.settings.financial.producttype.ServiceTypeEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.settings.financial.producttype.ServiceTypeViewerView;
import com.propertyvista.crm.client.ui.crud.settings.financial.producttype.ServiceTypeViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.settings.financial.tax.TaxEditorView;
import com.propertyvista.crm.client.ui.crud.settings.financial.tax.TaxEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.settings.financial.tax.TaxListerView;
import com.propertyvista.crm.client.ui.crud.settings.financial.tax.TaxListerViewImpl;
import com.propertyvista.crm.client.ui.crud.settings.financial.tax.TaxViewerView;
import com.propertyvista.crm.client.ui.crud.settings.financial.tax.TaxViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.settings.merchantaccount.MerchantAccountEditorView;
import com.propertyvista.crm.client.ui.crud.settings.merchantaccount.MerchantAccountEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.settings.merchantaccount.MerchantAccountListerView;
import com.propertyvista.crm.client.ui.crud.settings.merchantaccount.MerchantAccountListerViewImpl;
import com.propertyvista.crm.client.ui.crud.settings.merchantaccount.MerchantAccountViewerView;
import com.propertyvista.crm.client.ui.crud.settings.merchantaccount.MerchantAccountViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.settings.role.CrmRoleEditorView;
import com.propertyvista.crm.client.ui.crud.settings.role.CrmRoleEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.settings.role.CrmRoleListerView;
import com.propertyvista.crm.client.ui.crud.settings.role.CrmRoleListerViewImpl;
import com.propertyvista.crm.client.ui.crud.settings.role.CrmRoleViewerView;
import com.propertyvista.crm.client.ui.crud.settings.role.CrmRoleViewerViewImpl;

public class SettingsViewFactory extends ViewFactoryBase {

    public static <E extends IEntity, T extends IView> T instance(Class<T> type) {
        if (!map.containsKey(type)) {
            if (SiteViewer.class.equals(type)) {
                map.put(type, new SiteViewerImpl());
            } else if (SiteEditor.class.equals(type)) {
                map.put(type, new SiteEditorImpl());

            } else if (PageViewer.class.equals(type)) {
                map.put(type, new PageViewerImpl());
            } else if (PageEditor.class.equals(type)) {
                map.put(type, new PageEditorImpl());

            } else if (HomePageGadgetViewer.class.equals(type)) {
                map.put(type, new HomePageGadgetViewerImpl());
            } else if (HomePageGadgetEditor.class.equals(type)) {
                map.put(type, new HomePageGadgetEditorImpl());

            } else if (ProductDictionaryView.class.equals(type)) {
                map.put(type, new ProductDictionaryViewImpl());

            } else if (ServiceTypeViewerView.class.equals(type)) {
                map.put(type, new ServiceTypeViewerViewImpl());
            } else if (ServiceTypeEditorView.class.equals(type)) {
                map.put(type, new ServiceTypeEditorViewImpl());

            } else if (FeatureTypeViewerView.class.equals(type)) {
                map.put(type, new FeatureTypeViewerViewImpl());
            } else if (FeatureTypeEditorView.class.equals(type)) {
                map.put(type, new FeatureTypeEditorViewImpl());

            } else if (CrmRoleListerView.class.equals(type)) {
                map.put(type, new CrmRoleListerViewImpl());
            } else if (CrmRoleEditorView.class.equals(type)) {
                map.put(type, new CrmRoleEditorViewImpl());
            } else if (CrmRoleViewerView.class.equals(type)) {
                map.put(type, new CrmRoleViewerViewImpl());

            } else if (MerchantAccountListerView.class.equals(type)) {
                map.put(type, new MerchantAccountListerViewImpl());
            } else if (MerchantAccountEditorView.class.equals(type)) {
                map.put(type, new MerchantAccountEditorViewImpl());
            } else if (MerchantAccountViewerView.class.equals(type)) {
                map.put(type, new MerchantAccountViewerViewImpl());

            } else if (TaxListerView.class.equals(type)) {
                map.put(type, new TaxListerViewImpl());
            } else if (TaxEditorView.class.equals(type)) {
                map.put(type, new TaxEditorViewImpl());
            } else if (TaxViewerView.class.equals(type)) {
                map.put(type, new TaxViewerViewImpl());

            } else if (GlCodeCategoryListerView.class.equals(type)) {
                map.put(type, new GlCodeCategoryListerViewImpl());
            } else if (GlCodeCategoryEditorView.class.equals(type)) {
                map.put(type, new GlCodeCategoryEditorViewImpl());
            } else if (GlCodeCategoryViewerView.class.equals(type)) {
                map.put(type, new GlCodeCategoryViewerViewImpl());

            } else if (LeaseAdjustmentReasonListerView.class.equals(type)) {
                map.put(type, new LeaseAdjustmentReasonListerViewImpl());
            } else if (LeaseAdjustmentReasonEditorView.class.equals(type)) {
                map.put(type, new LeaseAdjustmentReasonEditorViewImpl());
            } else if (LeaseAdjustmentReasonViewerView.class.equals(type)) {
                map.put(type, new LeaseAdjustmentReasonViewerViewImpl());

            } else if (PersonCreditCheckListerView.class.equals(type)) {
                map.put(type, new PersonCreditCheckListerViewImpl());
            } else if (PersonCreditCheckViewerView.class.equals(type)) {
                map.put(type, new PersonCreditCheckViewerViewImpl());
            }
        }
        @SuppressWarnings("unchecked")
        T impl = (T) map.get(type);
        if (impl == null) {
            throw new Error("implementation of " + type.getName() + " not found");
        }
        return impl;
    }
}
