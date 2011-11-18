/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 4, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.maps;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.OkOption;

import com.propertyvista.portal.domain.dto.PropertyDTO;

public class MapDialog extends Dialog {

    private static I18n i18n = I18n.get(MapDialog.class);

    public MapDialog(PropertyDTO property) {
        super(i18n.tr("Location"), new OkOption() {

            @Override
            public boolean onClickOk() {
                return true;
            }
        });

        PropertyMapWidget map = PropertyMapWidget.get();
        map.populate(property);
        map.setSize("300px", "300px");

        setBody(map);

    }

}
