/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 15, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.personalinfo;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;

import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.portal.client.ui.residents.BasicViewImpl;
import com.propertyvista.portal.domain.dto.ResidentDTO;

public class PersonalInfoViewImpl extends BasicViewImpl<ResidentDTO> implements PersonalInfoView {

    public PersonalInfoViewImpl() {
        super(new PersonalInfoForm());
        getCancel().setVisible(false);

        Anchor resetPassword = new Anchor(i18n.tr("Reset Password"), new Command() {
            @Override
            public void execute() {
                ((PersonalInfoView.Presenter) getPresenter()).resetPassword();
            }
        });
        resetPassword.asWidget().getElement().getStyle().setMargin(10, Unit.PX);
        resetPassword.asWidget().getElement().getStyle().setMarginRight(30, Unit.PX);
        resetPassword.asWidget().getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
        resetPassword.asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLD);
        resetPassword.asWidget().getElement().getStyle().setColor("#F3931F");

        add(resetPassword);
    }
}
