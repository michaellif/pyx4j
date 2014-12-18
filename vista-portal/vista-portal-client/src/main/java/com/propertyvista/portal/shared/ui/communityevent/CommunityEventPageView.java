/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 14, 2011
 * @author Dad
 */
package com.propertyvista.portal.shared.ui.communityevent;

import com.pyx4j.commons.Key;

import com.propertyvista.domain.property.asset.CommunityEvent;
import com.propertyvista.portal.shared.ui.IEditorView;

public interface CommunityEventPageView extends IEditorView<CommunityEvent> {

    public interface CommunityEventPagePresenter extends IEditorPresenter<CommunityEvent> {
        void showEvent(Key caption);
    }

}
