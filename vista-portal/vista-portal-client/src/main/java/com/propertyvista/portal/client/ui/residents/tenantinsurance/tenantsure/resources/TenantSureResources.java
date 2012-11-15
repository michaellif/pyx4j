package com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundleWithLookup;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.google.gwt.resources.client.TextResource;

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

}
