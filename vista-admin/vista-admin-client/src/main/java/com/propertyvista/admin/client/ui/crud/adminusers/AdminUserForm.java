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
package com.propertyvista.admin.client.ui.crud.adminusers;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;

import com.propertyvista.admin.client.ui.crud.AdminEntityForm;
import com.propertyvista.admin.rpc.AdminUserDTO;

public class AdminUserForm extends AdminEntityForm<AdminUserDTO> {

    private final static I18n i18n = I18n.get(AdminUserForm.class);

    private Map<CComponent<?, ?>, Condition> conditionalVisibilityMap;

    private Condition isSelfManagedUserCondition;

    private Condition isNewUserCondition;

    public AdminUserForm(boolean viewMode) {
        super(AdminUserDTO.class, viewMode);

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

    }

    public AdminUserForm() {
        this(false);
    }

    @Override
    public void createTabs() {
        FormFlexPanel content = new FormFlexPanel();
        int row = -1;
        content.setH1(++row, 0, 1, i18n.tr("General"));
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name())).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().email())).build());

        content.setH1(++row, 0, 1, i18n.tr("Security"));
        content.setWidget(++row, 0, new DecoratorBuilder(addVisibilityCondition(inject(proto().password()), isNewUserCondition)).build());
        content.setWidget(++row, 0, new DecoratorBuilder(addVisibilityCondition(inject(proto().passwordConfirm()), isNewUserCondition)).build());
        content.setWidget(++row, 0, new DecoratorBuilder(addVisibilityCondition(inject(proto().enabled()), isSelfManagedUserCondition)).build());
        content.setWidget(++row, 0, new DecoratorBuilder(addVisibilityCondition(inject(proto().role()), isSelfManagedUserCondition)).build());
        content.setWidget(++row, 0,
                new DecoratorBuilder(addVisibilityCondition(inject(proto().requireChangePasswordOnNextLogIn()), isSelfManagedUserCondition)).build());

        selectTab(addTab(content, i18n.tr("General")));
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();
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
