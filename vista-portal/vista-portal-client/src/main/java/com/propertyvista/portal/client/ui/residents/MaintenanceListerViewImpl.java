/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 25, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.user.client.Command;

import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.site.client.ui.crud.ListerViewImplBasePanel;

import com.propertyvista.portal.client.ui.decorations.PortalHeaderDecorator;
import com.propertyvista.portal.domain.dto.MaintenanceRequestDTO;

public class MaintenanceListerViewImpl extends ListerViewImplBasePanel<MaintenanceRequestDTO> implements MaintenanceListerView {

    private static I18n i18n = I18nFactory.getI18n(MaintenanceListerViewImpl.class);

    public MaintenanceListerViewImpl() {
        super();
        CHyperlink systemStatus = new CHyperlink(null, new Command() {
            @Override
            public void execute() {
                //TODO finish
                // presenter.showSystemStatus();
            }
        });
        systemStatus.setValue(i18n.tr("System Status"));
        PortalHeaderDecorator header = new PortalHeaderDecorator(i18n.tr("Support Request History (to be finalized)"), "100%");
        header.addToTheRight(systemStatus);
        setHeader(header);
        setLister(new MaintenanceLister());
    }

}
