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

import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.crm.client.ui.crud.unit.UnitViewerView.Presenter;
import com.propertyvista.crm.client.ui.crud.unit.dialogs.ScopingResultDTO.ScopingResult;

public class ScopeDialog extends OkCancelDialog {

    private final static I18n i18n = I18n.get(ScopeDialog.class);

    private CEntityDecoratableEditor<ScopingResultDTO> form;

    private Presenter presenter;

    public ScopeDialog(Presenter presenter) {
        super(i18n.tr("Scoping Result"));
        this.presenter = presenter;

        form = new CEntityDecoratableEditor<ScopingResultDTO>(ScopingResultDTO.class) {
            @Override
            public IsWidget createContent() {
                FormFlexPanel content = new FormFlexPanel();
                int row = -1;
                content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().scopingResult())).build());
                get(proto().scopingResult()).addValueChangeHandler(new ValueChangeHandler<ScopingResultDTO.ScopingResult>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<ScopingResult> event) {
                        get(proto().renovationEndsOn()).setVisible(event.getValue() == ScopingResult.renovation);

                        get(proto().offMarketType()).setVisible(event.getValue() == ScopingResult.offMarket);
                    }
                });
                CComboBox<ScopingResult> combo = (CComboBox<ScopingResult>) get(proto().scopingResult());
                ArrayList<ScopingResult> options = new ArrayList<ScopingResultDTO.ScopingResult>();
//                if (opScopeAvailable != null) {
//                    options.add(ScopingResult.available);
//                }
//                if (opScopeRenovation != null) {
//                    options.add(ScopingResult.renovation);
//                }
//                if (opScopeOffMarket != null) {
//                    options.add(ScopingResult.offMarket);
//                }
                combo.setOptions(options);

                content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().renovationEndsOn())).build());
                get(proto().renovationEndsOn()).setVisible(false);
                content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().offMarketType())).build());
                get(proto().offMarketType()).setVisible(false);

                return content;
            }
        };
        form.initContent();
        form.asWidget().setSize("100%", "100%");
        setBody(form);
    }

    protected ScopingResultDTO getResult() {
        return form.getValue();
    }

    @Override
    public boolean onClickOk() {
        if (!form.isValid()) {
            return false;
        } else {
            return true;
        }
    }
}
