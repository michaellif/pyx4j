/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 15, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.operations.client.themes;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Selector;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.DefaultCComponentsTheme;
import com.pyx4j.forms.client.ui.datatable.DefaultDataTableTheme;
import com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme;
import com.pyx4j.forms.client.ui.folder.DefaultEntityFolderTheme;
import com.pyx4j.forms.client.ui.panels.FlexFormPanelTheme;
import com.pyx4j.site.client.ui.DefaultPaneTheme;
import com.pyx4j.widgets.client.DefaultWidgetsTheme;
import com.pyx4j.widgets.client.datepicker.DefaultDatePickerTheme;
import com.pyx4j.widgets.client.dialog.DefaultDialogTheme;
import com.pyx4j.widgets.client.tabpanel.DefaultTabTheme;

import com.propertyvista.common.client.theme.DraggerMixin;
import com.propertyvista.common.client.theme.HorizontalAlignCenterMixin;
import com.propertyvista.common.client.theme.SiteViewTheme;
import com.propertyvista.common.client.theme.VistaTheme;
import com.propertyvista.operations.client.ui.SearchBox;
import com.propertyvista.operations.client.ui.SearchBox.StyleSuffix;

public class OperationsTheme extends VistaTheme {

    public static double defaultHeaderHeight = 3;

    public static double defaultFooterHeight = 3;

    public static double defaultActionBarHeight = 2.9;

    public static double defaultTabHeight = 2.6;

    public static enum OperationsStyles implements IStyleName {

        TextFieldPreformatted

    }

    public OperationsTheme() {
        initStyles();
    }

    protected void initStyles() {
        addTheme(new HorizontalAlignCenterMixin());

        addTheme(new DefaultWidgetsTheme());
        addTheme(new DefaultWidgetDecoratorTheme() {
            @Override
            protected ThemeColor getBackgroundColor() {
                return ThemeColor.foreground;
            }
        });
        addTheme(new FlexFormPanelTheme() {
            @Override
            protected ThemeColor getBackgroundColor() {
                return ThemeColor.foreground;
            }
        });
        addTheme(new DefaultEntityFolderTheme() {
            @Override
            protected ThemeColor getBackgroundColor() {
                return ThemeColor.foreground;
            }
        });

        addTheme(new DefaultDatePickerTheme());
        addTheme(new DefaultPaneTheme());
        addTheme(new DefaultDataTableTheme());
        addTheme(new DefaultDialogTheme());
        addTheme(new DefaultCComponentsTheme());

        addTheme(new DefaultTabTheme());

        addTheme(new SiteViewTheme());
        addTheme(new DraggerMixin());

        initGeneralStyles();
        initBodyStyles();
        initCellListStyle();
        initMessageStyles();

        initSearchBoxStyles();
        initButtonStylesEx();

        initTabPanelStyles();
        initDialogBoxStyles();
        initMenuBarStyles();

        initSuggestBoxStyle();

        initOperationsStyle();
    }

    @Override
    protected void initGeneralStyles() {
        super.initGeneralStyles();

        Style style = new Style("a");
        style.addProperty("color", "#333");
        addStyle(style);
    }

    private void initOperationsStyle() {
        Style style = new Style(".", OperationsStyles.TextFieldPreformatted, " .", "gwt-HTML");
        style.addProperty("white-space", "pre !important;");
        addStyle(style);
    }

    protected void initSearchBoxStyles() {
        String prefix = SearchBox.DEFAULT_STYLE_NAME;

        Style style = new Style(Selector.valueOf(prefix));
        style.addProperty("background", "#ffffff");
        style.addProperty("overflow", "hidden");
        style.addProperty("border", "none");
        style.addProperty("border-radius", "15px");
        style.addProperty("-moz-border-radius", "15px");
        style.addProperty("min-width", "8em !important");
        style.addProperty("white-space", "normal !important");
        style.addProperty("padding-left", "0.7em !important");
        addStyle(style);

        // SearchBox
        style = new Style(Selector.valueOf(prefix, StyleSuffix.Text));
        style.addProperty("border-radius", "15px");
        style.addProperty("-moz-border-radius", "15px");
        style.addProperty("border", "none");
        //  style.addProperty("min-width", "4em !important");
        addStyle(style);

        //trigger
        style = new Style(Selector.valueOf(prefix, StyleSuffix.Trigger));
/*
 * style.addProperty("background", "url(" + AdminImages.INSTANCE.search().getURL() +
 * ") no-repeat");
 * style.addProperty("background-position", "center");
 */
        style.addProperty("width", "16px");
        style.addProperty("heigh", "16px");
        style.addProperty("float", "right !important");
        style.addProperty("border", "none");
        style.addProperty("margin-right", "0.2em !important");
        addStyle(style);
    }

    protected void initButtonStylesEx() {

        //
        // Toggle Button
        String buttonEx = Selector.valueOf("gwt-ToggleButton");
        Style style = new Style(buttonEx);
        style.addProperty("color", ThemeColor.object1, 0.1);
        style.addProperty("background-color", ThemeColor.object1, 0.8);
        style.addProperty("border-width", "0px");
        style.addProperty("border-style", "solid");
        style.addProperty("margin", "0.2em 0.2em");
        style.addProperty("padding", "0.2em 0.5em");
        style.addProperty("text-align", "center");
        addStyle(style);

        buttonEx = Selector.valueOf("gwt-ToggleButton-up");
        style = new Style(buttonEx);
        style.addProperty("background-color", ThemeColor.object1, 0.4);
        style.addProperty("border-left-color", ThemeColor.object1, 0.4);
        style.addProperty("border-top-color", ThemeColor.object1, 0.4);
        style.addProperty("border-right-color", ThemeColor.object1, 0.95);
        style.addProperty("border-bottom-color", ThemeColor.object1, 0.95);
        addStyle(style);

        buttonEx = Selector.valueOf("gwt-ToggleButton-up-hovering");
        style = new Style(buttonEx);
        style.addProperty("cursor", "hand");
        style.addProperty("background-color", ThemeColor.object1, 0.4);
        style.addProperty("border-left-color", ThemeColor.object1, 0.1);
        style.addProperty("border-top-color", ThemeColor.object1, 0.1);
        style.addProperty("border-right-color", ThemeColor.object1, 0.95);
        style.addProperty("border-bottom-color", ThemeColor.object1, 0.95);
        addStyle(style);

        buttonEx = Selector.valueOf("gwt-ToggleButton-down");
        style = new Style(buttonEx);
        style.addProperty("background-color", ThemeColor.object1, 0.8);
        style.addProperty("border-left-color", ThemeColor.object1, 0.95);
        style.addProperty("border-top-color", ThemeColor.object1, 0.95);
        style.addProperty("border-right-color", ThemeColor.object1, 0.1);
        style.addProperty("border-bottom-color", ThemeColor.object1, 0.1);
        addStyle(style);

        buttonEx = Selector.valueOf("gwt-ToggleButton-down-hovering");
        style = new Style(buttonEx);
        style.addProperty("cursor", "hand");
        style.addProperty("background-color", ThemeColor.object1, 0.8);
        style.addProperty("border-left-color", ThemeColor.object1, 0.95);
        style.addProperty("border-top-color", ThemeColor.object1, 0.95);
        style.addProperty("border-right-color", ThemeColor.object1, 0.1);
        style.addProperty("border-bottom-color", ThemeColor.object1, 0.1);
        addStyle(style);

        style = new Style(buttonEx + ":hover");
        style.addProperty("text-decoration", "underline");
        addStyle(style);

    }

}