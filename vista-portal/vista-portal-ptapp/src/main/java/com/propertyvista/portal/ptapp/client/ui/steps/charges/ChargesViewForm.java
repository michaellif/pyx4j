/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui.steps.charges;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.common.client.ui.components.editors.CEntityDecoratableEditor;
import com.propertyvista.common.client.ui.components.folders.ChargeLineFolder;
import com.propertyvista.common.client.ui.decorations.DecorationUtils;
import com.propertyvista.common.client.ui.decorations.VistaLineSeparator;
import com.propertyvista.domain.financial.Money;
import com.propertyvista.portal.domain.ptapp.Charges;
import com.propertyvista.portal.ptapp.client.ui.components.BuildingPicture;
import com.propertyvista.portal.rpc.ptapp.ChargesSharedCalculation;

public class ChargesViewForm extends CEntityDecoratableEditor<Charges> {

    private final FormFlexPanel splitCharges = new FormFlexPanel();

    public ChargesViewForm() {
        this(new VistaEditorsComponentFactory());
    }

    public ChargesViewForm(IEditableComponentFactory factory) {
        super(Charges.class, factory);

        addValueChangeHandler(new ValueChangeHandler<Charges>() {
            @Override
            public void onValueChange(ValueChangeEvent<Charges> event) {
                revalidate();
                if (isValid() && ChargesSharedCalculation.calculateCharges(getValue())) {
                    setValue(getValue());
                }
            }
        });
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setH1(++row, 0, 1, proto().monthlyCharges().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().monthlyCharges().charges(), new ChargeLineFolder(isEditable())));
        main.setWidget(++row, 0, createTotal(proto().monthlyCharges().total()));

        main.setH1(++row, 0, 1, proto().oneTimeCharges().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().oneTimeCharges().charges(), new ChargeLineFolder(isEditable())));
        main.setWidget(++row, 0, createTotal(proto().oneTimeCharges().total()));

        main.setH1(++row, 0, 1, proto().proratedCharges().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().proratedCharges().charges(), new ChargeLineFolder(isEditable())));
        main.setWidget(++row, 0, createTotal(proto().proratedCharges().total()));

        main.setH1(++row, 0, 1, proto().applicationCharges().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().applicationCharges().charges(), new ChargeLineFolder(isEditable())));
        main.setWidget(++row, 0, createTotal(proto().applicationCharges().total()));

        int row1 = -1;
        splitCharges.setH1(++row1, 0, 1, proto().paymentSplitCharges().getMeta().getCaption());
        splitCharges.setWidget(++row1, 0, inject(proto().paymentSplitCharges().charges(), new ChargeSplitListFolder(isEditable())));
        splitCharges.setWidget(++row1, 0, createTotal(proto().paymentSplitCharges().total()));
        main.setWidget(++row, 0, splitCharges);

        main.setWidth("600px");

        // last step - add building picture on the right:
        HorizontalPanel content = new HorizontalPanel();
        content.add(main);
        content.add(new BuildingPicture());
        content.setWidth("800px");
        return content;
    }

    @Override
    public void populate(Charges value) {
        super.populate(value);
//        splitCharges.setVisible(value.paymentSplitCharges().charges().size() > 1);
    }

    private Widget createTotal(Money member) {
        FlowPanel totalRow = new FlowPanel();

        Widget sp = new VistaLineSeparator(36, Unit.EM);
        sp.getElement().getStyle().setPadding(0, Unit.EM);
        sp.getElement().getStyle().setProperty("border", "1px dotted black");
        totalRow.add(sp);

        HTML total = new HTML("<b>" + member.getMeta().getCaption() + "</b>");
        total.getElement().getStyle().setPaddingLeft(0.7, Unit.EM);
        totalRow.add(DecorationUtils.inline(total, "29.3em", null));
        totalRow.add(DecorationUtils.inline(inject(member), "5em", "right"));
        get(member).asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLD);

        return totalRow;
    }
}
