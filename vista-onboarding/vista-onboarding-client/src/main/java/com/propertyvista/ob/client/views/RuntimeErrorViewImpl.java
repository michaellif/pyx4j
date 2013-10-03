/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-20
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.ob.client.views;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.shared.domain.Notification;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.ob.client.themes.OnboardingStyles;

public class RuntimeErrorViewImpl extends Composite implements RuntimeErrorView {

    public static enum Styles implements IStyleName {
        RuntimeErrorTitle, RuntimeErrorMessage, RuntimeErrorAck;
    }

    private static final I18n i18n = I18n.get(RuntimeErrorViewImpl.class);

    private final FlowPanel panel;

    private final Label errorMessageLabel;

    private final Label errorMessageTitle;

    private final Anchor acnkowledgeError;

    private Presenter presenter;

    public RuntimeErrorViewImpl() {
        panel = new FlowPanel();
        panel.addStyleName(OnboardingStyles.VistaObView.name());

        errorMessageTitle = new Label();
        errorMessageTitle.addStyleName(Styles.RuntimeErrorTitle.name());
        panel.add(errorMessageTitle);

        errorMessageLabel = new Label();
        errorMessageLabel.addStyleName(Styles.RuntimeErrorMessage.name());
        panel.add(errorMessageLabel);

        acnkowledgeError = new Anchor(i18n.tr("Ok"));
        acnkowledgeError.addStyleName(Styles.RuntimeErrorAck.name());
        acnkowledgeError.getElement().getStyle().setDisplay(Display.BLOCK);
        acnkowledgeError.getElement().getStyle().setTextAlign(TextAlign.CENTER);
        acnkowledgeError.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.acknowledgeError();
            }
        });
        panel.add(acnkowledgeError);

        initWidget(panel);
    }

    @Override
    public void setErrorMessage(Notification errorMessage) {
        errorMessageTitle.setText(errorMessage.getTitle() == null || errorMessage.getTitle().isEmpty() ? i18n.tr("Error") : errorMessage.getTitle());
        errorMessageLabel.setText(errorMessage.getMessage());
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }
}
