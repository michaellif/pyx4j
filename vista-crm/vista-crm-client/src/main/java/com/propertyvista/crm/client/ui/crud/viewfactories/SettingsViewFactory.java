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
import com.pyx4j.site.client.ui.IPane;

import com.propertyvista.crm.client.ui.crud.profile.paymentmethods.PmcPaymentMethodsEditorView;
import com.propertyvista.crm.client.ui.crud.profile.paymentmethods.PmcPaymentMethodsEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.profile.paymentmethods.PmcPaymentMethodsViewerView;
import com.propertyvista.crm.client.ui.crud.profile.paymentmethods.PmcPaymentMethodsViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.settings.creditchecks.CustomerCreditCheckListerView;
import com.propertyvista.crm.client.ui.crud.settings.creditchecks.CustomerCreditCheckListerViewImpl;
import com.propertyvista.crm.client.ui.crud.settings.creditchecks.CustomerCreditCheckViewerView;
import com.propertyvista.crm.client.ui.crud.settings.creditchecks.CustomerCreditCheckViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.settings.financial.arcode.ARCodeEditorView;
import com.propertyvista.crm.client.ui.crud.settings.financial.arcode.ARCodeEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.settings.financial.arcode.ARCodeListerView;
import com.propertyvista.crm.client.ui.crud.settings.financial.arcode.ARCodeListerViewImpl;
import com.propertyvista.crm.client.ui.crud.settings.financial.arcode.ARCodeViewerView;
import com.propertyvista.crm.client.ui.crud.settings.financial.arcode.ARCodeViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.settings.financial.glcode.GlCodeCategoryEditorView;
import com.propertyvista.crm.client.ui.crud.settings.financial.glcode.GlCodeCategoryEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.settings.financial.glcode.GlCodeCategoryListerView;
import com.propertyvista.crm.client.ui.crud.settings.financial.glcode.GlCodeCategoryListerViewImpl;
import com.propertyvista.crm.client.ui.crud.settings.financial.glcode.GlCodeCategoryViewerView;
import com.propertyvista.crm.client.ui.crud.settings.financial.glcode.GlCodeCategoryViewerViewImpl;
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
import com.propertyvista.crm.client.ui.crud.settings.tenantsecurity.TenantSecurityView;
import com.propertyvista.crm.client.ui.crud.settings.tenantsecurity.TenantSecurityViewImpl;
import com.propertyvista.crm.client.ui.wizard.creditcheck.CreditCheckStatusView;
import com.propertyvista.crm.client.ui.wizard.creditcheck.CreditCheckStatusViewImpl;

public class SettingsViewFactory extends ViewFactoryBase {

    public static <E extends IEntity, T extends IPane> T instance(Class<T> type) {
        if (!map.containsKey(type)) {
            if (ARCodeListerView.class.equals(type)) {
                map.put(type, new ARCodeListerViewImpl());

            } else if (ARCodeViewerView.class.equals(type)) {
                map.put(type, new ARCodeViewerViewImpl());
            } else if (ARCodeEditorView.class.equals(type)) {
                map.put(type, new ARCodeEditorViewImpl());

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

            } else if (CustomerCreditCheckListerView.class.equals(type)) {
                map.put(type, new CustomerCreditCheckListerViewImpl());
            } else if (CustomerCreditCheckViewerView.class.equals(type)) {
                map.put(type, new CustomerCreditCheckViewerViewImpl());

            } else if (CreditCheckStatusView.class.equals(type)) {
                map.put(type, new CreditCheckStatusViewImpl());

            } else if (PmcPaymentMethodsViewerView.class.equals(type)) {
                map.put(type, new PmcPaymentMethodsViewerViewImpl());
            } else if (PmcPaymentMethodsEditorView.class.equals(type)) {
                map.put(type, new PmcPaymentMethodsEditorViewImpl());

            } else if (TenantSecurityView.class.equals(type)) {
                map.put(type, new TenantSecurityViewImpl());
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
