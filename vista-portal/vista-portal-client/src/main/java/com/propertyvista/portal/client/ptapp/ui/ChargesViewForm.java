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
 * @version $Id: VistaTesterDispatcher.java 32 2011-02-02 04:49:39Z vlads $
 */
package com.propertyvista.portal.client.ptapp.ui;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Singleton;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewHeaderDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewLineSeparator;
import com.propertyvista.portal.domain.pt.ChargeLine;
import com.propertyvista.portal.domain.pt.Charges;
import com.propertyvista.portal.domain.pt.Summary;

import com.pyx4j.entity.client.ui.flex.CEntityForm;

@Singleton
public class ChargesViewForm extends CEntityForm<Charges> {

    private RentRelatedCharges rentRelatedCharges;

    private AvailableOptions availableOptions;

    private ProRateCharges proRateCharges;

    private ApplicationCharges applicationCharges;

    private PaymentSplit paymentSplit;

    public ChargesViewForm() {
        super(Charges.class);
    }

    @Override
    public void createContent() {
        FlowPanel main = new FlowPanel();

        main.add(new ViewHeaderDecorator(new HTML("<h4>Rent Related Charges</h4>")));
        main.add(rentRelatedCharges = new RentRelatedCharges());

        main.add(new ViewHeaderDecorator(new HTML("<h4>Available Options</h4>")));
        main.add(availableOptions = new AvailableOptions());

        main.add(new ViewHeaderDecorator(new HTML("<h4>Pro-Rate Charges</h4>")));
        main.add(proRateCharges = new ProRateCharges());

        main.add(new ViewHeaderDecorator(new HTML("<h4>Application Charges</h4>")));
        main.add(applicationCharges = new ApplicationCharges());

        main.add(new ViewHeaderDecorator(new HTML("<h4>Payment Split (Optional)</h4>")));
        main.add(paymentSplit = new PaymentSplit());

        setWidget(main);
    }

    @Override
    public void populate(Charges value) {
        super.populate(value);

        // populate internal views:
        rentRelatedCharges.populate(value);
        availableOptions.populate(value);
        proRateCharges.populate(value);
        applicationCharges.populate(value);
        paymentSplit.populate(value);
    }

    /*
     * Here is the workaround of the problem: our ViewHeaderDecorator has padding 1em on
     * both ends in the CSS style, so in order to set all other internal widgets intended
     * to be whole-width-wide by means of percentage width it's necessary to add those
     * padding values!
     */
    private Widget upperLevelElementElignment(Widget e) {
        e.getElement().getStyle().setPaddingLeft(1, Unit.EM);
        e.getElement().getStyle().setPaddingRight(1, Unit.EM);
        e.setWidth("70%");
        return e;
    }

    private Widget innerLevelElementElignment(Widget e) {
        upperLevelElementElignment(e);
        e.setWidth("100%");
        return e;
    }

    // Various view implementations:

    private abstract class ChargesViewsBase extends FlowPanel {

        public ChargesViewsBase() {
            upperLevelElementElignment(this);
        }

        public abstract void populate(Charges value);

        protected void addRow(String label, String value) {
            addRow(label, value, false);
        }

        protected void addCheckRow(String label, String value) {
            addRow(label, value, true);
        }

        private void addRow(String label, String value, boolean chekBox) {

            Widget l = (chekBox ? new CheckBox(label) : new HTML(label));
            l.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            l.setWidth("40%");
            add(l);

            HTML v = new HTML(value);
            v.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            v.getElement().getStyle().setProperty("textAlign", " right");
            v.setWidth("20%");
            add(v);
        }
    }

    private class RentRelatedCharges extends ChargesViewsBase {

