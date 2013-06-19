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
package com.propertyvista.portal.web.client.themes;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeId;

import com.propertyvista.portal.web.client.ui.residents.tenantinsurance.tenantsure.resources.TenantSureResources;
import com.propertyvista.portal.web.client.ui.residents.tenantinsurance.tenantsure.views.TenantSureAboutViewImpl;

public class TenantSureTheme extends Theme {

    public enum StyleName implements IStyleName {//@formatter:off
        TenantSureDecoratorPanel,
        TenantSureDecoratorHeader,
        TenantSureContentPanel, 
        TenantSureDecoratorFooter,
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
        TSPaymentAmount;
        
    }//@formatter:on

    public TenantSureTheme() {
        initDecoratorStyles();
        initTenantSurePurchaseViewStyles();
        initTenantSureAboutViewStyles();
        initSureFaqViewStyles();
        initOther();
    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

    private void initDecoratorStyles() {
        Style style = null;

        style = new Style(".", StyleName.TenantSureTermsLink.name());
        style.addProperty("text-align", "center");
        addStyle(style);

        style = new Style(".", StyleName.TenantSureDecoratorHeader.name());
        style.addProperty("padding-bottom", "20px");
        style.addProperty("text-align", "center");
        addStyle(style);

        style = new Style(".", StyleName.TenantSureDecoratorFooter.name());
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
            style.addProperty("margin-left", "50px");
            style.addProperty("margin-right", "50px");
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

    }

    public void initTenantSureAboutViewStyles() {
        {
            Style style = new Style("." + TenantSureAboutViewImpl.Styles.TenantSureAboutContactInfo.name());
            style.addProperty("marign-left", "auto");
            style.addProperty("marign-right", "auto");
            style.addProperty("padding-left", "20px");
            style.addProperty("padding-right", "20px");
            style.addProperty("padding-top", "20px");
            style.addProperty("text-align", "center");
            addStyle(style);
        }

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

}
