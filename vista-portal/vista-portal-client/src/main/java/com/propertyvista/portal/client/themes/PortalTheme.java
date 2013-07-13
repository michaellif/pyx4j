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

import com.pyx4j.commons.css.Selector;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.DefaultCComponentsTheme;
import com.pyx4j.forms.client.ui.datatable.DefaultDataTableTheme;
import com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme;
import com.pyx4j.forms.client.ui.folder.DefaultEntityFolderTheme;
import com.pyx4j.forms.client.ui.panels.FlexFormPanelTheme;
import com.pyx4j.widgets.client.DefaultWidgetsTheme;
import com.pyx4j.widgets.client.datepicker.DefaultDatePickerTheme;
import com.pyx4j.widgets.client.dialog.DefaultDialogTheme;

import com.propertyvista.common.client.theme.BillingTheme;
import com.propertyvista.common.client.theme.HorizontalAlignCenterMixin;
import com.propertyvista.common.client.theme.NewPaymentMethodEditorTheme;
import com.propertyvista.common.client.theme.TransactionHistoryViewerTheme;
import com.propertyvista.common.client.theme.VistaTheme;
import com.propertyvista.common.client.theme.VistaWizardPaneTheme;
import com.propertyvista.domain.site.SiteDescriptor.Skin;
import com.propertyvista.portal.client.ui.PortalRootPane;
import com.propertyvista.portal.client.ui.maps.PropertiesMapWidget;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.dashboard.statusviewers.TenantInsuranceStatusViewer;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.dashboard.statusviewers.TenantSureInsuranceStatusViewer;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.views.ProvideTenantInsuranceViewImpl;

public class PortalTheme extends VistaTheme {

    private final Skin skin;

    public PortalTheme(Skin skin) {
        this.skin = skin;
        initStyles();

    }

    protected void initStyles() {
        super.initGeneralStyles();

        addTheme(new HorizontalAlignCenterMixin());

        addTheme(new DefaultWidgetsTheme());
        addTheme(new DefaultWidgetDecoratorTheme() {
            @Override
            protected void initStyles() {
                super.initStyles();

                Style style = new Style(".", StyleName.WidgetDecorator);
                style.addProperty("margin", "6px 0");
                addStyle(style);

            }

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

            @Override
            protected void initStyles() {
                super.initStyles();

                Style style = new Style(".", StyleName.FormFlexPanelH1);
                style.addProperty("margin", "0");
                addStyle(style);

                style = new Style(".", StyleName.FormFlexPanelH1Label);
                style.addProperty("color", getBackgroundColor(), 1.4);
                style.addProperty("padding", "20px");
                style.addProperty("font-size", "1.3em");
                addStyle(style);

                style = new Style(".", StyleName.FormFlexPanelActionWidget);
                style.addProperty("margin-top", "20px");
                addStyle(style);

                style = new Style(".", StyleName.FormFlexPanelActionWidget, " a");
                style.addProperty("color", getBackgroundColor(), 1.4);
                style.addProperty("font-style", "italic");
                addStyle(style);

                if (Skin.skin1.equals(skin)) {
                    style = new Style(".", StyleName.FormFlexPanelH1);
                    style.addProperty("border-top", "solid 1px");
                    style.addProperty("border-bottom", "solid 1px");
                    style.addProperty("border-top-color", ThemeColor.foreground, 0.3);
                    style.addProperty("border-bottom-color", ThemeColor.foreground, 0.5);
                    style.addGradient(ThemeColor.foreground, 0.1, ThemeColor.foreground, 0.4);
                    addStyle(style);
                } else if (Skin.skin2.equals(skin) || Skin.skin3.equals(skin)) {
                    style = new Style(".", StyleName.FormFlexPanelH1);
                    style.addGradient(ThemeColor.foreground, 0.2, ThemeColor.foreground, 0.2);
                    addStyle(style);
                }
            }
        });

        //.vista-pmsite-residentPage .content

//        addTheme(new DefaultPaneTheme());
        addTheme(new DefaultDataTableTheme());
        addTheme(new DefaultCComponentsTheme());
        addTheme(new DefaultDatePickerTheme());
        addTheme(new DefaultEntityFolderTheme() {
            @Override
            protected ThemeColor getBackgroundColor() {
                return ThemeColor.foreground;
            }

            @Override
            protected void initStyles() {
                super.initStyles();
                Style style = new Style(".", StyleName.EntityFolderTableHeader);
                style.addProperty("width", "100%");
                style.addProperty("background-color", ThemeColor.foreground, 0.1);
                style.addProperty("line-height", "35px");
                style.addProperty("color", ThemeColor.foreground, 0.7);
                style.addProperty("border", "none");
                addStyle(style);

                style = new Style(".", StyleName.EntityFolderRowItemDecorator);
                style.addProperty("height", "35px");
                style.addProperty("border-bottom", "dotted 1px");
                style.addProperty("border-bottom-color", ThemeColor.foreground, 0.7);
                addStyle(style);

            }
        });
        addTheme(new DefaultDialogTheme());

        addTheme(new VistaWizardPaneTheme());
        addTheme(new TenantDashboardTheme());
        addTheme(new CommunicationCenterTheme());
        addTheme(new NewPaymentMethodEditorTheme());
        addTheme(new BillingTheme());
        addTheme(new TransactionHistoryViewerTheme());
        addTheme(new LandingPagesTheme());
        addTheme(new TenantInsuranceTheme());
        addTheme(new TenantSureTheme());

        initCellListStyle();
        initCheckBoxStyle();
        initHyperlinkStyle();
        initGroupBoxStyle();
        initSiteViewStyles();
        initPropertyMarkerStyle();
        initSuggestBoxStyle();
        initMessageStyles();

        initTenantInsuranceStyles(); // TODO move this to a theme class
    }

