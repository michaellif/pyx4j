/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-08
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.resident.themes;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.commons.css.ThemeId;

import com.propertyvista.portal.resident.resources.tenantsure.TenantSureResources;

// TODO VISTA-3598 Clean up unnecessary styles
public class TenantSureTheme extends Theme {

    public enum StyleName implements IStyleName {//@formatter:off
        
        TenantSureLogo,
        TenantSureLogoPhone,
        TenantSureManagementGreetingPanel,
        TenantSureManagementGreeting,
        TenantSureManagementContentPanel,
        TenantSureManagementStatusDetailsPanel,
        TenantSureManagementActionsPanel,
        
        TenantSureDecoratorPanel,
        TenantSureDecoratorHeader,
        TenantSureContentPanel, 
        TenantSure2HighCourtLinks,
        TenantSureTermsLink,
        TenantSurePrivacyPolicyLink,
        TenantSureBillingAndCancellationsPolicyLink,        
        
        TenantSurePresonalDisclaimer,        
        TSPurchaseViewSection,
        TSPurchaseViewNextStepButton,
        TSPurchaseViewCancelButton,
        TSPucrhaseViewMessageText,
        TSUnavailableMessage,
        TSSendDocs,
        TSPurchaseViewError, 
        TSPaymentAmount,
        TenantSurePapAgreementPanel,
        
        TenantSureAboutContactInfo,
        
        TenantSureMessages,
        TenantSureMessage
        
    }//@formatter:on

