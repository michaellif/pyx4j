/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 4, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.tester.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.LongBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.propertyvista.common.client.ui.VistaWidgetDecorator;
import com.propertyvista.common.client.ui.VistaWidgetDecorator.DecorationData;
import com.propertyvista.portal.tester.util.Constants;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CDatePicker;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CEmailField;
import com.pyx4j.forms.client.ui.CFocusComponent;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.ui.CIntegerField;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CLongField;
import com.pyx4j.forms.client.ui.CNumberField;
import com.pyx4j.forms.client.ui.CPasswordTextField;
import com.pyx4j.forms.client.ui.CSuggestBox;
import com.pyx4j.forms.client.ui.CTextArea;
import com.pyx4j.forms.client.ui.CTextComponent;
import com.pyx4j.forms.client.ui.CTextField;

public class TestedCComponentWraper extends LayoutPanel implements Comparable<TestedCComponentWraper> {

    private final VerticalPanel testedfeatures;

    private final LayoutPanel testedcomponent;

    private CFocusComponent<?> component;

    private String fullname;

    private String shortname;

    private final Label rawvalue;

    public TestedCComponentWraper(CFocusComponent<?> component) {
        super();
        testedfeatures = new VerticalPanel();
        testedcomponent = new LayoutPanel();
        rawvalue = new Label("");
        rawvalue.setStyleName("pyx-footer");
        rawvalue.ensureDebugId(Constants.DEBUG_ID_PRFX + "rawvalue");

        /**
         * Draw the Tested Features widget
         */
        Label t = new Label("Tested Features");
        t.setStyleName("pyx-header");
        DockLayoutPanel lp = new DockLayoutPanel(Unit.PCT);
        lp.addNorth(t, 10);
        VerticalPanel vp = new VerticalPanel();
        lp.setStylePrimaryName("pyx-chrome-border");

        vp.add(testedfeatures);
        testedfeatures.setSpacing(4);
        lp.add(vp);
        this.add(lp);
        this.setWidgetLeftWidth(lp, 1, Unit.PCT, 25, Unit.PCT);
        this.setWidgetTopBottom(lp, 5, Unit.PCT, 5, Unit.PCT);

        /**
         * Draw testing area
         */
        testedcomponent.setSize("100%", "100%");
        this.add(testedcomponent);
        this.setWidgetRightWidth(testedcomponent, 0, Unit.PCT, 71, Unit.PCT);
        this.setWidgetTopBottom(testedcomponent, 5, Unit.PCT, 5, Unit.PCT);
        processTestedComponent(component);
        this.ensureDebugId(Constants.DEBUG_ID_PRFX + fullname);

    }

    /**
     * 
     * @param component
     *            Format tested component, figure out its features, type and the like
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void processTestedComponent(CFocusComponent<?> component) {
        this.component = component;
        if (this.component == null)
            return;

        /**
         * right-hand panel formatting
         */
        fullname = this.component.getClass().getName();
        String[] s = fullname.split("\\.");
        shortname = s[s.length - 1];
        this.component.setTitle("Tested Value:");
        this.component.setDebugId(

        new IDebugId() {
            @Override
            public String getDebugIdString() {
                return (Constants.DEBUG_ID_PRFX + shortname);
            }
        }

        );

        /**
         * Format testing area
         */
        DockLayoutPanel lp = new DockLayoutPanel(Unit.PCT);
        //header
        Label t = new Label(fullname);
        t.setStyleName("pyx-header");
        lp.addNorth(t, 10);
        lp.addSouth(rawvalue, 36);
        //container for a tested component
        VerticalPanel vp = new VerticalPanel();
        vp.setWidth("100%");
        lp.setStylePrimaryName("pyx-chrome-border");
        VerticalPanel vvp = new VerticalPanel();
        vvp.setWidth("100%");
        //Tested component group
        HorizontalPanel testedrow = new HorizontalPanel();
        testedrow.setWidth("100%");

        //Specify tested component decorator for all but CHyperlink and CLabel

        DecorationData ddata = new DecorationData(120, 200);
        ddata.editable = true;
        ddata.hideInfoHolder = false;
        CComponent<?> cm = this.component;
        VistaWidgetDecorator decoratedcomp = new VistaWidgetDecorator(cm, ddata);
        testedrow.add(decoratedcomp);
        testedrow.setCellWidth(decoratedcomp, "470px");

