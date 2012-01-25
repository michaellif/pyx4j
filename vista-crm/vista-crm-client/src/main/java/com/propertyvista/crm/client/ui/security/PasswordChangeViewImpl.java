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
package com.propertyvista.crm.client.ui.security;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.HtmlUtils;
import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.rpc.PasswordChangeRequest;

import com.propertyvista.common.client.ui.components.login.PasswordEditorForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;

public class PasswordChangeViewImpl implements PasswordChangeView {

    private static final I18n i18n = I18n.get(PasswordChangeViewImpl.class);

    private Presenter presenter;

    private final PasswordEditorForm form;

    private final HTML userNameLabel;

    private final Panel panel;

    public PasswordChangeViewImpl() {
        FormFlexPanel content = new FormFlexPanel();
        content.getElement().getStyle().setPaddingTop(1, Unit.EM);
        int row = -1;
        userNameLabel = new HTML();
        content.setWidget(++row, 0, userNameLabel);
        content.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);
        form = new PasswordEditorForm(PasswordEditorForm.Type.CHANGE);
        form.initContent();
        form.setWidth("100%");
        content.setWidget(++row, 0, form);
        content.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);
        Button submitButton = new Button(i18n.tr("Submit"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.changePassword(form.getValue());
            }
        });
        submitButton.ensureDebugId(CrudDebugId.Criteria_Submit.toString()); // TODO why we need this???
        content.setWidget(++row, 0, submitButton);
        content.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);

        panel = new CrmScrollPanel(content);
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
    public void discard() {
        form.discard();
    }

}
