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
import com.propertyvista.crm.client.ui.crud.policies.ar.ARPolicyEditorView;
import com.propertyvista.crm.client.ui.crud.policies.ar.ARPolicyEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.policies.ar.ARPolicyListerView;
import com.propertyvista.crm.client.ui.crud.policies.ar.ARPolicyListerViewImpl;
import com.propertyvista.crm.client.ui.crud.policies.ar.ARPolicyViewerView;
import com.propertyvista.crm.client.ui.crud.policies.ar.ARPolicyViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.policies.backgroundcheck.BackgroundCheckPolicyEditorView;
import com.propertyvista.crm.client.ui.crud.policies.backgroundcheck.BackgroundCheckPolicyEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.policies.backgroundcheck.BackgroundCheckPolicyListerView;
import com.propertyvista.crm.client.ui.crud.policies.backgroundcheck.BackgroundCheckPolicyListerViewImpl;
import com.propertyvista.crm.client.ui.crud.policies.backgroundcheck.BackgroundCheckPolicyViewerView;
import com.propertyvista.crm.client.ui.crud.policies.backgroundcheck.BackgroundCheckPolicyViewerViewImpl;
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
import com.propertyvista.crm.client.ui.crud.policies.idassignment.IdAssignmentPolicyEditorView;
import com.propertyvista.crm.client.ui.crud.policies.idassignment.IdAssignmentPolicyEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.policies.idassignment.IdAssignmentPolicyListerView;
import com.propertyvista.crm.client.ui.crud.policies.idassignment.IdAssignmentPolicyListerViewImpl;
import com.propertyvista.crm.client.ui.crud.policies.idassignment.IdAssignmentPolicyViewerView;
import com.propertyvista.crm.client.ui.crud.policies.idassignment.IdAssignmentPolicyViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.policies.leaseadjustment.LeaseAdjustmentPolicyEditorView;
import com.propertyvista.crm.client.ui.crud.policies.leaseadjustment.LeaseAdjustmentPolicyEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.policies.leaseadjustment.LeaseAdjustmentPolicyListerView;
import com.propertyvista.crm.client.ui.crud.policies.leaseadjustment.LeaseAdjustmentPolicyListerViewImpl;
import com.propertyvista.crm.client.ui.crud.policies.leaseadjustment.LeaseAdjustmentPolicyViewerView;
import com.propertyvista.crm.client.ui.crud.policies.leaseadjustment.LeaseAdjustmentPolicyViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.policies.leasebilling.LeaseBillingPolicyEditorView;
import com.propertyvista.crm.client.ui.crud.policies.leasebilling.LeaseBillingPolicyEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.policies.leasebilling.LeaseBillingPolicyListerView;
import com.propertyvista.crm.client.ui.crud.policies.leasebilling.LeaseBillingPolicyListerViewImpl;
import com.propertyvista.crm.client.ui.crud.policies.leasebilling.LeaseBillingPolicyViewerView;
import com.propertyvista.crm.client.ui.crud.policies.leasebilling.LeaseBillingPolicyViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.policies.leaseterms.LegalDocumentationPolicyEditorView;
import com.propertyvista.crm.client.ui.crud.policies.leaseterms.LegalDocumentationPolicyEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.policies.leaseterms.LegalDocumentationPolicyListerView;
import com.propertyvista.crm.client.ui.crud.policies.leaseterms.LegalDocumentationPolicyListerViewImpl;
import com.propertyvista.crm.client.ui.crud.policies.misc.DatesPolicyEditorView;
import com.propertyvista.crm.client.ui.crud.policies.misc.DatesPolicyEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.policies.misc.DatesPolicyListerView;
import com.propertyvista.crm.client.ui.crud.policies.misc.DatesPolicyListerViewImpl;
import com.propertyvista.crm.client.ui.crud.policies.misc.DatesPolicyViewerView;
import com.propertyvista.crm.client.ui.crud.policies.misc.DatesPolicyViewerViewImpl;
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
import com.propertyvista.crm.client.ui.crud.policies.restrictions.RestrictionsPolicyEditorView;
import com.propertyvista.crm.client.ui.crud.policies.restrictions.RestrictionsPolicyEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.policies.restrictions.RestrictionsPolicyListerView;
import com.propertyvista.crm.client.ui.crud.policies.restrictions.RestrictionsPolicyListerViewImpl;
import com.propertyvista.crm.client.ui.crud.policies.restrictions.RestrictionsPolicyViewerView;
import com.propertyvista.crm.client.ui.crud.policies.restrictions.RestrictionsPolicyViewerViewImpl;

