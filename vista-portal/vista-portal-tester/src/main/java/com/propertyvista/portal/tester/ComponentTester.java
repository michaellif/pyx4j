/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 31, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.tester;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DecoratedStackPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.propertyvista.portal.tester.ui.TestedCComponentWraper;
import com.propertyvista.portal.tester.util.Constants;

import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CDatePicker;
import com.pyx4j.forms.client.ui.CDoubleField;
import com.pyx4j.forms.client.ui.CEmailField;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.ui.CIntegerField;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CListBox;
import com.pyx4j.forms.client.ui.CLongField;
import com.pyx4j.forms.client.ui.CMonthYearPicker;
import com.pyx4j.forms.client.ui.CPasswordTextField;
import com.pyx4j.forms.client.ui.CRadioGroup;
import com.pyx4j.forms.client.ui.CRadioGroupInteger;
import com.pyx4j.forms.client.ui.CRichTextArea;
import com.pyx4j.forms.client.ui.CSuggestBox;
import com.pyx4j.forms.client.ui.CTextArea;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.forms.client.ui.CTimeField;
import com.pyx4j.site.client.AppSite;

public class ComponentTester extends AppSite {
    private DecoratedStackPanel mainmenu;

    private SplitLayoutPanel maincontainer;

    /**
     * Tested components
     * TODO add more as required
     */
    private List<TestedCComponentWraper> testedComponents;

    @Override
    public void onModuleLoad() {
        hideLoadingIndicator();

        /**
         * Populate array of the tested components
         */
        testedComponents = new ArrayList<TestedCComponentWraper>(20);
        testedComponents.add(new TestedCComponentWraper(new CCheckBox()));
        CComboBox<String> cbx = new CComboBox<String>();
        List<String> options = new ArrayList<String>(3);
        options.add("Option 1");
        options.add("Option 2");
        options.add("Option 3");
        cbx.setOptions(options);
        testedComponents.add(new TestedCComponentWraper(cbx));
        testedComponents.add(new TestedCComponentWraper(new CDatePicker()));
        testedComponents.add(new TestedCComponentWraper(new CDoubleField()));
        testedComponents.add(new TestedCComponentWraper(new CEmailField()));
        CHyperlink hl = new CHyperlink(null, new Command() {
            @Override
            public void execute() {
                Window.alert("Tested");
            }
        });
        hl.setValue("Test Hyperlink");
        testedComponents.add(new TestedCComponentWraper(hl));
        testedComponents.add(new TestedCComponentWraper(new CIntegerField()));
        CLabel clbl = new CLabel();
        clbl.setValue("Test Label");
        testedComponents.add(new TestedCComponentWraper(clbl));
        CListBox<String> lbx = new CListBox<String>();
        List<String> loptions = new ArrayList<String>(3);
        lbx.populate(loptions);
        loptions.add("List Option 1");
        loptions.add("List Option 2");
        loptions.add("List Option 3");
        lbx.setOptions(loptions);

        testedComponents.add(new TestedCComponentWraper(lbx));
        testedComponents.add(new TestedCComponentWraper(new CLongField()));
        testedComponents.add(new TestedCComponentWraper(new CSuggestBox<String>()));
        testedComponents.add(new TestedCComponentWraper(new CTextArea()));
        testedComponents.add(new TestedCComponentWraper(new CTextField()));
        testedComponents.add(new TestedCComponentWraper(new CTimeField()));
        testedComponents.add(new TestedCComponentWraper(new CMonthYearPicker(false)));
        Map<Integer, String> rbuttons = new TreeMap<Integer, String>();
        rbuttons.put(1, "One");
        rbuttons.put(2, "Two");
        rbuttons.put(3, "Tree");
        testedComponents.add(new TestedCComponentWraper(new CRadioGroupInteger(CRadioGroup.Layout.VERTICAL, rbuttons)));
        testedComponents.add(new TestedCComponentWraper(new CRichTextArea()));
        testedComponents.add(new TestedCComponentWraper(new CPasswordTextField()));

        Collections.sort(testedComponents);

        maincontainer = new SplitLayoutPanel();
        /**
         * Main Menu
         */
        mainmenu = new DecoratedStackPanel();
        mainmenu.setWidth("100%");

        /**
         * List of tested widgets
         * 
         */
        VerticalPanel widgetpanel = createWidgetListPanel(testedComponents);

        LayoutPanel p = new LayoutPanel();
        for (TestedCComponentWraper tc : testedComponents) {
            p.add(tc);
            p.setWidgetLeftRight(tc, 3, Unit.PCT, 3, Unit.PCT);
            p.setWidgetTopBottom(tc, 3, Unit.PCT, 3, Unit.PCT);
            tc.getElement().getParentElement().addClassName("pyx-diplay-not");

        }

        mainmenu.add(widgetpanel, "Tested Components");
        mainmenu.add(new HTML("Future Test Groups"), "Other");

        /**
         * Main UI container
         */

        maincontainer.addWest(mainmenu, 200);
        maincontainer.add(p);
        maincontainer.setSize("100%", "100%");
        RootLayoutPanel.get().add(maincontainer);

    }

    @Override
    public void onSiteLoad() {
    };

    /*
     * Create a list of tested widgets
     */
    private VerticalPanel createWidgetListPanel(List<TestedCComponentWraper> testedCWidgets) {
        VerticalPanel widgetpanel = new VerticalPanel();
        widgetpanel.ensureDebugId(Constants.DEBUG_ID_PRFX + "widgetpanel");
        widgetpanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
        widgetpanel.setSpacing(4);

        for (TestedCComponentWraper w : testedCWidgets) {
            final Anchor widlink = new Anchor(w.getShortname());
            widlink.setTarget("_self");
            widlink.setStyleName("pyx-navigator");
            widlink.setName(w.getElement().getId());
            widlink.ensureDebugId(Constants.DEBUG_ID_PRFX + w.getShortname() + "-href");
            widgetpanel.add(widlink);
            // Show test panel for selected widget
            widlink.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    depictTestedComponent(event);
                }
            });
        }

        return widgetpanel;
    }

    private void depictTestedComponent(ClickEvent event) {
        String name = ((Anchor) event.getSource()).getName();
        TestedCComponentWraper found = null;
        for (TestedCComponentWraper c : testedComponents) {
            c.getElement().getParentElement().addClassName("pyx-diplay-not");
            if (name.equals(c.getElement().getId()))
                found = c;

        }
        found.setVisible(true);
        found.getElement().getParentElement().removeClassName("pyx-diplay-not");

    }
}
