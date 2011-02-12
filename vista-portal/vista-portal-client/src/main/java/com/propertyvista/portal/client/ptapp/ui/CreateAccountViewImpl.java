/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id: VistaTesterDispatcher.java 32 2011-02-02 04:49:39Z vlads $
 */
package com.propertyvista.portal.client.ptapp.ui;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.propertyvista.portal.rpc.pt.AccountCreationRequest;

import com.pyx4j.commons.GWTJava5Helper;
import com.pyx4j.commons.StringDebugId;
import com.pyx4j.entity.client.ui.BaseEditableComponentFactory;
import com.pyx4j.entity.client.ui.flex.EntityBinder;
import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CForm.LabelAlignment;
import com.pyx4j.forms.client.ui.CPasswordTextField;

public class CreateAccountViewImpl extends VerticalPanel implements CreateAccountView {

    private static I18n i18n = I18nFactory.getI18n(CreateAccountViewImpl.class);

    private Presenter presenter;

    public CreateAccountViewImpl() {

        //        EntityBinder<AccountCreationRequest> ep = EntityBinder.create(AccountCreationRequest.class, new BaseEditableComponentFactory());
        //
        //        ep.create(ep.proto().email());
        //
        //        CPasswordTextField password = new CPasswordTextField();
        //        ep.bind(password, ep.proto().password());
        //
        //        CComponent<?>[][] components = new CComponent[][] {
        //
        //        { ep.get(ep.proto().email()) },
        //
        //        { password },
        //
        //        };
        //
        //        Widget searchForm = CForm.createFormWidget(LabelAlignment.TOP, components, new StringDebugId(GWTJava5Helper.getSimpleName(CreateAccountView.class)));
        //        add(searchForm);

        Button viewButton = new Button("Let's Start");
        viewButton.ensureDebugId(CrudDebugId.Criteria_Submit.toString());
        viewButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
            }

        });
        viewButton.getElement().getStyle().setProperty("margin", "3px 20px 3px 8px");
        add(viewButton);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

}
