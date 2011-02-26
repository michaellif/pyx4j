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

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.propertyvista.portal.client.ptapp.ui.decorations.BoxReadOnlyFolderDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.BoxReadOnlyFolderItemDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewHeaderDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewLineSeparator;
import com.propertyvista.portal.domain.pt.ChargeLine;
import com.propertyvista.portal.domain.pt.ChargeLineList;
import com.propertyvista.portal.domain.pt.Charges;
import com.propertyvista.portal.rpc.pt.ChargesSharedCalculation;

import com.pyx4j.entity.client.ui.flex.CEntityEditableComponent;
import com.pyx4j.entity.client.ui.flex.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.CEntityFolderItem;
import com.pyx4j.entity.client.ui.flex.FolderDecorator;
import com.pyx4j.entity.client.ui.flex.FolderItemDecorator;
import com.pyx4j.forms.client.ui.CCheckBox;

public class ChargesViewFormV1 extends BaseEntityForm<Charges> {

    private final ValueChangeHandler<Boolean> valueChangeHandler;

    public ChargesViewFormV1() {
        super(Charges.class);
        valueChangeHandler = new ValueChangeHandler<Boolean>() {

            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                ChargesSharedCalculation.calculateCharges(getValue());
                setValue(getValue());
            }
        };
    }

    @Override
    public void createContent() {
        FlowPanel main = new FlowPanel();
        main.add(bind(new ChargeLineListEditor(false), proto().rentCharges()));
        main.add(bind(new ChargeLineListEditor(true), proto().upgradeCharges()));
        main.add(bind(new ChargeLineListEditor(false), proto().proRatedCharges()));
        main.add(bind(new ChargeLineListEditor(false), proto().applicationCharges()));

        //TODO
        //main.add(create(proto().paymentSplitCharges(), this));

        setWidget(main);
    }

    private class ChargeLineListEditor extends CEntityEditableComponent<ChargeLineList> {

        private final boolean selectedable;

        private HTML headerHTML;

        public ChargeLineListEditor(final boolean selectedable) {
            super(ChargeLineList.class);
            this.selectedable = selectedable;
        }

        @Override
        public void createContent() {
            FlowPanel main = new FlowPanel();
            main.add(new ViewHeaderDecorator(headerHTML = new HTML()));
            main.add(bind(createChargeLineFolderEditor(selectedable), proto().charges()));

            Widget sp = new ViewLineSeparator(70, Unit.PCT, 0.5, Unit.EM, 0.5, Unit.EM);
            sp.getElement().getStyle().setPadding(0, Unit.EM);
            sp.getElement().getStyle().setProperty("border", "1px dotted black");
            main.add(sp);

            FlowPanel totalRow = new FlowPanel();
            HTML total = new HTML("<b>" + proto().total().getMeta().getCaption() + "</b>");
            totalRow.add(inline(total, "60%", null));
            totalRow.add(inline(create(proto().total(), this), "10%", "right"));
            totalRow.getElement().getStyle().setPaddingLeft(1, Unit.EM);
            main.add(totalRow);

            setWidget(main);
        }

        @Override
        public void populate(ChargeLineList value) {
            if (value != null) {
                headerHTML.setHTML("<h4>" + value.getMeta().getCaption() + "</h4>");
            }
            super.populate(value);
        }
    }

    private CEntityFolder<ChargeLine> createChargeLineFolderEditor(final boolean selectedable) {

        return new CEntityFolder<ChargeLine>() {

            @Override
            protected FolderDecorator<ChargeLine> createFolderDecorator() {
                return new BoxReadOnlyFolderDecorator<ChargeLine>();
            }

            @Override
            protected CEntityFolderItem<ChargeLine> createItem() {
                return new ChargeLineFolderItem(selectedable);
            }

        };
    }

    class ChargeLineFolderItem extends CEntityFolderItem<ChargeLine> {

        private final boolean selectedable;

        public ChargeLineFolderItem(final boolean selectedable) {
            super(ChargeLine.class);
            this.selectedable = selectedable;
        }

        @Override
        public FolderItemDecorator createFolderItemDecorator() {
            return new BoxReadOnlyFolderItemDecorator(!isFirst());
        }

        @Override
        public void createContent() {
            FlowPanel main = new FlowPanel();
            if (selectedable) {
                CCheckBox cb = (CCheckBox) create(proto().selected(), this);
                cb.addValueChangeHandler(valueChangeHandler);
                //TODO this is hack for Misha to fix.
                cb.asWidget().setStyleName(null);
                main.add(inline(cb, "60%", null));
            } else {
                main.add(inline(create(proto().type(), this), "60%", null));
            }
            main.add(inline(create(proto().charge(), this), "10%", "right"));
            setWidget(main);
        }

        @Override
        public void populate(ChargeLine value) {
            if ((selectedable) && (value != null)) {
                CCheckBox cb = (CCheckBox) this.get(proto().selected());
                cb.asWidget().setText(value.type().getValue().toString());
            }
            super.populate(value);
        }

    }

    private IsWidget inline(IsWidget w, String width, String textAlign) {
        Widget wg = w.asWidget();
        wg.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        if (textAlign != null) {
            wg.getElement().getStyle().setProperty("textAlign", textAlign);
        }
        wg.setWidth(width);
        return w;
    }
}
