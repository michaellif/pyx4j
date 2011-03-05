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
package com.propertyvista.portal.client.ptapp.ui;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Singleton;
import com.propertyvista.portal.client.ptapp.ui.decorations.DecorationUtils;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewHeaderDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewLineSeparator;
import com.propertyvista.portal.domain.Money;
import com.propertyvista.portal.domain.pt.ChargeLine;
import com.propertyvista.portal.domain.pt.ChargeLineSelectable;
import com.propertyvista.portal.domain.pt.Charges;
import com.propertyvista.portal.domain.pt.TenantCharge;
import com.propertyvista.portal.rpc.pt.ChargesSharedCalculation;

import com.pyx4j.entity.client.ui.EditableComponentFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CEditableComponent;

@Singleton
public class ChargesViewForm extends BaseEntityForm<Charges> {

    @SuppressWarnings("rawtypes")
    private final ValueChangeHandler valueChangeHandler;

    @SuppressWarnings("rawtypes")
    public ChargesViewForm() {
        super(Charges.class);

        valueChangeHandler = new ValueChangeHandler() {

            @Override
            public void onValueChange(ValueChangeEvent event) {
                if (isValid()) {
                    ChargesSharedCalculation.calculateCharges(getValue());
                    setValue(getValue());
                }
            }
        };

    }

    public ChargesViewForm(EditableComponentFactory factory) {
        super(Charges.class, factory);

        valueChangeHandler = null;
    }

    @Override
    public IsWidget createContent() {
        FlowPanel main = new FlowPanel();

        main.add(new ViewHeaderDecorator(proto().monthlyCharges()));
        main.add(inject(proto().monthlyCharges().charges()));
        if (valueChangeHandler != null) {
            main.add(createHeader2(proto().monthlyCharges().upgradeCharges()));
            main.add(inject(proto().monthlyCharges().upgradeCharges()));
        }

        main.add(createTotal(proto().monthlyCharges().total()));

        main.add(new ViewHeaderDecorator(proto().proRatedCharges()));
        main.add(inject(proto().proRatedCharges().charges()));
        main.add(createTotal(proto().proRatedCharges().total()));

        main.add(new ViewHeaderDecorator(proto().applicationCharges()));
        main.add(inject(proto().applicationCharges().charges()));
        main.add(createTotal(proto().applicationCharges().total()));

        main.add(new ViewHeaderDecorator(proto().paymentSplitCharges()));
        main.add(inject(proto().paymentSplitCharges().charges()));
        main.add(createTotal(proto().paymentSplitCharges().total()));

        main.setWidth("700px");

        return main;
    }

    private Widget createHeader2(IObject<?> member) {

        HTML h = new HTML("<h5>" + member.getMeta().getCaption() + "</h5>");
        h.getElement().getStyle().setMarginTop(0.5, Unit.EM);
        h.getElement().getStyle().setMarginLeft(1, Unit.EM);
        return h;
    }

    private Widget createTotal(Money member) {
        FlowPanel totalRow = new FlowPanel();

        Widget sp = new ViewLineSeparator(400, Unit.PX, 0.5, Unit.EM, 0.5, Unit.EM);
        sp.getElement().getStyle().setPadding(0, Unit.EM);
        sp.getElement().getStyle().setProperty("border", "1px dotted black");
        totalRow.add(sp);

        HTML total = new HTML("<b>" + member.getMeta().getCaption() + "</b>");
        totalRow.add(DecorationUtils.inline(total, "300px", null));
        totalRow.add(DecorationUtils.inline(inject(member), "100px", "right"));
        totalRow.getElement().getStyle().setPaddingLeft(1, Unit.EM);
        return totalRow;
    }

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        if (member.getValueClass().equals(ChargeLine.class)) {
            return new ChargeLineFolder();
        } else if (member.getValueClass().equals(ChargeLineSelectable.class)) {
            return new ChargeLineSelectableFolder(valueChangeHandler);
        } else if (member.getValueClass().equals(TenantCharge.class)) {
            return new ChargeSplitListFolder(valueChangeHandler);
        } else {
            return super.create(member);
        }
    }
}
