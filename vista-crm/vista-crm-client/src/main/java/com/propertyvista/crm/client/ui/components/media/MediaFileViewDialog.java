/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 28, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.components.media;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.commons.Key;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.OkOption;

import com.propertyvista.common.client.ClentNavigUtils;
import com.propertyvista.portal.rpc.DeploymentConsts;
import com.propertyvista.portal.rpc.portal.ImageConsts;
import com.propertyvista.portal.rpc.portal.ImageConsts.ThumbnailSize;

public class MediaFileViewDialog extends VerticalPanel implements OkOption {

    String title;

    Key mediaId;

    public void show() {
        Dialog dialog = new Dialog(title, this);
        this.add(new Image(ClentNavigUtils.getDeploymentBaseURL() + DeploymentConsts.mediaImagesServletMapping + mediaId.toString() + "/"
                + ThumbnailSize.large.name() + "." + ImageConsts.THUMBNAIL_TYPE));
        dialog.setBody(this);
        dialog.show();
    }

    @Override
    public boolean onClickOk() {
        return true;
    }
}
