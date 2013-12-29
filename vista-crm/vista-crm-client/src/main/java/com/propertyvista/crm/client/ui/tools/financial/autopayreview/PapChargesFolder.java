/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-09-18
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.financial.autopayreview;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.VistaBoxFolderItemDecorator;
import com.propertyvista.crm.client.ui.tools.financial.autopayreview.PapReviewFolder.Styles;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.PapChargeReviewDTO;

public final class PapChargesFolder extends VistaBoxFolder<PapChargeReviewDTO> {

    public PapChargesFolder() {
        super(PapChargeReviewDTO.class);
        setAddable(false);
        setRemovable(false);
        setOrderable(false);
        asWidget().addStyleName(Styles.AutoPayChargesFolder.name());
    }

    @Override
    public IFolderItemDecorator<PapChargeReviewDTO> createItemDecorator() {
        VistaBoxFolderItemDecorator<PapChargeReviewDTO> itemDecorator = (VistaBoxFolderItemDecorator<PapChargeReviewDTO>) PapChargesFolder.super
                .createItemDecorator();
        itemDecorator.setCollapsible(false);
        return itemDecorator;
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof PapChargeReviewDTO) {
            return new PapChargeReviewForm();
        }
        return super.create(member);
    }

}