public class PolicyViewFactory extends ViewFactoryBase {
    public static <E extends IEntity, T extends IView> T instance(Class<T> type) {
        if (!map.containsKey(type)) {
            if (ApplicationDocumentationPolicyListerView.class.equals(type)) {
                map.put(ApplicationDocumentationPolicyListerView.class, new ApplicationDocumentationPolicyListerViewImpl());
            } else if (ApplicationDocumentationPolicyEdtiorView.class.equals(type)) {
                map.put(ApplicationDocumentationPolicyEdtiorView.class, new ApplicationDocumentationPolicyEditorViewImpl());

            } else if (LegalDocumentationPolicyListerView.class.equals(type)) {
                map.put(LegalDocumentationPolicyListerView.class, new LegalDocumentationPolicyListerViewImpl());
            } else if (LegalDocumentationPolicyEditorView.class.equals(type)) {
                map.put(LegalDocumentationPolicyEditorView.class, new LegalDocumentationPolicyEditorViewImpl());

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

            } else if (DatesPolicyListerView.class.equals(type)) {
                map.put(DatesPolicyListerView.class, new DatesPolicyListerViewImpl());
            } else if (DatesPolicyEditorView.class.equals(type)) {
                map.put(DatesPolicyEditorView.class, new DatesPolicyEditorViewImpl());
            } else if (DatesPolicyViewerView.class.equals(type)) {
                map.put(DatesPolicyViewerView.class, new DatesPolicyViewerViewImpl());

            } else if (RestrictionsPolicyListerView.class.equals(type)) {
                map.put(RestrictionsPolicyListerView.class, new RestrictionsPolicyListerViewImpl());
            } else if (RestrictionsPolicyViewerView.class.equals(type)) {
                map.put(RestrictionsPolicyViewerView.class, new RestrictionsPolicyViewerViewImpl());
            } else if (RestrictionsPolicyEditorView.class.equals(type)) {
                map.put(RestrictionsPolicyEditorView.class, new RestrictionsPolicyEditorViewImpl());

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

            } else if (BackgroundCheckPolicyListerView.class.equals(type)) {
                map.put(BackgroundCheckPolicyListerView.class, new BackgroundCheckPolicyListerViewImpl());
            } else if (BackgroundCheckPolicyEditorView.class.equals(type)) {
                map.put(BackgroundCheckPolicyEditorView.class, new BackgroundCheckPolicyEditorViewImpl());
            } else if (BackgroundCheckPolicyViewerView.class.equals(type)) {
                map.put(BackgroundCheckPolicyViewerView.class, new BackgroundCheckPolicyViewerViewImpl());

            } else if (LeaseBillingPolicyListerView.class.equals(type)) {
                map.put(LeaseBillingPolicyListerView.class, new LeaseBillingPolicyListerViewImpl());
            } else if (LeaseBillingPolicyEditorView.class.equals(type)) {
                map.put(LeaseBillingPolicyEditorView.class, new LeaseBillingPolicyEditorViewImpl());
            } else if (LeaseBillingPolicyViewerView.class.equals(type)) {
                map.put(LeaseBillingPolicyViewerView.class, new LeaseBillingPolicyViewerViewImpl());

            } else if (IdAssignmentPolicyListerView.class.equals(type)) {
                map.put(IdAssignmentPolicyListerView.class, new IdAssignmentPolicyListerViewImpl());
            } else if (IdAssignmentPolicyEditorView.class.equals(type)) {
                map.put(IdAssignmentPolicyEditorView.class, new IdAssignmentPolicyEditorViewImpl());
            } else if (IdAssignmentPolicyViewerView.class.equals(type)) {
                map.put(IdAssignmentPolicyViewerView.class, new IdAssignmentPolicyViewerViewImpl());

            } else if (ARPolicyListerView.class.equals(type)) {
                map.put(ARPolicyListerView.class, new ARPolicyListerViewImpl());
            } else if (ARPolicyEditorView.class.equals(type)) {
                map.put(ARPolicyEditorView.class, new ARPolicyEditorViewImpl());
            } else if (ARPolicyViewerView.class.equals(type)) {
                map.put(ARPolicyViewerView.class, new ARPolicyViewerViewImpl());
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
