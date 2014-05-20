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
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.crm.client.ui.crud.unit.UnitViewerView.Presenter;
import com.propertyvista.crm.client.ui.crud.unit.dialogs.ScopingResultDTO.ScopingResult;

public class ScopeDialog extends OkCancelDialog {

    private final static I18n i18n = I18n.get(ScopeDialog.class);

    private CForm<ScopingResultDTO> form;

    private final Presenter presenter;

    public ScopeDialog(Presenter presenter, final boolean canScopeAvailable, final boolean canScopeOffMarket, final LogicalDate minRenoEndDay) {
        super(i18n.tr("Scoping"));
        this.presenter = presenter;

        form = new CForm<ScopingResultDTO>(ScopingResultDTO.class) {
            @Override
            protected IsWidget createContent() {
                FormPanel formPanel = new FormPanel(this);

                formPanel.append(Location.Left, proto().scopingResult()).decorate();
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

                formPanel.append(Location.Left, proto().renovationEndsOn()).decorate();
                get(proto().renovationEndsOn()).setVisible(false);
                get(proto().renovationEndsOn()).addComponentValidator(new AbstractComponentValidator<LogicalDate>() {
                    @Override
                    public BasicValidationError isValid() {
                        if (getComponent().getValue().before(minRenoEndDay)) {
                            return new BasicValidationError(getComponent(), i18n.tr("The minimal acceptable renovation date is {0}", minRenoEndDay));
                        } else {
                            return null;
                        }
                    }
                });
                formPanel.append(Location.Left, proto().offMarketType()).decorate();
                get(proto().offMarketType()).setVisible(false);

                return formPanel;
            }
        };
        form.init();
        form.populateNew();
        form.asWidget().setSize("100%", "100%");
        setBody(form);
    }

    protected ScopingResultDTO getResult() {
        return form.getValue();
    }

    @Override
    public boolean onClickOk() {
        form.setVisitedRecursive();
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
