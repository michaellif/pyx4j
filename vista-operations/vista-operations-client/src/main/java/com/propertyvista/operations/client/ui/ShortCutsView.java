/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 18, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.operations.client.ui;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.client.IsView;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.operations.client.activity.NavigFolder;

public interface ShortCutsView extends IsWidget, IsView {

    public interface ShortCutsPresenter {
    }

    public void setPresenter(ShortCutsPresenter presenter);

    public void setNavigationFolders(List<NavigFolder> folders);

    public void updateShortcutFolder(CrudAppPlace place, IEntity value);
}
