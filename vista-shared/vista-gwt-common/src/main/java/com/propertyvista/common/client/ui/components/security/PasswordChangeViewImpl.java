/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 24, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.security;

import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.HtmlUtils;
import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.rpc.PasswordChangeRequest;
import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.common.client.ui.components.login.PasswordChangeForm;
import com.propertyvista.common.client.ui.decorations.DecorationUtils;

public class PasswordChangeViewImpl implements PasswordChangeView {

    private static final I18n i18n = I18n.get(PasswordChangeViewImpl.class);

    private Presenter presenter;

    private final PasswordChangeForm form;

    private final HTML userNameLabel;

    private final Panel panel;

    public PasswordChangeViewImpl() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();
        content.getElement().getStyle().setPaddingTop(1, Unit.EM);

        int row = -1;

        userNameLabel = new HTML();
        content.setWidget(++row, 0, userNameLabel);
        content.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);

        form = new PasswordChangeForm();
        form.initContent();
        form.asWidget().setWidth("100%");
        content.setWidget(++row, 0, form);
        content.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);

        FlowPanel footer = new FlowPanel();

        Button submitButton = new Button(i18n.tr("Submit"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (form.isValid()) {
                    presenter.changePassword(form.getValue());
                } else {
                    // hope that the form tells the user what's wrong.
                }
            }
        });
        submitButton.ensureDebugId(CrudDebugId.Criteria_Submit.toString());
        submitButton.asWidget().getElement().getStyle().setMargin(10, Unit.PX);
        submitButton.asWidget().getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
        footer.add(DecorationUtils.inline(submitButton));

        Anchor cancel = new Anchor(i18n.tr("Cancel"), new Command() {
            @Override
            public void execute() {
                presenter.cancel();
            }
        });
        cancel.asWidget().getElement().getStyle().setMargin(10, Unit.PX);
        cancel.asWidget().getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
        footer.add(DecorationUtils.inline(cancel));

        content.setWidget(++row, 0, footer);

        panel = new ScrollPanel(content);
    }

    @Override
    public Widget asWidget() {
        return panel;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
        form.populateNew();
    }

    @Override
    public void initialize(Key userPk, String userName) {
        PasswordChangeRequest newRequest = EntityFactory.create(PasswordChangeRequest.class);
        newRequest.userPk().setValue(userPk);
        form.populate(newRequest);

        if (userName != null) {
            userNameLabel.setVisible(true);
            userNameLabel.setHTML(HtmlUtils.h2(i18n.tr("Change password for {0}", new SafeHtmlBuilder().appendEscaped(userName).toSafeHtml().asString())));
        } else {
            userNameLabel.setVisible(false);
        }
    }

    @Override
    public PasswordChangeRequest getValue() {
        return form.getValue();
    }

    @Override
    public void reset() {
        form.reset();
    }

    @Override
    public void setAskForCurrentPassword(boolean isCurrentPasswordRequired) {
        form.setAskForCurrentPassword(isCurrentPasswordRequired);
    }

    @Override
    public void setAskForRequireChangePasswordOnNextSignIn(boolean isRequireChangePasswordOnNextSignInRequired) {
        form.setAskForRequireChangePasswordOnNextSignIn(isRequireChangePasswordOnNextSignInRequired);
    }

    @Override
    public void setDictionary(List<String> dictionary) {
        form.setDictionary(dictionary);
    }

}