        Button b = new Button("Print Component Raw Data");
        b.ensureDebugId(Constants.DEBUG_ID_PRFX + shortname + "-focus-btn");
        b.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                rawvalue.setText(TestedCComponentWraper.this.component.toString());

            }
        });
        testedrow.add(b);
        vvp.add(testedrow);
        vvp.setSpacing(10);
        vp.add(vvp);
        lp.add(vp);
        testedcomponent.add(lp);
        /**
         * tested features compilation
         */

        //TODO Toolotip does not work
        this.component.setTitle("This is " + shortname);
        this.component.setToolTip("Tooltip for " + shortname);
        //Enable disable
        CheckBox chk = null;
        if (!(this.component instanceof CLabel /* || this.component instanceof CHyperlink */)) {
            chk = new CheckBox("Disabled");
            chk.ensureDebugId(Constants.DEBUG_ID_PRFX + shortname + "-disabled-chk");
            chk.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    enableDisable(event);
                }
            });
            testedfeatures.add(chk);
        }

        if (this.component instanceof CEditableComponent<?, ?> && !(this.component instanceof CLabel || this.component instanceof CHyperlink)) {
            //Mandatory optional
            final CEditableComponent<?, ?> ec = (CEditableComponent<?, ?>) this.component;
            chk = new CheckBox("Mandatory");
            chk.ensureDebugId(Constants.DEBUG_ID_PRFX + shortname + "-mandatory-chk");

            chk.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    CheckBox chk = (CheckBox) event.getSource();
                    ec.setMandatory(chk.getValue());
                    ec.revalidate();
                }
            });
            testedfeatures.add(chk);

            chk = new CheckBox("Read Only");
            chk.ensureDebugId(Constants.DEBUG_ID_PRFX + shortname + "-read-only-chk");
            chk.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    CheckBox chk = (CheckBox) event.getSource();
                    ec.setEditable(!chk.getValue());
                }
            });

            testedfeatures.add(chk);

            chk = new CheckBox("Visited");
            chk.ensureDebugId(Constants.DEBUG_ID_PRFX + shortname + "-visited-chk");
            chk.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    CheckBox chk = (CheckBox) event.getSource();
                    ec.setVisited(chk.getValue());
                    ec.revalidate();
                }
            });

            testedfeatures.add(chk);

        }
        if (this.component instanceof CTextComponent<?, ?> && !(this.component instanceof CDatePicker || this.component instanceof CPasswordTextField)) {
            final CTextComponent<?, ?> ctxt = (CTextComponent<?, ?>) this.component;
            TextBox wmfield = new TextBox();
            wmfield.setTitle("Watermark");
            wmfield.ensureDebugId(Constants.DEBUG_ID_PRFX + shortname + "-watermark-txt");
            wmfield.addBlurHandler(new BlurHandler() {
                @Override
                public void onBlur(BlurEvent event) {
                    TextBox tb = (TextBox) event.getSource();
                    ctxt.setWatermark(tb.getValue());
                    ctxt.revalidate();
                }
            });
            Label wlbl = new Label("Watermark:");
            HorizontalPanel hp = new HorizontalPanel();
            hp.add(wlbl);
            hp.setCellWidth(wlbl, "35%");
            hp.add(wmfield);
            hp.setWidth("100%");
            hp.setSpacing(2);
            testedfeatures.add(hp);
        }
        if (this.component instanceof CNumberField) {
            final CNumberField cnum = (CNumberField) this.component;
            final ValueBox<?> fromfield;
            final ValueBox<?> tofield;

            if (cnum instanceof CIntegerField) {
                fromfield = new IntegerBox();
                tofield = new IntegerBox();
            } else if (cnum instanceof CLongField) {
                fromfield = new LongBox();
                tofield = new LongBox();
            } else {
                fromfield = new DoubleBox();
                tofield = new DoubleBox();
            }

            fromfield.ensureDebugId(Constants.DEBUG_ID_PRFX + shortname + "-fromnum-txt");
            fromfield.setTitle("From Number");
            fromfield.setWidth("85%");
            tofield.ensureDebugId(Constants.DEBUG_ID_PRFX + shortname + "-tonum-txt");
            tofield.setTitle("To Number");
            tofield.setWidth("85%");

            fromfield.addBlurHandler(new BlurHandler() {
                @Override
                public void onBlur(BlurEvent event) {
                    Number f = ((ValueBox<Number>) fromfield).getValue();
                    Number t = ((ValueBox<Number>) tofield).getValue();

                    if (f.doubleValue() > 0 && t.doubleValue() > 0)
                        cnum.setRange(f, t);
                    else
                        cnum.setRange(null, null);
                    cnum.revalidate();
                }
            });

            tofield.addBlurHandler(new BlurHandler() {
                @Override
                public void onBlur(BlurEvent event) {
                    Number f = ((ValueBox<Number>) fromfield).getValue();
                    Number t = ((ValueBox<Number>) tofield).getValue();

                    if (f.doubleValue() > 0 && t.doubleValue() > 0)
                        cnum.setRange(f, t);
                    else
                        cnum.setRange(null, null);
                    cnum.revalidate();

                }
            });

            FlexTable fxt = new FlexTable();
            fxt.setWidget(0, 0, new Label("Data Range:"));
            fxt.setWidget(1, 0, fromfield);
            fxt.setWidget(1, 1, tofield);
            fxt.setWidth("100%");
            fxt.getFlexCellFormatter().setColSpan(0, 0, 2);
            fxt.getFlexCellFormatter().setAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
            fxt.getFlexCellFormatter().setHorizontalAlignment(1, 0, HasHorizontalAlignment.ALIGN_CENTER);
            fxt.getFlexCellFormatter().setHorizontalAlignment(1, 1, HasHorizontalAlignment.ALIGN_CENTER);
            fxt.setStyleName("pyx-chrome-tiny-border");
            testedfeatures.add(fxt);

        }

        if (this.component instanceof CComboBox<?>) {
            CComboBox<?> cbx = (CComboBox<?>) this.component;
            cbx.setNoSelectionText("Please select an option");

        } else if (this.component instanceof CDatePicker) {
            final CDatePicker dtpk = (CDatePicker) this.component;
            chk = new CheckBox("No Past Date Selection");
            chk.ensureDebugId(Constants.DEBUG_ID_PRFX + shortname + "-no-pastdate-selection-chk");
            chk.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    CheckBox chk = (CheckBox) event.getSource();
                    dtpk.setPastDateSelectionAllowed(!chk.getValue());
                    dtpk.revalidate();
                }
            });
            testedfeatures.add(chk);
        } else if (this.component instanceof CEmailField || this.component instanceof CTextField || this.component instanceof CTextArea) {
            final CTextComponent<?, ?> ctxt = (CTextComponent<?, ?>) this.component;
            IntegerBox maxfield = new IntegerBox();
            maxfield.ensureDebugId(Constants.DEBUG_ID_PRFX + shortname + "-maxlength-txt");
            maxfield.setTitle("Max Length");
            maxfield.addBlurHandler(new BlurHandler() {
                @Override
                public void onBlur(BlurEvent event) {
                    IntegerBox tb = (IntegerBox) event.getSource();
                    Integer i = tb.getValue();
                    if (i == null)
                        i = 0;
                    ctxt.setMaxLength(i);
                    ctxt.revalidate();
                }
            });
            Label wlbl = new Label("Max Length:");
            HorizontalPanel hp = new HorizontalPanel();
            hp.add(wlbl);
            hp.setCellWidth(wlbl, "35%");
            hp.add(maxfield);
            hp.setWidth("100%");
            hp.setSpacing(2);
            testedfeatures.add(hp);
        } else if (this.component instanceof CSuggestBox<?>) {
            /**
             * Note! If this component gets instantiated and configured in calling object
             * the CSuggestBox does not show suggestions
             */
            CSuggestBox<?> sbx = (CSuggestBox<?>) (this.component);
            List options = new ArrayList(4);
            options.add("opt 1");
            options.add("option 2");
            options.add("pyx");
            options.add("abc");
            sbx.setOptions(options);

        }

    }

    public String getFullname() {
        return fullname;
    }

    public String getShortname() {
        return shortname;
    }

    private void enableDisable(ClickEvent e) {
        CheckBox chk = (CheckBox) e.getSource();
        component.setEnabled(!chk.getValue());
    }

    @Override
    public int compareTo(TestedCComponentWraper o) {
        if (o == null)
            return 1;
        return shortname.compareTo(o.getShortname());
    }

}