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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.common.client.ui.components.c.CMoneyLabel;
import com.propertyvista.common.client.ui.components.folders.ChargeLineFolder;
import com.propertyvista.common.client.ui.decorations.DecorationUtils;
import com.propertyvista.common.client.ui.decorations.VistaLineSeparator;
import com.propertyvista.domain.charges.ChargeLineList;
import com.propertyvista.domain.financial.Money;
import com.propertyvista.portal.domain.ptapp.Charges;
import com.propertyvista.portal.rpc.ptapp.ChargesSharedCalculation;

public class ChargesViewForm extends CEntityDecoratableEditor<Charges> {

    private static final Logger log = LoggerFactory.getLogger(ChargesViewForm.class);

    private final FormFlexPanel splitCharges = new FormFlexPanel();

    public ChargesViewForm() {
        this(new VistaEditorsComponentFactory());
    }

    public ChargesViewForm(IEditableComponentFactory factory) {
        super(Charges.class, factory);
        setEditable(factory instanceof VistaEditorsComponentFactory);

        addValueChangeHandler(new ValueChangeHandler<Charges>() {
            @Override
            public void onValueChange(ValueChangeEvent<Charges> event) {
                revalidate();
                log.trace("calculateCharges");
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
        main.setWidget(++row, 0, inject(proto().monthlyCharges(), new ChargesSubCategoryViewForm(factory, proto().monthlyCharges().getMeta().getCaption())));
        main.setWidget(++row, 0, inject(proto().oneTimeCharges(), new ChargesSubCategoryViewForm(factory, proto().oneTimeCharges().getMeta().getCaption())));
        main.setWidget(++row, 0, inject(proto().proratedCharges(), new ChargesSubCategoryViewForm(factory, proto().proratedCharges().getMeta().getCaption())));
        main.setWidget(++row, 0,
                inject(proto().applicationCharges(), new ChargesSubCategoryViewForm(factory, proto().applicationCharges().getMeta().getCaption())));

        int row1 = -1;
        splitCharges.setH1(++row1, 0, 1, proto().paymentSplitCharges().getMeta().getCaption());
        splitCharges.setWidget(++row1, 0, inject(proto().paymentSplitCharges().charges(), new ChargeSplitListFolder(isEditable())));
        splitCharges.setWidget(++row1, 0, createTotal(this, proto().paymentSplitCharges().total()));
        main.setWidget(++row, 0, splitCharges);

        return main;
    }

    @Override
    public void populate(Charges value) {
        super.populate(value);
        splitCharges.setVisible(value.paymentSplitCharges().charges().size() > 1);
    }

    private static Widget createTotal(CEntityEditor<?> form, Money member) {
        FlowPanel totalRow = new FlowPanel();

        Widget sp = new VistaLineSeparator(38, Unit.EM);
        sp.getElement().getStyle().setPadding(0, Unit.EM);
        sp.getElement().getStyle().setProperty("border", "1px dotted black");
        totalRow.add(sp);
        HTML total = new HTML("<b>" + member.getMeta().getCaption() + "</b>");
        total.getElement().getStyle().setPaddingLeft(0.7, Unit.EM);
        totalRow.add(DecorationUtils.inline(total, "30.5em", null));
        totalRow.add(DecorationUtils.inline(form.inject(member, new CMoneyLabel()), "7em"));
        form.get(member).asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLD);
        return totalRow;
    }

    /**
     * Self charges view form for sub category that renders itself to invisible sate when no charges are available.
     * 
     * @author ArtyomB
     * 
     */
    private static class ChargesSubCategoryViewForm extends CEntityDecoratableEditor<ChargeLineList> {
        private final String caption;

        public ChargesSubCategoryViewForm(IEditableComponentFactory factory, String caption) {
            super(ChargeLineList.class, factory);
            setEditable(factory instanceof VistaEditorsComponentFactory);
            this.caption = caption;
        }

        @Override
        public IsWidget createContent() {
            FormFlexPanel content = new FormFlexPanel();
            int row = -1;
            content.setH1(++row, 0, 1, caption);
            content.setWidget(++row, 0, inject(proto().charges(), new ChargeLineFolder(isEditable())));
            content.setWidget(++row, 0, createTotal(this, proto().total()));

            return content;
        }

        @Override
        public void populate(ChargeLineList entity) {
            super.populate(entity);
            this.asWidget().setVisible(!entity.charges().isEmpty());
        }
    }
}