    protected void initSiteViewStyles() {
        String prefix = PortalRootPane.DEFAULT_STYLE_PREFIX;

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

        style = new Style(Selector.valueOf(prefix, PortalRootPane.StyleSuffix.Display));
        addStyle(style);

        String gwtButton = (".gwt-Button");
        List<Style> styles = getStyles(gwtButton);
        if (styles != null && styles.size() > 0) {
            for (Style st : styles) {
                if (st.getSelector().equals(gwtButton)) {
                    st.addProperty("border-radius", "5px");
                    st.addProperty("-moz-border-radius", "5px");
                    st.addProperty("background-color", ThemeColor.object1, 0.5);
                    st.addProperty("border", "1px solid");
                    st.addProperty("border-color", "black");
                    st.addProperty("min-width", "100px");
                    break;
                }
            }

        }

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
        style.addProperty("border-color", ThemeColor.object1, 0.4);
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
        style.addProperty("color", ThemeColor.object1, 0.95);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PropertiesMapWidget.StyleSuffix.CardMenuItem) + ":hover");
        style.addProperty("text-decoration", "underline");
        style.addProperty("color", ThemeColor.object1, 0.95);
        addStyle(style);

    }

    private void initTenantInsuranceStyles() {

        // TODO move to TenantDashboardTheme?
        // Dashboard ******************************************************************************************************************************************
        {
            Style style = new Style("." + TenantInsuranceStatusViewer.Styles.TenantInsuranceWarningText.name());
            style.addProperty("color", "#AA0000");
            style.addProperty("width", "100%");
            style.addProperty("text-align", "center");
            style.addProperty("font-size", "16px");
            style.addProperty("font-weight", "bold");
            style.addProperty("font-style", "normal");
            style.addProperty("padding-top", "0.5em");
            addStyle(style);
        }

        {
            Style style = new Style("." + TenantInsuranceStatusViewer.Styles.TenantInsuranceAnchor.name());
            style.addProperty("display", "block");
            style.addProperty("margin-left", "auto");
            style.addProperty("margin-right", "auto");
            style.addProperty("width", "100%");
            style.addProperty("text-align", "center");
            style.addProperty("padding-top", "0.5em");
            addStyle(style);
        }

        {
            Style style = new Style("." + TenantSureInsuranceStatusViewer.STYLE_PREFIX + TenantSureInsuranceStatusViewer.StyleSuffix.TenantSureLogo);
            style.addProperty("display", "block");
            style.addProperty("margin-left", "auto");
            style.addProperty("margin-right", "auto");
            style.addProperty("text-align", "center");
            style.addProperty("margin-top", "10px");
            style.addProperty("margin-bottom", "10px");
            addStyle(style);
        }

        // ProvideTenantInsuranceView **************************************************************************************************************************

        {
            Style style = new Style("." + ProvideTenantInsuranceViewImpl.Styles.ProvideTIRequirements.name());
            style.addProperty("width", "53em");
            style.addProperty("text-align", "justify");
            style.addProperty("font-weight", "normal");
            style.addProperty("font-style", "normal");
            style.addProperty("margin-left", "auto");
            style.addProperty("margin-right", "auto");
            style.addProperty("margin-top", "30px");
            style.addProperty("margin-bottom", "40px");
            addStyle(style);
        }

        {
            Style style = new Style("." + ProvideTenantInsuranceViewImpl.Styles.ProvideTIInsuranceStatus.name());
            style.addProperty("text-align", "center");
            style.addProperty("font-weight", "bold");
            style.addProperty("font-style", "normal");
            style.addProperty("margin-left", "auto");
            style.addProperty("margin-right", "auto");
            style.addProperty("margin-top", "10px");
            style.addProperty("margin-bottom", "50px");
            addStyle(style);
        }

        {
            Style style = new Style("." + ProvideTenantInsuranceViewImpl.Styles.ProvideTITenantSureLogo.name());
            style.addProperty("display", "block");
            style.addProperty("margin-left", "auto");
            style.addProperty("margin-right", "auto");
            style.addProperty("width", "100%");
            style.addProperty("text-align", "center");
            style.addProperty("padding-top", "0.5em");
            style.addProperty("padding-bottom", "2em");
            addStyle(style);
        }

        {
            Style style = new Style("." + ProvideTenantInsuranceViewImpl.Styles.ProvideTIBGetTenantSure.name());
            style.addProperty("margin-left", "20px");
            style.addProperty("width", "20em");
            style.addProperty("padding", "0.40em");
            style.addProperty("font-size", "18px");
            style.addProperty("text-align", "center");
            style.addProperty("font-style", "bold");

            addStyle(style);
        }

        {
            Style style = new Style("." + ProvideTenantInsuranceViewImpl.Styles.ProvideTIUpdateExisitingInsurance.name());
            style.addProperty("display", "block");
            style.addProperty("margin-left", "auto");
            style.addProperty("margin-right", "auto");
            style.addProperty("width", "100%");
            style.addProperty("text-align", "center");
            style.addProperty("padding-top", "0.5em");
            addStyle(style);
        }

    }

}
