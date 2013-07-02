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
package com.propertyvista.portal.web.client.themes;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.commons.css.ThemeId;
import com.pyx4j.forms.client.ui.DefaultCComponentsTheme;
import com.pyx4j.forms.client.ui.datatable.DefaultDataTableTheme;
import com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme;
import com.pyx4j.forms.client.ui.folder.DefaultEntityFolderTheme;
import com.pyx4j.forms.client.ui.panels.DefaultFormFlexPanelTheme;
import com.pyx4j.site.client.ui.layout.responsive.ResponsiveLayoutTheme;
import com.pyx4j.widgets.client.DefaultWidgetsTheme;
import com.pyx4j.widgets.client.datepicker.DefaultDatePickerTheme;
import com.pyx4j.widgets.client.dialog.DefaultDialogTheme;

import com.propertyvista.common.client.theme.BillingTheme;
import com.propertyvista.common.client.theme.HorizontalAlignCenterMixin;
import com.propertyvista.common.client.theme.NewPaymentMethodEditorTheme;
import com.propertyvista.common.client.theme.TransactionHistoryViewerTheme;
import com.propertyvista.common.client.theme.VistaWizardPaneTheme;
import com.propertyvista.domain.site.SiteDescriptor.Skin;
import com.propertyvista.portal.web.client.resources.PortalImages;
import com.propertyvista.portal.web.client.ui.residents.tenantinsurance.dashboard.statusviewers.TenantInsuranceStatusViewer;
import com.propertyvista.portal.web.client.ui.residents.tenantinsurance.dashboard.statusviewers.TenantSureInsuranceStatusViewer;
import com.propertyvista.portal.web.client.ui.residents.tenantinsurance.views.ProvideTenantInsuranceViewImpl;

public class PortalWebTheme extends Theme {

    private final Skin skin;

    public PortalWebTheme(Skin skin) {
        this.skin = skin;
        initStyles();
    }

    protected void initStyles() {
        initGeneralStyles();
        initBodyStyles();
        initMenuBarStyles();

        initBackground();

        addTheme(new PortalWebRootPaneTheme());

        addTheme(new ResponsiveLayoutTheme());

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
        addTheme(new DefaultFormFlexPanelTheme() {
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
        addTheme(new DashboardTheme());
        addTheme(new TenantInsuranceTheme());
        addTheme(new TenantSureTheme());

        initTenantInsuranceStyles(); // TODO move this to a theme class
    }

    private void initBackground() {

        Style style = new Style(".", ResponsiveLayoutTheme.StyleName.ResponsiveLayoutMainHolder.name());
        style.addProperty("background-image", "url('" + PortalImages.INSTANCE.background().getSafeUri().asString() + "')");
        style.addProperty("background-size", "100% auto");
        addStyle(style);

        style = new Style(".", ResponsiveLayoutTheme.StyleName.ResponsiveLayoutContentBackground.name());
        style.addProperty("background-color", ThemeColor.background);
        style.addProperty("opacity", "0.9");
        addStyle(style);

        style = new Style(".", ResponsiveLayoutTheme.StyleName.ResponsiveLayoutFooterHolder.name());
        style.addProperty("background-color", ThemeColor.background);
        style.addProperty("opacity", "0.9");
        addStyle(style);
    }

    protected void initGeneralStyles() {
        Style style = new Style("html");
        style.addProperty("overflow", "hidden");
        addStyle(style);

        style = new Style("a");
        style.addProperty("text-decoration", "none");
        addStyle(style);

        style = new Style("a:hover");
        style.addProperty("text-decoration", "underline");
        addStyle(style);

        style = new Style("td");
        style.addProperty("padding", "0px");
        addStyle(style);

        style = new Style("p");
        style.addProperty("margin", "0.3em");
        addStyle(style);

        style = new Style("h1");
        style.addProperty("font-size", "2em");
        style.addProperty("line-height", "2.5em");
        style.addProperty("padding-bottom", "0.5px");
        style.addProperty("margin", "0");
        addStyle(style);
        style = new Style("h2");
        style.addProperty("font-size", "1.5em");
        style.addProperty("padding-bottom", "0.5px");
        style.addProperty("margin", "0");
        addStyle(style);
        style = new Style("h3");
        style.addProperty("font-size", "1.17em");
        style.addProperty("padding-bottom", "0.5px");
        style.addProperty("margin", "0");
        addStyle(style);
        style = new Style("h4, blockquote");
        style.addProperty("font-size", "1.12em");
        style.addProperty("padding-bottom", "0.3px");
        style.addProperty("margin", "0");
        addStyle(style);
        style = new Style("h5");
        style.addProperty("font-size", "1.08em");
        style.addProperty("padding-bottom", "0.2px");
        style.addProperty("margin", "0");
        addStyle(style);
        style = new Style("h6");
        style.addProperty("font-size", ".75em");
        style.addProperty("padding-bottom", "0.2px");
        style.addProperty("margin", "0");
        addStyle(style);

        style = new Style("h1, h2, h3, h4, h5, h6, b, strong");
        style.addProperty("font-weight", "bolder");
        addStyle(style);

        style = new Style("blockquote, ul, fieldset, form, ol, dl, dir, menu");
        style.addProperty("margin", "0");
        style.addProperty("padding", "0");
        addStyle(style);

        style = new Style("blockquote");
        style.addProperty("margin-left", "40px");
        style.addProperty("margin-right", "40px");
        addStyle(style);

        style = new Style(".", DefaultWidgetsTheme.StyleName.DropDownPanel);
        style.addProperty("z-index", "20");
        addStyle(style);

    }

    protected void initBodyStyles() {
        Style style = new Style("body");
        style.addProperty("background-color", "white");
        style.addProperty("color", ThemeColor.foreground);
        style.addProperty("margin", "0");
        style.addProperty("border", "none");
        style.addProperty("font-family", "'Lato', sans-serif");
        addStyle(style);

    }

    protected void initMenuBarStyles() {
        Style style = new Style(".gwt-MenuBar");
        style.addProperty("cursor", "pointer");
        style.addProperty("cursor", "hand");
        style.addProperty("color", ThemeColor.foreground, 0.2);
        addStyle(style);

        style = new Style(".gwt-MenuItem");
        style.addProperty("cursor", "pointer");
        style.addProperty("cursor", "hand");
        style.addProperty("background-color", ThemeColor.foreground, 0.2);
        style.addProperty("background", "transparent");
        style.addProperty("color", "#E5F0E1");
        style.addProperty("border", "0");
        addStyle(style);

        style = new Style(".gwt-MenuItem-selected");
        style.addProperty("background", ThemeColor.foreground, 0.8);
        style.addProperty("cursor", "pointer");
        style.addProperty("cursor", "hand");
        style.addProperty("text-decoration", "underline");
        style.addProperty("background", "transparent");
        addStyle(style);

        style = new Style(".gwt-MenuBar-vertical");
        style.addProperty("margin-top", "0px");
        style.addProperty("margin-left", "0px");
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColor.foreground, 0.2);
        style.addProperty("background", "#FFFFFF");
        addStyle(style);

        style = new Style(".gwt-MenuBar-vertical .gwt-MenuItem");
        style.addProperty("padding", "4px 14px 4px 1px");
        style.addProperty("color", "#666666");
        addStyle(style);

        style = new Style(".gwt-MenuBar-horizontal");
        addStyle(style);

        style = new Style(".gwt-MenuBar-horizontal .gwt-MenuItem");
        style.addProperty("vertical-align", "bottom");
        style.addProperty("background", "transparent");
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

    @Override
    public ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

}
