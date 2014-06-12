/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 24, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.extra;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import com.propertyvista.portal.rpc.portal.resident.dto.ExtraGadgetDTO;

public abstract class ExtraGadget<E extends ExtraGadgetDTO> extends FlowPanel {

    private final E gadgetDTO;

    public ExtraGadget(E gadgetDTO, String title) {
        this.gadgetDTO = gadgetDTO;

        HTML titleHTML = new HTML(title);

        add(titleHTML);
        add(createBody());
    }

    public E getGadgetDTO() {
        return gadgetDTO;
    }

    abstract protected Widget createBody();

}
