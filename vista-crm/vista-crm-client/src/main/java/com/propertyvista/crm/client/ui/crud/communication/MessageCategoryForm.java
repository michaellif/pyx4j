/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.communication;

import java.util.EnumSet;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.Key;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IFormView;

import com.propertyvista.crm.client.ui.components.CrmRoleFolder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.organisation.employee.EmployeeFolder;
import com.propertyvista.crm.client.ui.crud.organisation.employee.EmployeeFolder.ParentEmployeeGetter;
import com.propertyvista.domain.communication.MessageCategory;
import com.propertyvista.domain.communication.MessageCategory.CategoryType;
import com.propertyvista.misc.VistaTODO;

public class MessageCategoryForm extends CrmEntityForm<MessageCategory> {

    private static final I18n i18n = I18n.get(MessageCategoryForm.class);

    private final IsWidget mainTab;

    private EmployeeFolder dispatcherFolder;

    private Widget dispatcherHeader;

    public MessageCategoryForm(IFormView<MessageCategory, ?> view) {
        super(MessageCategory.class, view);

        mainTab = createInfoTab();
        setTabBarVisible(false);
        selectTab(addTab(mainTab, i18n.tr("Message Category Properties")));

    }

    private IsWidget createInfoTab() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Left, proto().category()).decorate().customLabel(i18n.tr("Name"));
        if (ApplicationMode.isDevelopment() && VistaTODO.ADDITIONAL_COMMUNICATION_FEATURES) {
            CComboBox<CategoryType> categoryComp = new CComboBox<CategoryType>();
            categoryComp.setOptions(EnumSet.of(CategoryType.Message, CategoryType.IVR, CategoryType.Notification, CategoryType.SMS));

            formPanel.append(Location.Left, proto().categoryType(), categoryComp).decorate().customLabel(i18n.tr("Type"));
        } else {
            formPanel.append(Location.Left, proto().categoryType(), new CLabel<CategoryType>()).decorate().customLabel(i18n.tr("Type"));
        }
        dispatcherHeader = formPanel.h1(i18n.tr("Message Category Dispatchers"));
        formPanel.append(Location.Left, proto().dispatchers(), dispatcherFolder = new EmployeeFolder(this, new ParentEmployeeGetter() {
            @Override
            public Key getParentId() {
                return null;
            }
        }, true));

        get(proto().categoryType()).addValueChangeHandler(new ValueChangeHandler<CategoryType>() {
            @Override
            public void onValueChange(ValueChangeEvent<CategoryType> event) {
                setDispatchersVisability(event.getValue());
            }
        });

        formPanel.h1(i18n.tr("User Roles allowed to see the category messages"));
        formPanel.append(Location.Left, proto().roles(), new CrmRoleFolder(this));
        return formPanel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        MessageCategory se = getValue();
        if (se == null) {
            return;
        }

        setDispatchersVisability(se.categoryType().getValue());
    }

    @Override
    public boolean isValid() {
        if (getValue() != null && !getValue().isNull() && !getValue().isEmpty() && !getValue().isPrototype()) {
            if (CategoryType.Ticket.equals(getValue().categoryType().getValue())) {
                if ((getValue().dispatchers() == null || getValue().dispatchers().isNull() || getValue().dispatchers().isEmpty() || getValue().dispatchers()
                        .size() < 1)
                        && (getValue().roles() == null || getValue().roles().isNull() || getValue().roles().isEmpty() || getValue().roles().size() < 1)) {
                    validationErrors.add(new BasicValidationError(this, i18n.tr("Please specify at least one dispatcher or role.")));
                }
            } else {
                if (getValue().roles() == null || getValue().roles().isNull() || getValue().roles().isEmpty() || getValue().roles().size() < 1) {
                    validationErrors.add(new BasicValidationError(this, i18n.tr("Please specify at least one role.")));
                }
            }
        }
        return super.isValid();
    }

    private void setDispatchersVisability(CategoryType value) {
        boolean showDispatchers = CategoryType.Ticket.equals(value);
        dispatcherFolder.setVisible(showDispatchers);
        dispatcherHeader.setVisible(showDispatchers);
    }
}
