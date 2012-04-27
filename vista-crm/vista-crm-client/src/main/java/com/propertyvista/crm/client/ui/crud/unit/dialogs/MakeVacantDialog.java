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

import java.util.Date;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CDatePicker;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationFailure;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.crm.client.ui.crud.unit.UnitViewerView;
import com.propertyvista.crm.client.ui.crud.unit.UnitViewerView.Presenter;

public class MakeVacantDialog extends OkCancelDialog {

    private final static I18n i18n = I18n.get(MakeVacantDialog.class);

    private CDatePicker startDate;

    private final Presenter presenter;

    private CEntityDecoratableEditor<MakeVacantDTO> form;

    public MakeVacantDialog(UnitViewerView.Presenter presenter, final LogicalDate minMakeVacantStartDay, final LogicalDate maxMakeVacantStartDay) {
        super(i18n.tr("Make Vacant..."));
        this.presenter = presenter;
        this.form = new CEntityDecoratableEditor<MakeVacantDTO>(MakeVacantDTO.class) {

            @Override
            public IsWidget createContent() {
                FormFlexPanel content = new FormFlexPanel();
                content.setWidget(0, 0, new DecoratorBuilder(inject(proto().vacantStartDay()), 9).build());

                if (minMakeVacantStartDay.equals(maxMakeVacantStartDay)) {
                    get(proto().vacantStartDay()).setViewable(true);
                } else {
                    EditableValueValidator<Date> validator = null;
                    if (maxMakeVacantStartDay == null) {
                        validator = new EditableValueValidator<Date>() {
                            @Override
                            public ValidationFailure isValid(CComponent<Date, ?> component, Date value) {
                                if (value == null || value.before(minMakeVacantStartDay)) {
                                    return new ValidationFailure(i18n.tr("please enter a date greater or equal to {0}", minMakeVacantStartDay));
                                } else {
                                    return null;
                                }
                            }
                        };
                    } else {
                        validator = new EditableValueValidator<Date>() {
                            @Override
                            public ValidationFailure isValid(CComponent<Date, ?> component, Date value) {
                                if (value == null || (value.before(minMakeVacantStartDay) | value.after(maxMakeVacantStartDay))) {
                                    return new ValidationFailure(i18n.tr("please enter a between {0} and {1}", minMakeVacantStartDay, maxMakeVacantStartDay));
                                } else {
                                    return null;
                                }
                            }
                        };
                    }
                    get(proto().vacantStartDay()).addValueValidator(validator);
                }

                return content;
            }
        };
        form.initContent();
        MakeVacantDTO defaultValue = EntityFactory.create(MakeVacantDTO.class);
        defaultValue.vacantStartDay().setValue(minMakeVacantStartDay);
        form.populate(defaultValue);

        setBody(form);
    }

    protected LogicalDate getStartingDate() {
        return new LogicalDate(startDate.getValue());
    }

    @Override
    public boolean onClickOk() {
        if (form.isValid()) {
            presenter.makeVacant(form.getValue().vacantStartDay().getValue());
            return true;
        } else {
            return false;
        }
    }
}
