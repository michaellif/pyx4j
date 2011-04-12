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
 * @author vadims
 * @version $Id$
 */
package com.propertyvista.portal.tester;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratedStackPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.propertyvista.portal.tester.ui.EditDepartmentViewImpl;
import com.propertyvista.portal.tester.ui.TestedCComponentWraper;
import com.propertyvista.portal.tester.unit.TestDeferredCommands;
import com.propertyvista.portal.tester.util.Constants;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.entity.client.ClientEntityFactory;
import com.pyx4j.forms.client.ui.CButton;
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
import com.pyx4j.log4gwt.client.ClientLogger;
import com.pyx4j.log4gwt.rpcappender.RPCAppender;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.unit.client.ui.TestRunnerDialog;
import com.pyx4j.unit.shared.UnitDebugId;
import com.pyx4j.widgets.client.dialog.UnrecoverableErrorHandlerDialog;
import com.pyx4j.widgets.client.style.StyleManger;
import com.pyx4j.widgets.client.style.Theme;
import com.pyx4j.widgets.client.style.ThemeColor;
import com.pyx4j.widgets.client.style.theme.WindowsTheme;

public class ComponentTester extends AppSite {
    private DecoratedStackPanel mainmenu;

    private SplitLayoutPanel maincontainer;

    /**
     * Tested components
     * TODO add more as required
     */
    private List<TestedCComponentWraper> testedComponents;

    private Widget beingtested;

    private LayoutPanel testcontainer;

    @Override
    public void onModuleLoad() {
        ClientEntityFactory.ensureIEntityImplementations();
        UnrecoverableErrorHandlerDialog.register();

        Theme theme = new WindowsTheme();
        theme.putThemeColor(ThemeColor.OBJECT_TONE1, 0xFFFFFF);
        StyleManger.installTheme(theme);

        ClientLogger.addAppender(new RPCAppender());
        ClientLogger.setDebugOn(true);
        RPCManager.enableAppEngineUsageStats();

        hideLoadingIndicator();
        beingtested = null;

        final Label testMessage = new Label();
        testMessage.ensureDebugId(TesterDebugId.TestMessage.name());
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
                testMessage.setText("CHyperlink clicked");
            }
        });
        hl.setValue("Test Hyperlink");
        testedComponents.add(new TestedCComponentWraper(hl));
        CButton b = new CButton("Test CButton", new Command() {
            @Override
            public void execute() {
                testMessage.setText("CButton clicked");
            }
        });
        testedComponents.add(new TestedCComponentWraper(b));
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
        testcontainer = new LayoutPanel();

        testcontainer.setStyleName("pyx-generic-background");

        HorizontalPanel messagePanel = new HorizontalPanel();
        testcontainer.add(messagePanel);

        HTML version = new HTML("GWT version used: " + GWT.getVersion());
        version.setStyleName("pyx-note", true);
        messagePanel.add(version);

        messagePanel.add(testMessage);
        Button testMessageClear = new Button("Clear Message");
        testMessageClear.ensureDebugId(TesterDebugId.TestMessageClear.name());
        messagePanel.add(testMessageClear);
        testMessageClear.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                testMessage.setText("");
            }
        });

        /*
         * for (TestedCComponentWraper tc : testedComponents) {
         * testingcontainer.add(tc);
         * testingcontainer.setWidgetLeftRight(tc, 3, Unit.PCT, 3, Unit.PCT);
         * testingcontainer.setWidgetTopBottom(tc, 3, Unit.PCT, 3, Unit.PCT);
         * tc.getElement().getParentElement().addClassName("pyx-diplay-not");
         * 
         * }
         */

        mainmenu.add(widgetpanel, "Tested C Components");

        final Anchor junitlink = new Anchor("Start Tests");
        junitlink.setTarget("_self");
        junitlink.setStyleName("pyx-navigator");
        junitlink.ensureDebugId(UnitDebugId.JUnit_StartClientTests.name());
        junitlink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                TestRunnerDialog.createAsync();
            }
        });
        mainmenu.add(junitlink, "Client Side JUnit");

        final Anchor dformlink = new Anchor("Departments");
        dformlink.setTarget("_self");
        dformlink.setStyleName("pyx-navigator");
        dformlink.ensureDebugId(Constants.DEBUG_ID_PRFX + TesterDebugId.F1_HREF.debugId());
        dformlink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (beingtested != null)
                    ComponentTester.this.testcontainer.remove(beingtested);
                beingtested = new EditDepartmentViewImpl();
                //new TestDeptImpl();
                //EditDepartmentViewImpl();
                if (beingtested != null) {
                    ComponentTester.this.testcontainer.add(beingtested);
                    ComponentTester.this.testcontainer.setWidgetLeftRight(beingtested, 3, Unit.PCT, 3, Unit.PCT);
                    ComponentTester.this.testcontainer.setWidgetTopBottom(beingtested, 3, Unit.PCT, 3, Unit.PCT);
                }
            }
        });

        mainmenu.add(dformlink, "Form Tests");

        mainmenu.add(new TestDeferredCommands(), "Deferred Commands");

        mainmenu.ensureDebugId(TesterDebugId.TesterMainMenu.name());
        /**
         * Main UI container
         */
        maincontainer.addWest(mainmenu, 200);
        maincontainer.add(testcontainer);
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
        widgetpanel.ensureDebugId(Constants.DEBUG_ID_PRFX + TesterDebugId.CCOMP_STACK.debugId());
        widgetpanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
        widgetpanel.setSpacing(4);

        for (TestedCComponentWraper w : testedCWidgets) {
            final Anchor widlink = new Anchor(w.getShortname());
            widlink.setTarget("_self");
            widlink.setStyleName("pyx-navigator");
            widlink.setName(w.getElement().getId());
            widlink.ensureDebugId(CompositeDebugId.debugId(w.getShortname(), TesterDebugId.StartTestSufix));
            widgetpanel.add(widlink);
            // Show test panel for selected widget
            widlink.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    //Remove current tested component if any
                    if (beingtested != null)
                        ComponentTester.this.testcontainer.remove(beingtested);
                    beingtested = getTestedComponentById(((Anchor) event.getSource()).getName());
                    if (beingtested != null) {
                        ComponentTester.this.testcontainer.add(beingtested);
                        ComponentTester.this.testcontainer.setWidgetLeftRight(beingtested, 3, Unit.PCT, 3, Unit.PCT);
                        ComponentTester.this.testcontainer.setWidgetTopBottom(beingtested, 3, Unit.PCT, 3, Unit.PCT);
                    }

                }
            });
        }

        return widgetpanel;
    }

    private TestedCComponentWraper getTestedComponentById(String compId) {
        if (compId == null)
            return null;
        for (TestedCComponentWraper tc : testedComponents) {
            if (tc.getElement().getId().equals(compId))
                return tc;

        }
        return null;
    }

    /*
     * private void depictTestedComponent(ClickEvent event) {
     * String name = ((Anchor) event.getSource()).getName();
     * TestedCComponentWraper found = null;
     * for (TestedCComponentWraper c : testedComponents) {
     * if (c.isActive())
     * c.setActive(false);
     * if (name.equals(c.getElement().getId()))
     * found = c;
     * 
     * }
     * if (found != null)
     * found.setActive(true);
     * }
     */
}
