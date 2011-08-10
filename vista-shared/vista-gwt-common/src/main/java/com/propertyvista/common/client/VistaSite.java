/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 13, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.common.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

import com.pyx4j.commons.Consts;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.essentials.client.ApplicationCommon;
import com.pyx4j.forms.client.ImageFactory;
import com.pyx4j.log4gwt.client.ClientLogger;
import com.pyx4j.log4gwt.rpcappender.RPCAppender;
import com.pyx4j.log4gwt.shared.Level;
import com.pyx4j.site.client.AppPlaceDispatcher;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.shared.meta.SiteMap;
import com.pyx4j.widgets.client.CaptchaComposite;
import com.pyx4j.widgets.client.GlassPanel;

import com.propertyvista.common.client.resources.FormImageBundle;

public abstract class VistaSite extends AppSite {

    private Message message;

    public VistaSite(Class<? extends SiteMap> siteMapClass) {
        super(siteMapClass);
    }

    public VistaSite(Class<? extends SiteMap> siteMapClass, AppPlaceDispatcher dispatcher) {
        super(siteMapClass, dispatcher);
    }

    @Override
    public void onSiteLoad() {
        ImageFactory.setImageBundle((FormImageBundle) GWT.create(FormImageBundle.class));
        ApplicationCommon.initRpcGlassPanel();
        if (ApplicationMode.isDevelopment() && Window.Location.getParameter("trace") != null) {
            RPCAppender rpcAppender = new RPCAppender(Level.TRACE);
            rpcAppender.autoFlush(2 * Consts.SEC2MILLISECONDS);
            ClientLogger.addAppender(rpcAppender);
            ClientLogger.setTraceOn(true);
        } else {
            ClientLogger.addAppender(new RPCAppender(Level.WARN));
        }
        RootPanel.get().add(GlassPanel.instance());
        CaptchaComposite.setPublicKey("6LfVZMESAAAAAJaoJgKeTN_F9CKs6_-XGqG4nsth");

    }

    public static VistaSite instance() {
        return (VistaSite) AppSite.instance();
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    public abstract void showMessageDialog(String message, String title, String buttonText, Command command);
}