        @Override
        public void populate(Charges value) {

            clear();

            for (ChargeLine cl : value.rentCharges().charges()) {

                addRow(cl.label().getStringView(), "$" + cl.charge().amount().toString());
                Widget sp = new ViewLineSeparator(60, Unit.PCT, 0.5, Unit.EM, 0.5, Unit.EM);
                sp.getElement().getStyle().setPadding(0, Unit.EM);
                add(sp);
            }

            Widget sp = new ViewLineSeparator(60, Unit.PCT, 0.5, Unit.EM, 0.5, Unit.EM);
            sp.getElement().getStyle().setPadding(0, Unit.EM);
            sp.getElement().getStyle().setProperty("border", "1px dotted black");
            add(sp);

            addRow("Sub-Total", "$" + value.rentCharges().total().getStringView());
        }
    }

    private class AvailableOptions extends ChargesViewsBase {

        @Override
        public void populate(Charges value) {

            clear();

            for (ChargeLine cl : value.upgradeCharges().charges()) {

                addCheckRow(cl.label().getStringView(), "$" + cl.charge().amount().toString());
                Widget sp = new ViewLineSeparator(60, Unit.PCT, 0.5, Unit.EM, 0.5, Unit.EM);
                sp.getElement().getStyle().setPadding(0, Unit.EM);
                add(sp);
            }

            Widget sp = new ViewLineSeparator(60, Unit.PCT, 0.5, Unit.EM, 0.5, Unit.EM);
            sp.getElement().getStyle().setPadding(0, Unit.EM);
            sp.getElement().getStyle().setProperty("border", "1px dotted black");
            add(sp);

            addRow("Sub-Total", "$" + value.upgradeCharges().total().getStringView());
        }
    }

    private class ProRateCharges extends ChargesViewsBase {

        @Override
        public void populate(Charges value) {

            clear();

            for (ChargeLine cl : value.proRatedCharges().charges()) {

                addRow(cl.label().getStringView(), "$" + cl.charge().amount().toString());
                Widget sp = new ViewLineSeparator(60, Unit.PCT, 0.5, Unit.EM, 0.5, Unit.EM);
                sp.getElement().getStyle().setPadding(0, Unit.EM);
                add(sp);
            }

            Widget sp = new ViewLineSeparator(60, Unit.PCT, 0.5, Unit.EM, 0.5, Unit.EM);
            sp.getElement().getStyle().setPadding(0, Unit.EM);
            sp.getElement().getStyle().setProperty("border", "1px dotted black");
            add(sp);

            addRow("TOTAL", "$" + value.proRatedCharges().total().getStringView());
        }
    }

    private class ApplicationCharges extends ChargesViewsBase {

        @Override
        public void populate(Charges value) {

            clear();

            for (ChargeLine cl : value.applicationCharges().charges()) {

                addRow(cl.label().getStringView(), "$" + cl.charge().amount().toString());
                Widget sp = new ViewLineSeparator(60, Unit.PCT, 0.5, Unit.EM, 0.5, Unit.EM);
                sp.getElement().getStyle().setPadding(0, Unit.EM);
                add(sp);
            }

            Widget sp = new ViewLineSeparator(60, Unit.PCT, 0.5, Unit.EM, 0.5, Unit.EM);
            sp.getElement().getStyle().setPadding(0, Unit.EM);
            sp.getElement().getStyle().setProperty("border", "1px dotted black");
            add(sp);

            addRow("TOTAL", "$" + value.applicationCharges().total().getStringView());
        }
    }

    private class PaymentSplit extends ChargesViewsBase {

        @Override
        public void populate(Charges value) {

            clear();

            //            for (ChargeLine cl : value.paymentSplitCharges().charges()) {
            //
            //                addRow(cl.label().getStringView(), "$" + cl.charge().amount().toString());
            //                Widget sp = new ViewLineSeparator(60, Unit.PCT, 0.5, Unit.EM, 0.5, Unit.EM);
            //                sp.getElement().getStyle().setPadding(0, Unit.EM);
            //                add(sp);
            //            }

            Widget sp = new ViewLineSeparator(60, Unit.PCT, 0.5, Unit.EM, 0.5, Unit.EM);
            sp.getElement().getStyle().setPadding(0, Unit.EM);
            sp.getElement().getStyle().setProperty("border", "1px dotted black");
            add(sp);

            addRow("TOTAL", "$" + value.paymentSplitCharges().total().getStringView());
        }
    }

}
