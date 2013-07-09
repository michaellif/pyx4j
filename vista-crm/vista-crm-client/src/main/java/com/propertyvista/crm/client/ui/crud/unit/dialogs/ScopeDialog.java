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

import java.util.ArrayList;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.crm.client.ui.crud.unit.UnitViewerView.Presenter;
import com.propertyvista.crm.client.ui.crud.unit.dialogs.ScopingResultDTO.ScopingResult;

public class ScopeDialog extends OkCancelDialog {

    private final static I18n i18n = I18n.get(ScopeDialog.class);

    private CEntityDecoratableForm<ScopingResultDTO> form;

    private final Presenter presenter;

    public ScopeDialog(Presenter presenter, final boolean canScopeAvailable, final boolean canScopeOffMarket, final LogicalDate minRenoEndDay) {
        super(i18n.tr("Scoping"));
        this.presenter = presenter;

        form = new CEntityDecoratableForm<ScopingResultDTO>(ScopingResultDTO.class) {
            @Override
            public IsWidget createContent() {
                FormFlexPanel content = new FormFlexPanel();
                int row = -1;
                content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().scopingResult()), 10).build());
                get(proto().scopingResult()).addValueChangeHandler(new ValueChangeHandler<ScopingResultDTO.ScopingResult>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<ScopingResult> event) {
                        get(proto().renovationEndsOn()).setVisible(event.getValue() == ScopingResult.renovation);

                        get(proto().offMarketType()).setVisible(event.getValue() == ScopingResult.offMarket);
                    }
                });
                CComboBox<ScopingResult> combo = (CComboBox<ScopingResult>) get(proto().scopingResult());
                ArrayList<ScopingResult> options = new ArrayList<ScopingResultDTO.ScopingResult>();
                if (canScopeAvailable) {
                    options.add(ScopingResult.available);
                }
                if (canScopeOffMarket) {
                    options.add(ScopingResult.offMarket);
                }
                if (minRenoEndDay != null) {
                    options.add(ScopingResult.renovation);
                }
                combo.setOptions(options);

                content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().renovationEndsOn()), 10).build());
                get(proto().renovationEndsOn()).setVisible(false);
                get(proto().renovationEndsOn()).addValueValidator(new EditableValueValidator<LogicalDate>() {
                    @Override
                    public ValidationError isValid(CComponent<LogicalDate> component, LogicalDate value) {
                        if (value.before(minRenoEndDay)) {
                            return new ValidationError(component, i18n.tr("The minimal acceptable renovation date is {0}", minRenoEndDay));
                        } else {
                            return null;
                        }
                    }
                });
                content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().offMarketType()), 10).build());
                get(proto().offMarketType()).setVisible(false);

                return content;
            }
        };
        form.initContent();
        form.populateNew();
        form.asWidget().setSize("100%", "100%");
        setBody(form);
    }

    protected ScopingResultDTO getResult() {
        return form.getValue();
    }

    @Override
    public boolean onClickOk() {
        form.setUnconditionalValidationErrorRendering(true);
        if (!form.isValid()) {
            return false;
        } else {
            switch (form.getValue().scopingResult().getValue()) {
            case available:
                presenter.scopeAvailable();
                break;
            case offMarket:
                presenter.scopeOffMarket(form.getValue().offMarketType().getValue());
                break;
            case renovation:
                presenter.scopeRenovation(form.getValue().renovationEndsOn().getValue());
                break;
            }
            return true;
        }
    }
}
