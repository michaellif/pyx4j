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
package com.propertyvista.portal.client.themes;

import java.util.List;

import com.pyx4j.entity.client.ui.datatable.DataTable;
import com.pyx4j.forms.client.ui.NativeRadioGroup;
import com.pyx4j.site.client.ui.crud.ListerBase;
import com.pyx4j.widgets.client.ListBox;
import com.pyx4j.widgets.client.style.Selector;
import com.pyx4j.widgets.client.style.Style;
import com.pyx4j.widgets.client.style.ThemeColors;

import com.propertyvista.common.client.theme.VistaTheme;
import com.propertyvista.common.client.ui.decorations.VistaHeaderBar;
import com.propertyvista.common.client.ui.decorations.VistaLineSeparator;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator.StyleSuffix;
import com.propertyvista.portal.client.ui.NavigViewImpl;
import com.propertyvista.portal.client.ui.PortalScreen;
import com.propertyvista.portal.client.ui.decorations.CriteriaWidgetDecorator;
import com.propertyvista.portal.client.ui.decorations.TableFolderDecorator;
import com.propertyvista.portal.client.ui.maps.PropertiesMapWidget;
import com.propertyvista.portal.client.ui.residents.NewPaymentMethodForm;

public class PortalTheme extends VistaTheme {

    public PortalTheme() {
        super();
    }

    @Override
    protected void initStyles() {
        super.initStyles();
        initBodyStyles();
        initListBoxStyle();
        initListerStyles();
        initEntityDataTableStyles();
        initDecoratorsStyles();
        initSiteViewStyles();
        initVistaNavigViewStyles();
        initTableDecorators();
        initCriteriaWidgetDecoratorStyles();
        initPropertyMarkerStyle();
        initPaymentRadioButtonGroupStyles();
    }

    @Override
    protected void initListBoxStyle() {
        super.initListBoxStyle();
        Style style = new Style(Selector.valueOf(ListBox.DEFAULT_STYLE_PREFIX), " option");
        style.addProperty("background-color", "white");
        addStyle(style);
    }

