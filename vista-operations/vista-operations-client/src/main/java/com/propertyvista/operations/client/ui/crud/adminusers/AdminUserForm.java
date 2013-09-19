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
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.rpc.dto.OperationsUserDTO;

public class AdminUserForm extends OperationsEntityForm<OperationsUserDTO> {

    private final static I18n i18n = I18n.get(AdminUserForm.class);

    private Map<CComponent<?>, Condition> conditionalVisibilityMap;

    private Condition isSelfManagedUserCondition;

    private Condition isNewUserCondition;

    public AdminUserForm(IForm<OperationsUserDTO> view) {
        super(OperationsUserDTO.class, view);

        conditionalVisibilityMap = new HashMap<CComponent<?>, Condition>();

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
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(i18n.tr("General"));
        int row = -1;
        content.setH1(++row, 0, 2, i18n.tr("General"));
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().name()), true).build());
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().email()), true).build());

        content.setH1(++row, 0, 2, i18n.tr("Security"));
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(addVisibilityCondition(inject(proto().password()), isNewUserCondition), true).build());
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(addVisibilityCondition(inject(proto().passwordConfirm()), isNewUserCondition), true).build());
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(addVisibilityCondition(inject(proto().enabled()), isSelfManagedUserCondition), true).build());
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(addVisibilityCondition(inject(proto().role()), isSelfManagedUserCondition), true).build());
        content.setWidget(++row, 0, 2,
                new FormDecoratorBuilder(addVisibilityCondition(inject(proto().requiredPasswordChangeOnNextLogIn()), isSelfManagedUserCondition), true).build());

        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().credentialUpdated()), true).build());

        setTabBarVisible(false);
        selectTab(addTab(content));
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        for (Entry<CComponent<?>, Condition> entry : conditionalVisibilityMap.entrySet()) {
            entry.getKey().setVisible(entry.getValue().isVisible());
        }
    }

    private CComponent<?> addVisibilityCondition(CComponent<?> widget, Condition condition) {
        conditionalVisibilityMap.put(widget, condition);
        return widget;
    }

    private interface Condition {

        boolean isVisible();

    }
}
