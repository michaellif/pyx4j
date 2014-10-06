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
 * @version $Id$
 */
package com.propertyvista.crm.client.ui;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Label;
import com.pyx4j.widgets.client.RadioGroup;
import com.pyx4j.widgets.client.TextBox;
import com.pyx4j.widgets.client.style.theme.WidgetTheme;

import com.propertyvista.common.client.theme.SiteViewTheme;

public class FeedbackViewImpl extends FlowPanel implements FeedbackView {

    private static final Logger log = LoggerFactory.getLogger(FeedbackViewImpl.class);

    private static final I18n i18n = I18n.get(FeedbackViewImpl.class);

    private FeedbackPresenter presenter;

    final FlowPanel context;

    public FeedbackViewImpl() {
        super();
        setStyleName(SiteViewTheme.StyleName.SiteViewExtra.name());

        this.context = new FlowPanel();
        ScrollPanel scrollPanel = new ScrollPanel(context);
        scrollPanel.setAlwaysShowScrollBars(false);
        createContent();
        this.add(scrollPanel);

    }

    @Override
    public void setPresenter(final FeedbackPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void refreshFeedback() {

    }

    private void createContent() {

        Label header = new Label(i18n.tr("Help us shape the MyCommunity portal (or PropertyVista CRM). Your feedback is really important to us."));
        header.setStyleName(WidgetTheme.StyleName.Label.name());
        header.getElement().getStyle().setFontSize(14, Unit.PX);
        header.getElement().getStyle().setTextAlign(TextAlign.LEFT);
        header.getElement().getStyle().setPaddingTop(14, Unit.PX);
        context.add(header);

        Label experience = new Label(i18n.tr("1. What has been your experience of using MyCommunity portal so far?"));
        experience.setStyleName(WidgetTheme.StyleName.Label.name());
        experience.getElement().getStyle().setPaddingTop(14, Unit.PX);
        experience.getElement().getStyle().setFontSize(14, Unit.PX);
        experience.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        experience.getElement().getStyle().setTextAlign(TextAlign.LEFT);
        context.add(experience);

        Label labelRadioGroup = new Label(i18n.tr(" What has been your experience of using MyCommunity portal so far?"));
        labelRadioGroup.setStyleName(WidgetTheme.StyleName.Label.name());
        labelRadioGroup.getElement().getStyle().setTextAlign(TextAlign.LEFT);
        context.add(labelRadioGroup);

        RadioGroup<String> rg = new RadioGroup<String>(RadioGroup.Layout.VERTICAL);
        rg.setOptions(Arrays.asList("I like it", "I dislike it", "I neither like nor dislike it"));
        context.add(rg);

        Label labelLike = new Label(i18n.tr("2. Please tell us more about what you like about MyCommunity portal?"));
        labelLike.setStyleName(WidgetTheme.StyleName.Label.name());
        labelLike.getElement().getStyle().setFontSize(14, Unit.PX);
        labelLike.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        labelLike.getElement().getStyle().setTextAlign(TextAlign.LEFT);
        labelLike.getElement().getStyle().setPaddingTop(14, Unit.PX);
        context.add(labelLike);
        TextBox textLike = new TextBox();
        context.add(textLike);

        Label labelDislike = new Label(i18n.tr("3. Please tell us more about what you dislike about MyCommunity portal?"));
        labelDislike.setStyleName(WidgetTheme.StyleName.Label.name());
        labelDislike.getElement().getStyle().setFontSize(14, Unit.PX);
        labelDislike.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        labelDislike.getElement().getStyle().setTextAlign(TextAlign.LEFT);
        labelDislike.getElement().getStyle().setPaddingTop(14, Unit.PX);
        context.add(labelDislike);
        TextBox textDislike = new TextBox();
        context.add(textDislike);

        Label labelDevice = new Label(i18n.tr("4. What device are you viewing MyCommunity portal on?"));
        labelDevice.setStyleName(WidgetTheme.StyleName.Label.name());
        labelDevice.getElement().getStyle().setFontSize(14, Unit.PX);
        labelDevice.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        labelDevice.getElement().getStyle().setTextAlign(TextAlign.LEFT);
        labelDislike.getElement().getStyle().setPaddingTop(14, Unit.PX);
        context.add(labelDevice);

    }
}
