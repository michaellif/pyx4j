package com.propertyvista.portal.web.client.resources.tenantsure;

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

    // TODO VISTA-3596: consider keeping this on server and fetching it via activity
    @Source("pre-authorized-payment-disclaimer.html")
    TextResource preAuthorizedPaymentDisclaimer();

    public enum FaqStyles implements IStyleName {

        TenantSureFaqQnA, TenantSureFaqQ, TenantSureFaqA

    }

    // TODO VISTA-3596: consider keeping this on server and fetching it via activity
    @Source("faq.html")
    TextResource faq();

    /** this is id of the anchor that should hold a link to privacy policy which is injected dynamically to the page upon population */
    public static final String PRIVACY_POLICY_ANCHOR_ID = "PrivacyPolicy";

    // TODO VISTA-3596: consider keeping this on server and fetching it via activity
    @Source("personal-disclaimer.html")
    TextResource personalDisclaimer();

    @Source("contact-info.html")
    TextResource contactInfo();

    @Source("management-panel-greeting.html")
    TextResource managementPanelGreeting();

}
