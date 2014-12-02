/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 7, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.scheduler.trigger;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CDatePicker;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.VistaBoxFolderItemDecorator;
import com.propertyvista.operations.domain.scheduler.ScheduleType;
import com.propertyvista.operations.domain.scheduler.TriggerSchedule;

public class TriggerScheduleFolder extends VistaBoxFolder<TriggerSchedule> {

    private static final I18n i18n = I18n.get(TriggerScheduleFolder.class);

    public TriggerScheduleFolder(boolean modifyable) {
        super(TriggerSchedule.class, modifyable);
    }

    @Override
    protected CForm<TriggerSchedule> createItemForm(IObject<?> member) {
        return new TriggerScheduleEditor();
    }

    private class TriggerScheduleEditor extends CForm<TriggerSchedule> {

        public TriggerScheduleEditor() {
            super(TriggerSchedule.class);
        }

        @Override
        protected IsWidget createContent() {
            FormPanel formPanel = new FormPanel(this);

            formPanel.append(Location.Left, proto().repeatType()).decorate().componentWidth(144);
            formPanel.append(Location.Right, proto().nextFireTime()).decorate().componentWidth(144);

            formPanel.append(Location.Left, proto().repeatEvery()).decorate().componentWidth(144);
            formPanel.append(Location.Left, proto().time()).decorate().componentWidth(144);

            formPanel.append(Location.Left, proto().startsOn()).decorate().componentWidth(144);
            formPanel.append(Location.Right, proto().endsOn()).decorate().componentWidth(144);

            // fill ScheduleType types:
            if (get(proto().repeatType()) instanceof CComboBox) {
                ((CComboBox<ScheduleType>) get(proto().repeatType())).setOptions(ScheduleType.uiSet());
            }

            return formPanel;
        }

        @Override
        public void addValidations() {
            get(proto().repeatType()).addValueChangeHandler(new ValueChangeHandler<ScheduleType>() {
                @Override
                public void onValueChange(ValueChangeEvent<ScheduleType> event) {
                    updateVisibility();
                    get(proto().nextFireTime()).setValue(null);
                }
            });
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);
            updateVisibility();

            @SuppressWarnings("rawtypes")
            CComponent starts = get(proto().startsOn());
            ((CDatePicker) starts).setPastDateSelectionAllowed(getValue().getPrimaryKey() != null);
        }

        private void updateVisibility() {
            ScheduleType type = get(proto().repeatType()).getValue();
            boolean enabled = (type != ScheduleType.Manual);
            get(proto().time()).setEnabled(enabled);
            get(proto().startsOn()).setEnabled(enabled);

            boolean repeat = enabled && (type != ScheduleType.Once);
            get(proto().repeatEvery()).setEnabled(repeat);
            get(proto().endsOn()).setEnabled(repeat);
        }
    }

    @Override
    public VistaBoxFolderItemDecorator<TriggerSchedule> createItemDecorator() {
        VistaBoxFolderItemDecorator<TriggerSchedule> decor = super.createItemDecorator();
        decor.setExpended(false);
        return decor;
    }

    @Override
    protected void createNewEntity(AsyncCallback<TriggerSchedule> callback) {
        TriggerSchedule newEntity = EntityFactory.create(TriggerSchedule.class);

        newEntity.repeatType().setValue(ScheduleType.Weekly);
        newEntity.repeatEvery().setValue(1);
        newEntity.startsOn().setValue(new LogicalDate());

        callback.onSuccess(newEntity);
    }

}