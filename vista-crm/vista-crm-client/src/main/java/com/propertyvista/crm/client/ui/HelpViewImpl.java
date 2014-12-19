/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 18, 2011
 * @author Dad
 */
package com.propertyvista.crm.client.ui;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.common.client.WalkMe;
import com.propertyvista.common.client.theme.SiteViewTheme;

public class HelpViewImpl extends FlowPanel implements HelpView {

    private static final Logger log = LoggerFactory.getLogger(HelpViewImpl.class);

    private HelpPresenter presenter;

    final FlowPanel context;

    public HelpViewImpl() {
        super();
        setStyleName(SiteViewTheme.StyleName.SiteViewExtra.name());

        this.context = new FlowPanel();
        ScrollPanel scrollPanel = new ScrollPanel(context);
        scrollPanel.setAlwaysShowScrollBars(false);
        this.add(scrollPanel);

    }

    @Override
    public void setPresenter(final HelpPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void updateContextHelp() {
        WalkMe.obtainWalkthrus("vista-help", new AsyncCallback<Map<Integer, String>>() {

            @Override
            public void onFailure(Throwable caught) {
                // ignore
                context.clear();
            }

            @Override
            public void onSuccess(Map<Integer, String> result) {
                context.clear();
                for (final Map.Entry<Integer, String> me : result.entrySet()) {
                    // Use this list to configure Permission
                    log.debug("got WalkThru {} '{}'", me.getKey(), me.getValue());

                    Anchor anchor = new Anchor(me.getValue(), false);
                    anchor.setStyleName(SiteViewTheme.StyleName.SiteViewExtraItem.name());
                    anchor.getElement().getStyle().setTextAlign(TextAlign.LEFT);
                    anchor.getElement().getStyle().setDisplay(Display.BLOCK);
                    anchor.addClickHandler(new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent event) {
                            log.debug("call WalkThru {} '{}'", me.getKey(), me.getValue());
                            WalkMe.startWalkthruById(me.getKey());
                        }
                    });
                    context.add(anchor);
                }
            }
        });
    }

}
