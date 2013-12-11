/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-10-17
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.legal.n4;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.rpc.dto.legal.n4.N4DownloadSettingsDTO;
import com.propertyvista.crm.rpc.dto.legal.n4.N4GenerationDTO;

public class N4DownloadSettingsForm extends CEntityDecoratableForm<N4DownloadSettingsDTO> {

    private CComboBox<N4GenerationDTO> generationsBox;

    public N4DownloadSettingsForm() {
        super(N4DownloadSettingsDTO.class);
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel();
        int row = -1;

        generationsBox = new CComboBox<N4GenerationDTO>(CComboBox.NotInOptionsPolicy.DISCARD) {
            @Override
            public String getItemName(N4GenerationDTO o) {
                if (o != null) {
                    return o.groupId().getValue();
                } else {
                    return "";
                }
            }
        };
        generationsBox.setMandatory(true);
        panel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().selectedGeneration(), generationsBox)).componentWidth("300px").build());

        return panel;
    }

    public void setGenerations(List<N4GenerationDTO> generations) {
        generationsBox.setOptions(generations);
    }

}
