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
package com.propertyvista.portal.ptapp.client.ui;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Singleton;

import com.propertyvista.common.client.ui.decorations.DecorationUtils;
import com.propertyvista.common.client.ui.decorations.ViewLineSeparator;
import com.propertyvista.common.domain.financial.Money;
import com.propertyvista.portal.domain.ptapp.ChargeLine;
import com.propertyvista.portal.domain.ptapp.Charges;
import com.propertyvista.portal.ptapp.client.ui.components.BuildingPicture;
import com.propertyvista.portal.ptapp.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.portal.ptapp.client.ui.decorations.ViewHeaderDecorator;
import com.propertyvista.portal.rpc.ptapp.ChargesSharedCalculation;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CEditableComponent;

@Singleton
public class ChargesViewForm extends CEntityForm<Charges> {

    private final FlowPanel splitCharges = new FlowPanel();

    private boolean summaryViewMode = false;

    @SuppressWarnings("rawtypes")
    public ChargesViewForm() {
        super(Charges.class, new VistaEditorsComponentFactory());
        summaryViewMode = false;

        addValueChangeHandler(new ValueChangeHandler<Charges>() {
            @Override
            public void onValueChange(ValueChangeEvent event) {
                revalidate();
                if (isValid() && ChargesSharedCalculation.calculateCharges(getValue())) {
                    setValue(getValue());
                }
            }
        });
    }

    public ChargesViewForm(IEditableComponentFactory factory) {
        super(Charges.class, factory);
        summaryViewMode = true;
    }

    public boolean isSummaryViewMode() {
        return summaryViewMode;
    }

    @Override
    public IsWidget createContent() {
        FlowPanel main = new FlowPanel();

        main.add(new ViewHeaderDecorator(proto().monthlyCharges(), "700px"));
        main.add(inject(proto().monthlyCharges().charges()));
        if (!summaryViewMode) {
            main.add(createHeader2(proto().monthlyCharges().upgradeCharges()));
            main.add(inject(proto().monthlyCharges().upgradeCharges(), new ChargeLineSelectableFolder(summaryViewMode)));
        }

        main.add(createTotal(proto().monthlyCharges().total()));

        main.add(new ViewHeaderDecorator(proto().proRatedCharges(), "700px"));
        main.add(inject(proto().proRatedCharges().charges()));
        main.add(createTotal(proto().proRatedCharges().total()));

        main.add(new ViewHeaderDecorator(proto().applicationCharges(), "700px"));
        main.add(inject(proto().applicationCharges().charges()));
        main.add(createTotal(proto().applicationCharges().total()));

        // could be hided from resulting form:
        splitCharges.add(new ViewHeaderDecorator(proto().paymentSplitCharges(), "700px"));

        splitCharges.add(inject(proto().paymentSplitCharges().charges(), new ChargeSplitListFolder(summaryViewMode)));
        splitCharges.add(createTotal(proto().paymentSplitCharges().total()));
        main.add(splitCharges);

        main.setWidth("700px");

        if (isSummaryViewMode()) {
            return main;
        } else {
            // last step - add building picture on the right:
            HorizontalPanel content = new HorizontalPanel();
            content.add(main);
            content.add(new BuildingPicture());
            return content;
        }
    }

    @Override
    public void populate(Charges value) {
        super.populate(value);

        splitCharges.setVisible(value.paymentSplitCharges().charges().size() > 1);
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
        } else {
            return super.create(member);
        }
    }
}
