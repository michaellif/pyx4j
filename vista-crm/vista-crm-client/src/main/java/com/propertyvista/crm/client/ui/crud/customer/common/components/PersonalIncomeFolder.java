/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 1, 2011
 * @author vladlouk
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.customer.common.components;

import java.util.EnumSet;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.folder.CFolderItem;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;

import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.policy.policies.ApplicationDocumentationPolicy;
import com.propertyvista.domain.policy.policies.RestrictionsPolicy;
import com.propertyvista.domain.tenant.income.CustomerScreeningIncome;
import com.propertyvista.domain.tenant.income.IncomeSource;
import com.propertyvista.misc.VistaTODO;

public class PersonalIncomeFolder extends VistaBoxFolder<CustomerScreeningIncome> {

    private static final I18n i18n = I18n.get(PersonalIncomeFolder.class);

    private RestrictionsPolicy restrictionsPolicy = EntityFactory.create(RestrictionsPolicy.class);

    private ApplicationDocumentationPolicy documentationPolicy;

    public PersonalIncomeFolder(boolean modifyable) {
        super(CustomerScreeningIncome.class, modifyable);
    }

    public void setPolicyEntity(IEntity parentEntity) {
        ClientPolicyManager.obtainHierarchicalEffectivePolicy(parentEntity, RestrictionsPolicy.class, new DefaultAsyncCallback<RestrictionsPolicy>() {
            @Override
            public void onSuccess(RestrictionsPolicy result) {
                setRestrictionsPolicy(result);
            }
        });
        ClientPolicyManager.obtainHierarchicalEffectivePolicy(parentEntity, ApplicationDocumentationPolicy.class,
                new DefaultAsyncCallback<ApplicationDocumentationPolicy>() {
                    @Override
                    public void onSuccess(ApplicationDocumentationPolicy result) {
                        setDocumentationPolicy(result);
                    }
                });
    }

    public void setRestrictionsPolicy(RestrictionsPolicy policy) {
        restrictionsPolicy = policy;
        revalidate();
    }

    public void setDocumentationPolicy(ApplicationDocumentationPolicy policy) {
        documentationPolicy = policy;
        for (CComponent<?, ?, ?, ?> item : getComponents()) {
            ((PersonalIncomeEditor) ((CFolderItem<?>) item).getComponents().iterator().next()).onSetDocumentationPolicy();
        }
    }

    public ApplicationDocumentationPolicy getDocumentationPolicy() {
        return documentationPolicy;
    }

    @Override
    protected CForm<CustomerScreeningIncome> createItemForm(IObject<?> member) {
        return new PersonalIncomeEditor(this);
    }

    @Override
    protected void addItem() {
        new SelectEnumDialog<IncomeSource>(i18n.tr("Select Income Source"), EnumSet.allOf(IncomeSource.class)) {
            @Override
            public boolean onClickOk() {
                CustomerScreeningIncome item = EntityFactory.create(CustomerScreeningIncome.class);
                item.incomeSource().setValue(getSelectedType());
                addItem(item);
                return true;
            }
        }.show();
    }

    @Override
    public void addValidations() {
        super.addValidations();

        // waiting for 'soft mode' validation!
        if (!VistaTODO.VISTA_4498_Remove_Unnecessary_Validation_Screening_CRM) {
        }
    }
}