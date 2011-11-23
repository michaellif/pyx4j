/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 30, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.components.cms;

import java.text.ParseException;

import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.INativeEditableComponent;
import com.pyx4j.forms.client.ui.NativeTriggerComponent;

import com.propertyvista.domain.File;

public class NativeFileUploader extends NativeTriggerComponent<File> implements INativeEditableComponent<File> {

    private final Anchor hyperlink;

    public NativeFileUploader(final CFileUploader fileUploader) {
        super();
        hyperlink = new Anchor();
        construct(hyperlink);
        hyperlink.setHref("TestTest");
    }

    @Override
    public void setTabIndex(int tabIndex) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setFocus(boolean focused) {
        // TODO Auto-generated method stub

    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler focusHandler) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HandlerRegistration addBlurHandler(BlurHandler blurHandler) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setEnabled(boolean enabled) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isEnabled() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setVisible(boolean visible) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isVisible() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public CComponent<?, ?> getCComponent() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setWidth(String width) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setHeight(String height) {
        // TODO Auto-generated method stub

    }

    @Override
    public Widget asWidget() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setEditable(boolean editable) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isEditable() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setNativeValue(File value) {
        // TODO Auto-generated method stub

    }

    @Override
    public File getNativeValue() throws ParseException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setValid(boolean valid) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onTrigger(boolean show) {
        // TODO Auto-generated method stub

    }

}
