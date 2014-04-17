/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 6, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.administration.ils;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.site.client.ui.prime.form.AccessoryEntityForm;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.settings.ILSEmailConfig;

public class ILSEmailConfigEditorFolder extends VistaBoxFolder<ILSEmailConfig> {

    public ILSEmailConfigEditorFolder() {
        super(ILSEmailConfig.class);
    }

    @Override
    public <T extends CComponent<T, ?>> T create(IObject<?> member) {
        if (member instanceof ILSEmailConfig) {
            return (T) new ILSEmailConfigEditor();
        } else {
            return super.create(member);
        }
    }

    class ILSEmailConfigEditor extends AccessoryEntityForm<ILSEmailConfig> {

        public ILSEmailConfigEditor() {
            super(ILSEmailConfig.class);
        }

        @Override
        protected IsWidget createContent() {
            TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();
            int row = -1;

            content.setWidget(++row, 0, 2, injectAndDecorate(proto().frequency(), 10, true));
            content.setWidget(++row, 0, 2, injectAndDecorate(proto().email(), 20, true));
            content.setWidget(++row, 0, 2, injectAndDecorate(proto().maxDailyAds(), 10, true));

            return content;
        }
    }
}
