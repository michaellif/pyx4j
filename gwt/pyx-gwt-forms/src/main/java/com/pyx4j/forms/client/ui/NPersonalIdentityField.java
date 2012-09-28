package com.pyx4j.forms.client.ui;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.shared.IPersonalIdentity;

public class NPersonalIdentityField extends NTextBox<IPersonalIdentity> {

    public NPersonalIdentityField(CPersonalIdentityField cComponent) {
        super(cComponent);
    }

    @Override
    protected void onEditorCreate() {
        super.onEditorCreate();
        getEditor().addFocusHandler(new FocusHandler() {
            @Override
            public void onFocus(FocusEvent event) {
                // clear value for new input
                if (isEditable() && getCComponent().getValue() != null && !getCComponent().getValue().obfuscatedNumber().isNull()) {
                    getEditor().setText("");
                }
            }
        });
        getEditor().addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent event) {
                // clear value for new input
                if (isEditable() && CommonsStringUtils.isEmpty(getEditor().getText()) && getCComponent().getValue() != null) {
                    getCComponent().refresh(false);
                }
            }
        });
    }

    @Override
    protected void onViewerInit() {
        ((CPersonalIdentityField) getCComponent()).postprocess();
        super.onViewerInit();
    }
}
