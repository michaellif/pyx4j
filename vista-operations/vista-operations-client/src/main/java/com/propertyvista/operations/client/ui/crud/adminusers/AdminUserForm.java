/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 22, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.adminusers;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.ui.crud.IFormView;

import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.rpc.AdminUserDTO;

public class AdminUserForm extends OperationsEntityForm<AdminUserDTO> {

    private final static I18n i18n = I18n.get(AdminUserForm.class);

    private Map<CComponent<?, ?>, Condition> conditionalVisibilityMap;

    private Condition isSelfManagedUserCondition;

    private Condition isNewUserCondition;

    public AdminUserForm(IFormView<AdminUserDTO> view) {
        super(AdminUserDTO.class, view);

        conditionalVisibilityMap = new HashMap<CComponent<?, ?>, Condition>();

        isSelfManagedUserCondition = new Condition() {
            @Override
            public boolean isVisible() {
                return !ClientContext.getUserVisit().getPrincipalPrimaryKey().equals(getValue().getPrimaryKey());
            }
        };

        isNewUserCondition = new Condition() {
            @Override
            public boolean isVisible() {
                return getValue().id().isNull();
            }
        };
        createTabs();
    }

    public void createTabs() {
        FormFlexPanel content = new FormFlexPanel(i18n.tr("General"));
        int row = -1;
        content.setH1(++row, 0, 1, i18n.tr("General"));
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name())).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().email())).build());

        content.setH1(++row, 0, 1, i18n.tr("Security"));
        content.setWidget(++row, 0, new DecoratorBuilder(addVisibilityCondition(inject(proto().password()), isNewUserCondition)).build());
        content.setWidget(++row, 0, new DecoratorBuilder(addVisibilityCondition(inject(proto().passwordConfirm()), isNewUserCondition)).build());
        content.setWidget(++row, 0, new DecoratorBuilder(addVisibilityCondition(inject(proto().enabled()), isSelfManagedUserCondition)).build());
        content.setWidget(++row, 0, new DecoratorBuilder(addVisibilityCondition(inject(proto().role()), isSelfManagedUserCondition)).build());
        content.setWidget(++row, 0, new DecoratorBuilder(
                addVisibilityCondition(inject(proto().requiredPasswordChangeOnNextLogIn()), isSelfManagedUserCondition)).build());

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().credentialUpdated())).build());

        selectTab(addTab(content));
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        for (Entry<CComponent<?, ?>, Condition> entry : conditionalVisibilityMap.entrySet()) {
            entry.getKey().setVisible(entry.getValue().isVisible());
        }
    }

    private CComponent<?, ?> addVisibilityCondition(CComponent<?, ?> widget, Condition condition) {
        conditionalVisibilityMap.put(widget, condition);
        return widget;
    }

    private interface Condition {

        boolean isVisible();

    }
}
