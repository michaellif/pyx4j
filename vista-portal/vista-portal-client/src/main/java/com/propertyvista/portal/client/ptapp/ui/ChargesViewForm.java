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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Singleton;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewHeaderDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewLineSeparator;
import com.propertyvista.portal.domain.pt.ChargeLine;
import com.propertyvista.portal.domain.pt.Charges;
import com.propertyvista.portal.domain.pt.Summary;
import com.propertyvista.portal.domain.pt.TenantCharge;

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

        public void addRow(String label, String value) {

            addRow(new HTML(label), new HTML(value));
        }

        public CheckBox addCheckRow(String label, String value, boolean setSelected) {

            CheckBox chk = new CheckBox(label);
            chk.setValue(setSelected);
            addRow(chk, new HTML(value));
            return chk;
        }

        public TextBox addEditRow(String label, String edit, String value) {

            TextBox txt = new TextBox();
            txt.setValue(edit);
            addRow(new HTML(label), txt, new HTML(value));
            return txt;
        }

        // Internal formatters:
        private void addRow(Widget left, Widget right) {

            left.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            left.setWidth("60%");
            add(left);

            right.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            right.getElement().getStyle().setProperty("textAlign", " right");
            right.setWidth("10%");
            add(right);
        }

        private void addRow(Widget left, Widget middle, Widget right) {

            left.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            left.setWidth("50%");
            add(left);

            HTML preMiddle = new HTML("%&nbsp");
            preMiddle.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            preMiddle.getElement().getStyle().setProperty("textAlign", " right");
            preMiddle.setWidth("5%");
            add(preMiddle);

            middle.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            middle.setWidth("5%");
            add(middle);

            right.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            right.getElement().getStyle().setProperty("textAlign", " right");
            right.setWidth("10%");
            add(right);
        }
    }

    private class RentRelatedCharges extends ChargesViewsBase {

        @Override
        public void populate(Charges value) {

            clear();

            boolean firstRun = true;
            for (final ChargeLine cl : value.rentCharges().charges()) {

                if (!firstRun) {
                    Widget sp = new ViewLineSeparator(70, Unit.PCT, 0.5, Unit.EM, 0.5, Unit.EM);
                    sp.getElement().getStyle().setPadding(0, Unit.EM);
                    add(sp);
                } else {
                    firstRun = false;
                }

                addRow(cl.type().getValue().getLabel(), "$" + cl.charge().amount().getStringView());
            }

            Widget sp = new ViewLineSeparator(70, Unit.PCT, 0.5, Unit.EM, 0.5, Unit.EM);
            sp.getElement().getStyle().setPadding(0, Unit.EM);
            sp.getElement().getStyle().setProperty("border", "1px dotted black");
            add(sp);

            addRow("Sub-Total", "$" + value.rentCharges().total().amount().getStringView());
        }
    }

    private class AvailableOptions extends ChargesViewsBase {

        @Override
        public void populate(Charges value) {

            clear();

            boolean firstRun = true;
            for (final ChargeLine cl : value.upgradeCharges().charges()) {

                if (!firstRun) {
                    Widget sp = new ViewLineSeparator(70, Unit.PCT, 0.5, Unit.EM, 0.5, Unit.EM);
                    sp.getElement().getStyle().setPadding(0, Unit.EM);
                    add(sp);
                } else {
                    firstRun = false;
                }

                final CheckBox chk = addCheckRow(cl.type().getValue().getLabel(), "$" + cl.charge().amount().getStringView(), cl.selected().getValue());
                chk.addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        cl.selected().setValue(chk.getValue());
                    }
                });
            }

            Widget sp = new ViewLineSeparator(70, Unit.PCT, 0.5, Unit.EM, 0.5, Unit.EM);
            sp.getElement().getStyle().setPadding(0, Unit.EM);
            sp.getElement().getStyle().setProperty("border", "1px dotted black");
            add(sp);

            addRow("Sub-Total", "$" + value.upgradeCharges().total().amount().getStringView());
        }
    }

    private class ProRateCharges extends ChargesViewsBase {

        @Override
        public void populate(Charges value) {

            clear();

            boolean firstRun = true;
            for (final ChargeLine cl : value.proRatedCharges().charges()) {

                if (!firstRun) {
                    Widget sp = new ViewLineSeparator(70, Unit.PCT, 0.5, Unit.EM, 0.5, Unit.EM);
                    sp.getElement().getStyle().setPadding(0, Unit.EM);
                    add(sp);
                } else {
                    firstRun = false;
                }

                addRow(cl.type().getValue().getLabel(), "$" + cl.charge().amount().getStringView());
            }

            Widget sp = new ViewLineSeparator(70, Unit.PCT, 0.5, Unit.EM, 0.5, Unit.EM);
            sp.getElement().getStyle().setPadding(0, Unit.EM);
            sp.getElement().getStyle().setProperty("border", "1px dotted black");
            add(sp);

            addRow("TOTAL", "$" + value.proRatedCharges().total().amount().getStringView());
        }
    }

    private class ApplicationCharges extends ChargesViewsBase {

        @Override
        public void populate(Charges value) {

            clear();

            boolean firstRun = true;
            for (final ChargeLine cl : value.applicationCharges().charges()) {

                if (!firstRun) {
                    Widget sp = new ViewLineSeparator(70, Unit.PCT, 0.5, Unit.EM, 0.5, Unit.EM);
                    sp.getElement().getStyle().setPadding(0, Unit.EM);
                    add(sp);
                } else {
                    firstRun = false;
                }

                addRow(cl.type().getValue().getLabel(), "$" + cl.charge().amount().getStringView());
            }

            Widget sp = new ViewLineSeparator(70, Unit.PCT, 0.5, Unit.EM, 0.5, Unit.EM);
            sp.getElement().getStyle().setPadding(0, Unit.EM);
            sp.getElement().getStyle().setProperty("border", "1px dotted black");
            add(sp);

            addRow("TOTAL", "$" + value.applicationCharges().total().amount().getStringView());
        }
    }

    private class PaymentSplit extends ChargesViewsBase {

        @Override
        public void populate(Charges value) {

            clear();

            boolean firstRun = true;
            for (final TenantCharge tc : value.paymentSplitCharges().charges()) {

                if (!firstRun) {
                    Widget sp = new ViewLineSeparator(70, Unit.PCT, 0.5, Unit.EM, 0.5, Unit.EM);
                    sp.getElement().getStyle().setPadding(0, Unit.EM);
                    add(sp);
                } else {
                    firstRun = false;
                }

                final TextBox txt = addEditRow(tc.tenant().firstName().getStringView() + " &nbsp " + tc.tenant().lastName().getStringView(), tc.percentage()
                        .getStringView(), "$" + tc.charge().amount().getStringView());

                // Filter non-digit input.
                txt.addKeyPressHandler(new KeyPressHandler() {

                    @Override
                    public void onKeyPress(KeyPressEvent event) {
                        if (!Character.isDigit(event.getCharCode())) {
                            ((TextBox) event.getSource()).cancelKey();
                        }
                    }
                });

                // accept input:
                txt.addFocusHandler(new FocusHandler() {

                    @Override
                    public void onFocus(FocusEvent event) {
                        // TODO : distinguish between SET focus and LOST focus here!!!
                        tc.percentage().setValue(Integer.parseInt(txt.getValue().trim()));
                    }
                });
            }

            Widget sp = new ViewLineSeparator(70, Unit.PCT, 0.5, Unit.EM, 0.5, Unit.EM);
            sp.getElement().getStyle().setPadding(0, Unit.EM);
            sp.getElement().getStyle().setProperty("border", "1px dotted black");
            add(sp);

            addRow("TOTAL", "$" + value.paymentSplitCharges().total().amount().getStringView());
        }
    }

}
