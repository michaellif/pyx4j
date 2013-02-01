package com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundleWithLookup;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.google.gwt.resources.client.TextResource;

import com.pyx4j.commons.css.IStyleName;

public interface TenantSureResources extends ClientBundleWithLookup {

    public static TenantSureResources INSTANCE = GWT.create(TenantSureResources.class);

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("logo-TenantSure-transparent-small.png")
    ImageResource logoTenantSure();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("logo-Highcourt-small.png")
    ImageResource logoHighcourt();

    @Source("pre-authorized-payment-disclaimer.html")
    TextResource preAuthorizedPaymentDisclaimer();

    public enum FaqStyles implements IStyleName {

        TenantSureFaqQnA, TenantSureFaqQ, TenantSureFaqA

    }

    @Source("faq.html")
    TextResource faq();

    public static final String PRIVACY_POLICY_ANCHOR_ID = "PrivacyPolicy";

    @Source("personal-disclaimer.html")
    TextResource personalDisclaimer();

    public enum PrivacyPolicyStyles implements IStyleName {
        TenantSurePrivacyPolicySection
    }

    @Source("privacy-policy.html")
    TextResource privacyPolicy();

    @Source("contact-info.html")
    TextResource contactInfo();

    @Source("management-panel-greeting.html")
    TextResource managementPanelGreeting();

}
