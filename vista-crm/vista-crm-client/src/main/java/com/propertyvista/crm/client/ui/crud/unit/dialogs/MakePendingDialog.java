/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 16, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.unit.dialogs;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CDatePicker;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.unit.UnitViewerView;
import com.propertyvista.crm.client.ui.crud.unit.UnitViewerView.Presenter;

public class MakePendingDialog extends OkCancelDialog {

    private final static I18n i18n = I18n.get(MakePendingDialog.class);

    private CDatePicker startDate;

    private final Presenter presenter;

    private CEntityForm<MakePendingDTO> form;

    public MakePendingDialog(UnitViewerView.Presenter presenter, final LogicalDate minMakeVacantStartDay, final LogicalDate maxMakeVacantStartDay) {
        super(i18n.tr("Make Pending..."));
        this.presenter = presenter;
        this.form = new CEntityForm<MakePendingDTO>(MakePendingDTO.class) {

            @Override
            public IsWidget createContent() {
                TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();
                content.setWidget(0, 0, new FormDecoratorBuilder(inject(proto().pendingStartDay()), 9).build());

                if (minMakeVacantStartDay.equals(maxMakeVacantStartDay)) {
                    get(proto().pendingStartDay()).setViewable(true);
                } else {
                    EditableValueValidator<LogicalDate> validator = null;
                    if (maxMakeVacantStartDay == null) {
                        validator = new EditableValueValidator<LogicalDate>() {
                            @Override
                            public ValidationError isValid(CComponent<LogicalDate> component, LogicalDate value) {
                                if (value == null || value.before(minMakeVacantStartDay)) {
                                    return new ValidationError(component, i18n.tr("please enter a date greater or equal to {0,date,short}",
                                            minMakeVacantStartDay));
                                } else {
                                    return null;
                                }
                            }
                        };
                    } else {
                        validator = new EditableValueValidator<LogicalDate>() {
                            @Override
                            public ValidationError isValid(CComponent<LogicalDate> component, LogicalDate value) {
                                if (value == null || (value.before(minMakeVacantStartDay) | value.after(maxMakeVacantStartDay))) {
                                    return new ValidationError(component, i18n.tr("please enter a date between {0,date,short} and {1,date,short}",
                                            minMakeVacantStartDay, maxMakeVacantStartDay));
                                } else {
                                    return null;
                                }
                            }
                        };
                    }
                    get(proto().pendingStartDay()).addValueValidator(validator);
                }

                return content;
            }
        };
        form.initContent();
        MakePendingDTO defaultValue = EntityFactory.create(MakePendingDTO.class);
        defaultValue.pendingStartDay().setValue(minMakeVacantStartDay);
        form.populate(defaultValue);

        setBody(form);
    }

    protected LogicalDate getStartingDate() {
        return new LogicalDate(startDate.getValue());
    }

    @Override
    public boolean onClickOk() {
        if (form.isValid()) {
            presenter.makeVacant(form.getValue().pendingStartDay().getValue());
            return true;
        } else {
            return false;
        }
    }
}