    protected void initSiteViewStyles() {
        String prefix = PortalScreen.DEFAULT_STYLE_PREFIX;

        int minWidth = 960;
        int maxWidth = 960;
        int leftColumnWidth = 0;
        int rightColumnWidth = 0;

        Style style = new Style(Selector.valueOf(prefix));
        style.addProperty("width", "95%");
        style.addProperty("min-width", minWidth + "px");
        style.addProperty("max-width", maxWidth + "px");
        style.addProperty("margin", "0 auto");
        addStyle(style);

        String headerstyle = Selector.valueOf(prefix, PortalScreen.StyleSuffix.Header);
        style = new Style(headerstyle);
        style.addProperty("background-color", ThemeColors.OBJECT_TONE35);
        style.addProperty("font-size", "14px");
        style.addProperty("color", ThemeColors.OBJECT_TONE95);
        style.addProperty("height", "115px");
        style.addProperty("display", "table");
        addStyle(style);

        //TODO  think of a better way
        style = new Style(headerstyle + " a:link, a:visited, a:active");
        style.addProperty("text-decoration", "none");
        style.addProperty("color", ThemeColors.OBJECT_TONE95);
        addStyle(style);

        style = new Style(headerstyle + " a:hover");
        style.addProperty("text-decoration", "underline");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PortalScreen.StyleSuffix.Navig));
        style.addProperty("width", "100%");
        style.addProperty("height", "9.2em");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PortalScreen.StyleSuffix.Center));
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PortalScreen.StyleSuffix.Main));
        style.addProperty("height", "100%");
        style.addProperty("margin", "0 " + rightColumnWidth + "px 0 " + leftColumnWidth + "px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PortalScreen.StyleSuffix.Content));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PortalScreen.StyleSuffix.Left));
        style.addProperty("float", "left");
        style.addProperty("width", leftColumnWidth + "px");
        style.addProperty("margin-left", "-100%");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PortalScreen.StyleSuffix.Right));
        style.addProperty("float", "left");
        style.addProperty("width", rightColumnWidth + "px");
        style.addProperty("margin-left", "-" + rightColumnWidth + "px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PortalScreen.StyleSuffix.Footer));
        style.addProperty("background-color", ThemeColors.OBJECT_TONE1);
        style.addProperty("clear", "left");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PortalScreen.StyleSuffix.Display));
        addStyle(style);

        style = new Style(Selector.valueOf(VistaLineSeparator.DEFAULT_STYLE_PREFIX));
        style.addProperty("border-top-width", "1px");
        style.addProperty("border-top-style", "dotted");
        style.addProperty("border-top-color", ThemeColors.OBJECT_TONE4);
        style.addProperty("margin-bottom", "0.3em");
        style.addProperty("width", "400px");
        addStyle(style);

        style = new Style(Selector.valueOf(VistaWidgetDecorator.DEFAULT_STYLE_PREFIX + StyleSuffix.Label));
        style.addProperty("padding-top", "2px");
        addStyle(style);

        String gwtButton = (".gwt-Button");
        List<Style> styles = getStyles(gwtButton);
        if (styles != null && styles.size() > 0) {
            for (Style st : styles) {
                if (st.getSelector().equals(gwtButton)) {
                    st.addProperty("border-radius", "5px");
                    st.addProperty("-moz-border-radius", "5px");
                    st.addProperty("background-color", ThemeColors.OBJECT_TONE50);
                    st.addProperty("border", "1px solid");
                    st.addProperty("border-color", "black");
                    st.addProperty("min-width", "100px");
                    break;
                }
            }

        }

    }

    protected void initListerStyles() {
        String prefix = ListerBase.DEFAULT_STYLE_PREFIX;

        Style style = new Style(Selector.valueOf(prefix));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, ListerBase.StyleSuffix.actionsPanel));
        style.addProperty("margin-top", "0.5em");
        style.addProperty("margin-bottom", "0.5em");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, ListerBase.StyleSuffix.filtersPanel));
        style.addProperty("padding-top", "0.5em");
        style.addProperty("background-color", ThemeColors.OBJECT_TONE15);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, ListerBase.StyleSuffix.listPanel));
        addStyle(style);
    }

    protected void initEntityDataTableStyles() {
        String prefix = DataTable.BASE_NAME;

        Style style = new Style(Selector.valueOf(prefix));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.Header));
        style.addProperty("background-color", ThemeColors.OBJECT_TONE35);
        style.addProperty("color", ThemeColors.OBJECT_TONE95);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.ColumnSelector));
        style.addProperty("background-color", ThemeColors.OBJECT_TONE60);
        style.addProperty("color", ThemeColors.OBJECT_TONE10);
        style.addProperty("font-weight", "bold");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.ColumnSelector) + " a:link, a:visited, a:active");
        style.addProperty("color", ThemeColors.OBJECT_TONE10);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.ColumnSelector) + ":hover");
        style.addProperty("background-color", ThemeColors.OBJECT_TONE80);
        style.addProperty("color", ThemeColors.OBJECT_TONE10);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.ColumnMenu));
        style.addProperty("background-color", ThemeColors.OBJECT_TONE10);
        style.addProperty("color", ThemeColors.OBJECT_TONE90);
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColors.OBJECT_TONE90);
        style.addProperty("padding", "5px 7px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.Row));
        style.addProperty("cursor", "pointer");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.Row, DataTable.StyleDependent.even));
        style.addProperty("background-color", ThemeColors.OBJECT_TONE15);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.Row, DataTable.StyleDependent.odd));
        style.addProperty("background-color", "white");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.Row, DataTable.StyleDependent.nodetails));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.Row, DataTable.StyleDependent.selected));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.ActionsBar));
        style.addProperty("background-color", ThemeColors.OBJECT_TONE30);
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColors.BORDER);
        addStyle(style);
    }

    private void initVistaNavigViewStyles() {
        String prefix = NavigViewImpl.DEFAULT_STYLE_PREFIX;

        Style style = new Style(Selector.valueOf(prefix));
        style.addProperty("width", "100%");
        style.addProperty("overflow", "hidden");
        style.addProperty("padding-bottom", "20px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, NavigViewImpl.StyleSuffix.Holder));
        style.addProperty("margin", "0");
        style.addProperty("padding", "0");
        style.addProperty("background-color", ThemeColors.OBJECT_TONE35);
        style.addProperty("list-style", "none");
        style.addProperty("overflow", "hidden");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, NavigViewImpl.StyleSuffix.Tab));
        style.addProperty("background-color", ThemeColors.OBJECT_TONE55);
        style.addProperty("margin-right", "4px");
        style.addProperty("border", "none");
        style.addProperty("border-top-left-radius", "8px");
        style.addProperty("border-top-right-radius", "8px");
        style.addProperty("-moz-border-radius-topleft", "8px");
        style.addProperty("-moz-border-radius-topright", "8px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, NavigViewImpl.StyleSuffix.Label, NavigViewImpl.StyleDependent.current));
        style.addProperty("background-color", ThemeColors.OBJECT_TONE95);
        style.addProperty("color", "white");
        style.addProperty("border-top-left-radius", "8px");
        style.addProperty("border-top-right-radius", "8px");
        style.addProperty("-moz-border-radius-topleft", "8px");
        style.addProperty("-moz-border-radius-topright", "8px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, NavigViewImpl.StyleSuffix.Label));
        style.addProperty("height", "50px");
        style.addProperty("line-height", "50px");
        style.addProperty("color", "white");
        style.addProperty("font-size", "14px");
        style.addProperty("font-style", "normal");
        //      style.addProperty("text-shadow", "0 -1px 0 #E6E6E6");
        style.addProperty("padding-left", "29px");
        style.addProperty("padding-right", "29px");
        addStyle(style);

    }

    private void initDecoratorsStyles() {
        String prefix = VistaHeaderBar.DEFAULT_STYLE_PREFIX;
        Style style = new Style(Selector.valueOf(prefix));
        style.addProperty("background-color", ThemeColors.OBJECT_TONE25);
        style.addProperty("margin-top", "10px");
        style.addProperty("padding-top", "10px");
        style.addProperty("padding-bottom", "10px");
        style.addProperty("padding-left", "20px");
        style.addProperty("margin-bottom", "10px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, VistaHeaderBar.StyleSuffix.Caption));
        style.addProperty("font-size", "14px");
        style.addProperty("font-weight", "bold");
        style.addProperty("color", "#888888");
        style.addProperty("padding-left", "20px");
        addStyle(style);

        prefix = VistaWidgetDecorator.DEFAULT_STYLE_PREFIX;
        style = new Style(Selector.valueOf(prefix));
        addStyle(style);

    }

    private void initCriteriaWidgetDecoratorStyles() {
        String prefix = CriteriaWidgetDecorator.DEFAULT_STYLE_PREFIX;

        Style style = new Style(Selector.valueOf(prefix));
        style.addProperty("padding", "10px");
        addStyle(style);
    }

    private void initTableDecorators() {

        String prefix = TableFolderDecorator.DEFAULT_STYLE_PREFIX;
        Style style = new Style(Selector.valueOf(prefix));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, TableFolderDecorator.StyleSuffix.Header));
        style.addProperty("font-size", "12px");
        style.addProperty("font-weight", "bold");
        style.addProperty("margin-top", "5px");
        style.addProperty("padding-bottom", "10px");
        style.addProperty("border-bottom", "solid 1px");
        style.addProperty("border-color", ThemeColors.SEPARATOR);
        addStyle(style);

    }

    private void initPropertyMarkerStyle() {
        String prefix = PropertiesMapWidget.PROPERTY_CARD_STYLE_PREFIX;
        Style style = new Style(Selector.valueOf(prefix));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PropertiesMapWidget.StyleSuffix.CardImage));
        style.addProperty("border-color", "#cccccc!important");
        style.addProperty("border", "solid 1px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PropertiesMapWidget.StyleSuffix.CardImage) + " img");
        style.addProperty("max-width", "100%!important");
        style.addProperty("width", "100%!important");
        style.addProperty("height", "100%!important");
        style.addProperty("-moz-background-size", "100% 100%!important");
        style.addProperty("-webkit-background-size", "100% 100%!important");
        style.addProperty("-khtml-background-size", "100% 100%!important");
        style.addProperty("background-size", "100% 100%!important");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PropertiesMapWidget.StyleSuffix.CardContent));
        style.addProperty("font-size", "12px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PropertiesMapWidget.StyleSuffix.CardContentItem));
        style.addProperty("border-bottom", "1px solid");
        style.addProperty("border-color", ThemeColors.SEPARATOR);
        style.addProperty("margin-bottom", "5px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PropertiesMapWidget.StyleSuffix.CardLeft));
        style.addProperty("font-size", "12px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PropertiesMapWidget.StyleSuffix.CardLeftItem));
        style.addProperty("font-weight", "bold");
        style.addProperty("margin-bottom", "5px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PropertiesMapWidget.StyleSuffix.CardMenuItem));
        style.addProperty("text-decoration", "none");
        style.addProperty("color", ThemeColors.OBJECT_TONE95);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PropertiesMapWidget.StyleSuffix.CardMenuItem) + ":hover");
        style.addProperty("text-decoration", "underline");
        style.addProperty("color", ThemeColors.OBJECT_TONE95);
        addStyle(style);

    }

    private void initPaymentRadioButtonGroupStyles() {

        String prefix = NewPaymentMethodForm.PAYMENT_BUTTONS_STYLE_PREFIX;

        Style style = new Style(Selector.valueOf(prefix));
        style.addProperty("margin-top", "10px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, NativeRadioGroup.StyleSuffix.Item));
        style.addProperty("width", "100%");
        style.addProperty("padding-top", "3px");
        style.addProperty("height", "27px");
        style.addProperty("border-top", "1px solid #F7F7F7");
        style.addProperty("border-bottom", "1px solid #F7F7F7");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, NativeRadioGroup.StyleSuffix.Item) + " input");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, NativeRadioGroup.StyleSuffix.Item) + " label");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, NativeRadioGroup.StyleSuffix.Item, NativeRadioGroup.StyleDependent.selected));
        style.addProperty("border-top", "1px solid #bbb");
        style.addProperty("border-bottom", "1px solid #bbb");
        style.addProperty("background-color", "white");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, NewPaymentMethodForm.StyleSuffix.PaymentForm));
        style.addProperty("border-radius", "5px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, NewPaymentMethodForm.StyleSuffix.PaymentImages));
        style.addProperty("margin-top", "10px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, NewPaymentMethodForm.StyleSuffix.PaymentImages) + " div");
        style.addProperty("height", "27px");
        style.addProperty("padding-top", "5px");
        style.addProperty("padding-right", "10px");
        style.addProperty("padding-left", "10px");
        style.addProperty("border-top-left-radius", "3px");
        style.addProperty("border-bottom-left-radius", "3px");
        style.addProperty("border-left", "1px solid #F7F7F7");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, NewPaymentMethodForm.StyleSuffix.PaymentImages) + " div.selected");
        style.addProperty("padding-top", "4px");
        style.addProperty("height", "26px");
        style.addProperty("border-top", "1px solid #bbb");
        style.addProperty("border-bottom", "1px solid #bbb");
        style.addProperty("border-left", "1px solid #bbb");
        style.addProperty("background-color", "white");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, NewPaymentMethodForm.StyleSuffix.PaymentImages) + " div img");
        style.addProperty("padding-left", "10px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, NewPaymentMethodForm.StyleSuffix.PaymentFee));
        style.addProperty("margin-top", "10px");
        style.addProperty("position", "relative");
        style.addProperty("z-index", "2");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, NewPaymentMethodForm.StyleSuffix.PaymentFee) + " div");
        style.addProperty("padding-right", "50px");
        style.addProperty("padding-top", "8px");
        style.addProperty("height", "24px");
        style.addProperty("padding-left", "50px");
        style.addProperty("padding-right", "20px");
        style.addProperty("z-index", "2");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, NewPaymentMethodForm.StyleSuffix.PaymentFee) + " div.selected");
        style.addProperty("padding-top", "7px");
        style.addProperty("height", "23px");
        style.addProperty("border-top", "1px solid #bbb");
        style.addProperty("border-bottom", "1px solid #bbb");
        style.addProperty("background-color", "white");
        addStyle(style);
    }
}
