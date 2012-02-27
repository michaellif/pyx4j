/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 22, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.viewfactories;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.client.ui.crud.IView;

import com.propertyvista.crm.client.ui.crud.policies.applicationdocumentation.ApplicationDocumentationPolicyEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.policies.applicationdocumentation.ApplicationDocumentationPolicyEdtiorView;
import com.propertyvista.crm.client.ui.crud.policies.applicationdocumentation.ApplicationDocumentationPolicyListerView;
import com.propertyvista.crm.client.ui.crud.policies.applicationdocumentation.ApplicationDocumentationPolicyListerViewImpl;
import com.propertyvista.crm.client.ui.crud.policies.deposit.DepositPolicyEditorView;
import com.propertyvista.crm.client.ui.crud.policies.deposit.DepositPolicyEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.policies.deposit.DepositPolicyListerView;
import com.propertyvista.crm.client.ui.crud.policies.deposit.DepositPolicyListerViewImpl;
import com.propertyvista.crm.client.ui.crud.policies.deposit.DepositPolicyViewerView;
import com.propertyvista.crm.client.ui.crud.policies.deposit.DepositPolicyViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.policies.emailtemplates.EmailTemplatesPolicyEditorView;
import com.propertyvista.crm.client.ui.crud.policies.emailtemplates.EmailTemplatesPolicyEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.policies.emailtemplates.EmailTemplatesPolicyListerView;
import com.propertyvista.crm.client.ui.crud.policies.emailtemplates.EmailTemplatesPolicyListerViewImpl;
import com.propertyvista.crm.client.ui.crud.policies.emailtemplates.EmailTemplatesPolicyViewerView;
import com.propertyvista.crm.client.ui.crud.policies.emailtemplates.EmailTemplatesPolicyViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.policies.leaseadjustment.LeaseAdjustmentPolicyEditorView;
import com.propertyvista.crm.client.ui.crud.policies.leaseadjustment.LeaseAdjustmentPolicyEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.policies.leaseadjustment.LeaseAdjustmentPolicyListerView;
import com.propertyvista.crm.client.ui.crud.policies.leaseadjustment.LeaseAdjustmentPolicyListerViewImpl;
import com.propertyvista.crm.client.ui.crud.policies.leaseadjustment.LeaseAdjustmentPolicyViewerView;
import com.propertyvista.crm.client.ui.crud.policies.leaseadjustment.LeaseAdjustmentPolicyViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.policies.leaseterms.LeaseTermsPolicyEditorView;
import com.propertyvista.crm.client.ui.crud.policies.leaseterms.LeaseTermsPolicyEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.policies.leaseterms.LeaseTermsPolicyListerView;
import com.propertyvista.crm.client.ui.crud.policies.leaseterms.LeaseTermsPolicyListerViewImpl;
import com.propertyvista.crm.client.ui.crud.policies.misc.MiscPolicyEditorView;
import com.propertyvista.crm.client.ui.crud.policies.misc.MiscPolicyEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.policies.misc.MiscPolicyListerView;
import com.propertyvista.crm.client.ui.crud.policies.misc.MiscPolicyListerViewImpl;
import com.propertyvista.crm.client.ui.crud.policies.misc.MiscPolicyViewerView;
import com.propertyvista.crm.client.ui.crud.policies.misc.MiscPolicyViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.policies.pet.PetPolicyEditorView;
import com.propertyvista.crm.client.ui.crud.policies.pet.PetPolicyEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.policies.pet.PetPolicyListerView;
import com.propertyvista.crm.client.ui.crud.policies.pet.PetPolicyListerViewImpl;
import com.propertyvista.crm.client.ui.crud.policies.pet.PetPolicyViewerView;
import com.propertyvista.crm.client.ui.crud.policies.pet.PetPolicyViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.policies.producttaxes.ProductTaxPolicyEditorView;
import com.propertyvista.crm.client.ui.crud.policies.producttaxes.ProductTaxPolicyEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.policies.producttaxes.ProductTaxPolicyListerView;
import com.propertyvista.crm.client.ui.crud.policies.producttaxes.ProductTaxPolicyListerViewImpl;
import com.propertyvista.crm.client.ui.crud.policies.producttaxes.ProductTaxPolicyViewerView;
import com.propertyvista.crm.client.ui.crud.policies.producttaxes.ProductTaxPolicyViewerViewImpl;

