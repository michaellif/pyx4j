/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-11
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.ob.client.views;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.misc.IMemento;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Label;

public class PmcAccountCreationCompleteViewImpl extends Composite implements PmcAccountCreationCompleteView {

    private static final I18n i18n = I18n.get(PmcAccountCreationCompleteViewImpl.class);

    private final Anchor redirectToCrmSite;

    public PmcAccountCreationCompleteViewImpl() {
        FlowPanel viewPanel = new FlowPanel();
        viewPanel.setSize("100%", "100%");

        Label completionCongratulationsMessage = new Label();
        completionCongratulationsMessage.setText(i18n.tr("Congratulations!"));
        viewPanel.add(completionCongratulationsMessage);

        redirectToCrmSite = new Anchor(i18n.tr("Go to your CRM!"));
        viewPanel.add(redirectToCrmSite);

        initWidget(viewPanel);
    }

    @Override
    public void setCrmSiteUrl(String crimeSiteUrl) {
        redirectToCrmSite.setHref(crimeSiteUrl);
    }

    @Override
    public IMemento getMemento() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void storeState(Place place) {
        // TODO Auto-generated method stub

    }

    @Override
    public void restoreState() {
        // TODO Auto-generated method stub

    }

    @Override
    public void showVisor(IsWidget widget, String caption) {
        // TODO Auto-generated method stub

    }

    @Override
    public void hideVisor() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isVisorShown() {
        // TODO Auto-generated method stub
        return false;
    }

}