    public TenantSureTheme() {
        initTenantSureCommonStyles();
        initTenantSureDecoratorStyles();
        initTenantSurePurchaseViewStyles();
        initTenantSureManagementViewStyles();
        initTenantSureAboutViewStyles();
        initSureFaqViewStyles();
        initOther();
    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

    private void initTenantSureDecoratorStyles() {
        Style style = null;

        style = new Style(".", StyleName.TenantSurePrivacyPolicyLink.name());
        style.addProperty("text-align", "right");
        addStyle(style);

        style = new Style(".", StyleName.TenantSureBillingAndCancellationsPolicyLink.name());
        style.addProperty("text-align", "left");
        addStyle(style);

        style = new Style(".", StyleName.TenantSureDecoratorHeader.name());
        style.addProperty("padding-bottom", "20px");
        style.addProperty("text-align", "center");
        addStyle(style);

        style = new Style(".", StyleName.TenantSure2HighCourtLinks.name());
        style.addProperty("margin-top", "20px");
        style.addProperty("padding-top", "20px");
        style.addProperty("text-align", "center");
        addStyle(style);
    }

    public void initTenantSurePurchaseViewStyles() {
        {
            Style style = new Style(".", TenantSureTheme.StyleName.TenantSurePresonalDisclaimer.name());
            style.addProperty("text-align", "justify");
            style.addProperty("padding-left", "20px");
            style.addProperty("padding-right", "20px");
            addStyle(style);
        }
        {
            Style style = new Style(".", TenantSureTheme.StyleName.TenantSurePresonalDisclaimer.name(), " ", "li");
            style.addProperty("text-align", "justify");
            style.addProperty("margin-left", "5%");
            style.addProperty("margin-right", "5%");
            addStyle(style);
        }
        {
            Style style = new Style(".", TenantSureTheme.StyleName.TSUnavailableMessage.name());
            style.addProperty("text-align", "center");
            addStyle(style);
        }
        {
            Style style = new Style("." + TenantSureTheme.StyleName.TSPurchaseViewSection.name());
            style.addProperty("margin-right", "20px");
            style.addProperty("margin-left", "20px");
            addStyle(style);
        }
        {
            Style style = new Style("." + TenantSureTheme.StyleName.TSPucrhaseViewMessageText.name());
            style.addProperty("margin-right", "auto");
            style.addProperty("margin-left", "auto");
            style.addProperty("text-align", "center");
            addStyle(style);
        }

        {
            Style style = new Style("." + TenantSureTheme.StyleName.TSPurchaseViewSection.name());
            style.addProperty("margin-right", "20px");
            style.addProperty("margin-left", "20px");
            addStyle(style);
        }
        {
            Style style = new Style("." + TenantSureTheme.StyleName.TSPurchaseViewNextStepButton.name());
            style.addProperty("float", "right");
            style.addProperty("margin-right", "20px");
            addStyle(style);
        }
        {
            Style style = new Style("." + TenantSureTheme.StyleName.TSPurchaseViewCancelButton.name());
            style.addProperty("float", "right");
            addStyle(style);
        }

        {
            Style style = new Style(".", TenantSureTheme.StyleName.TSSendDocs.name());
            style.addProperty("text-align", "center");
            style.addProperty("padding-top", "20px");
            style.addProperty("padding-bottom", "20px");
            addStyle(style);
        }

        {
            Style style = new Style(".", TenantSureTheme.StyleName.TSSendDocs.name(), " .Button");
            style.addProperty("float", "none");
            addStyle(style);
        }

        {
            Style style = new Style("." + TenantSureTheme.StyleName.TenantSurePapAgreementPanel.name(), " a");
            style.addProperty("color", ThemeColor.contrast2);
            addStyle(style);
        }

    }

    private void initTenantSureManagementViewStyles() {
        Style style = null;

        style = new Style(".", StyleName.TenantSureMessages.name());
        style.addProperty("margin-top", "30px");
        style.addProperty("margin-bottom", "30px");
        style.addProperty("text-align", "center");
        addStyle(style);

        style = new Style(".", StyleName.TenantSureMessage.name());
        addStyle(style);
    }

    public void initTenantSureAboutViewStyles() {
        {
            Style style = new Style("." + TenantSureTheme.StyleName.TenantSureAboutContactInfo.name());
            style.addProperty("marign-left", "auto");
            style.addProperty("marign-right", "auto");
            style.addProperty("padding-left", "20px");
            style.addProperty("padding-right", "20px");
            style.addProperty("padding-top", "20px");
            style.addProperty("text-align", "center");
            addStyle(style);
        }

        Style style;

        style = new Style(".", StyleName.TenantSureManagementGreetingPanel.name());
        addStyle(style);

        style = new Style(".", StyleName.TenantSureManagementGreetingPanel.name(), " ", ".", StyleName.TenantSureLogo.name());
        style.addProperty("display", "inline-block");
        style.addProperty("width", "auto");
        addStyle(style);

        style = new Style(".", StyleName.TenantSureManagementGreeting.name());
        style.addProperty("width", "100%");
        style.addProperty("height", "100%");
        style.addProperty("text-align", "center");
        style.addProperty("vertical-align", "middle");
        style.addProperty("display", "inline-block");
        addStyle(style);

        style = new Style(".", StyleName.TenantSureManagementContentPanel.name());
        style.addProperty("margin-top", "50px");
        style.addProperty("margin-bottom", "50px");
        style.addProperty("width", "100%");
        style.addProperty("height", "100%");
        addStyle(style);

        style = new Style(".", StyleName.TenantSureManagementStatusDetailsPanel.name());
        style.addProperty("display", "inline-block");
        style.addProperty("width", "50%");
        style.addProperty("vertical-align", "middle");
        addStyle(style);

        style = new Style(".", StyleName.TenantSureManagementActionsPanel.name());
        style.addProperty("display", "inline-block");
        style.addProperty("width", "50%");
        style.addProperty("vertical-align", "middle");
        style.addProperty("text-align", "center");
        addStyle(style);

        style = new Style(".", StyleName.TenantSureManagementActionsPanel.name(), " ", ".Anchor");
        style.addProperty("display", "block");
        style.addProperty("float", "none");
        style.addProperty("margin", "0px");
        style.addProperty("padding", "0px");
        addStyle(style);
    }

    public void initSureFaqViewStyles() {
        {
            Style style = new Style("." + TenantSureResources.FaqStyles.TenantSureFaqQnA.name());
            style.addProperty("margin-bottom", "20px");
            addStyle(style);
        }
        {
            Style style = new Style("." + TenantSureResources.FaqStyles.TenantSureFaqQ.name());
            style.addProperty("font-weight", "bold");
            addStyle(style);
        }

        {
            Style style = new Style("." + TenantSureResources.FaqStyles.TenantSureFaqA.name());
            style.addProperty("text-align", "justify");
            addStyle(style);
        }
    }

    public void initOther() {
        Style style = new Style("." + TenantSureTheme.StyleName.TSPaymentAmount.name());
        style.addProperty("width", "10em");
        addStyle(style);
    }

    private void initTenantSureCommonStyles() {

        Style style;

        style = new Style(".", StyleName.TenantSureLogo.name());
        addStyle(style);

        style = new Style(".", StyleName.TenantSureLogoPhone.name());
        style.addProperty("width", "100%");
        style.addProperty("display", "block");
        style.addProperty("text-align", "center");
        addStyle(style);

    }

}