public class PolicyViewFactory extends ViewFactoryBase {
    public static <E extends IEntity, T extends IView<E>> T instance(Class<T> type) {
        if (!map.containsKey(type)) {
            if (ApplicationDocumentationPolicyListerView.class.equals(type)) {
                map.put(ApplicationDocumentationPolicyListerView.class, new ApplicationDocumentationPolicyListerViewImpl());
            } else if (ApplicationDocumentationPolicyEdtiorView.class.equals(type)) {
                map.put(ApplicationDocumentationPolicyEdtiorView.class, new ApplicationDocumentationPolicyEditorViewImpl());

            } else if (LeaseTermsPolicyListerView.class.equals(type)) {
                map.put(LeaseTermsPolicyListerView.class, new LeaseTermsPolicyListerViewImpl());
            } else if (LeaseTermsPolicyEditorView.class.equals(type)) {
                map.put(LeaseTermsPolicyEditorView.class, new LeaseTermsPolicyEditorViewImpl());

            } else if (PetPolicyListerView.class.equals(type)) {
                map.put(PetPolicyListerView.class, new PetPolicyListerViewImpl());
            } else if (PetPolicyEditorView.class.equals(type)) {
                map.put(PetPolicyEditorView.class, new PetPolicyEditorViewImpl());
            } else if (PetPolicyViewerView.class.equals(type)) {
                map.put(PetPolicyViewerView.class, new PetPolicyViewerViewImpl());

            } else if (EmailTemplatesPolicyListerView.class.equals(type)) {
                map.put(EmailTemplatesPolicyListerView.class, new EmailTemplatesPolicyListerViewImpl());
            } else if (EmailTemplatesPolicyViewerView.class.equals(type)) {
                map.put(EmailTemplatesPolicyViewerView.class, new EmailTemplatesPolicyViewerViewImpl());
            } else if (EmailTemplatesPolicyEditorView.class.equals(type)) {
                map.put(EmailTemplatesPolicyEditorView.class, new EmailTemplatesPolicyEditorViewImpl());

            } else if (MiscPolicyListerView.class.equals(type)) {
                map.put(MiscPolicyListerView.class, new MiscPolicyListerViewImpl());
            } else if (MiscPolicyEditorView.class.equals(type)) {
                map.put(MiscPolicyEditorView.class, new MiscPolicyEditorViewImpl());
            } else if (MiscPolicyViewerView.class.equals(type)) {
                map.put(MiscPolicyViewerView.class, new MiscPolicyViewerViewImpl());

            } else if (ProductTaxPolicyListerView.class.equals(type)) {
                map.put(ProductTaxPolicyListerView.class, new ProductTaxPolicyListerViewImpl());
            } else if (ProductTaxPolicyEditorView.class.equals(type)) {
                map.put(ProductTaxPolicyEditorView.class, new ProductTaxPolicyEditorViewImpl());
            } else if (ProductTaxPolicyViewerView.class.equals(type)) {
                map.put(ProductTaxPolicyViewerView.class, new ProductTaxPolicyViewerViewImpl());

            } else if (LeaseAdjustmentPolicyListerView.class.equals(type)) {
                map.put(LeaseAdjustmentPolicyListerView.class, new LeaseAdjustmentPolicyListerViewImpl());
            } else if (LeaseAdjustmentPolicyEditorView.class.equals(type)) {
                map.put(LeaseAdjustmentPolicyEditorView.class, new LeaseAdjustmentPolicyEditorViewImpl());
            } else if (LeaseAdjustmentPolicyViewerView.class.equals(type)) {
                map.put(LeaseAdjustmentPolicyViewerView.class, new LeaseAdjustmentPolicyViewerViewImpl());

            } else if (DepositPolicyListerView.class.equals(type)) {
                map.put(DepositPolicyListerView.class, new DepositPolicyListerViewImpl());
            } else if (DepositPolicyEditorView.class.equals(type)) {
                map.put(DepositPolicyEditorView.class, new DepositPolicyEditorViewImpl());
            } else if (DepositPolicyViewerView.class.equals(type)) {
                map.put(DepositPolicyViewerView.class, new DepositPolicyViewerViewImpl());
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
