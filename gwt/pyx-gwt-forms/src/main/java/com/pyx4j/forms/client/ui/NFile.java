/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Jan 11, 2010
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import java.text.ParseException;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.entity.shared.IFile;
import com.pyx4j.forms.client.ImageFactory;
import com.pyx4j.gwt.client.upload.FileUploadDialog;
import com.pyx4j.gwt.client.upload.UploadReceiver;
import com.pyx4j.gwt.rpc.upload.UploadService;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.IWidget;

public class NFile extends NField<IFile<?>, NFile.ContentPanel, CFile, NFile.ContentPanel> {

    private static final I18n i18n = I18n.get(NFile.class);

    private final ContentPanel contentPanel;

    private final Button changeButton;

    private final Button clearButton;

    public NFile(final CFile file) {
        super(file);

        contentPanel = new ContentPanel();

        changeButton = new Button(ImageFactory.getImages().triggerDown(), new Command() {

            @Override
            public void execute() {
                showUploadFileDialog();
            }
        });
        setTriggerButton(changeButton);

        clearButton = new Button(ImageFactory.getImages().clear(), new Command() {

            @Override
            public void execute() {
                getCComponent().setValue(null);
            }
        });
        setClearButton(clearButton);

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void showUploadFileDialog() {
        UploadService<?, ?> service = getCComponent().getUploadService();
        new FileUploadDialog(i18n.tr("Upload Image File"), null, service, new UploadReceiver() {
            @Override
            public void onUploadComplete(IFile<?> uploadResponse) {
                getCComponent().setValue(uploadResponse);
            }
        }).show();
    }

    @Override
    public void setNativeValue(IFile<?> value) {
        contentPanel.setNativeValue(value);

    }

    @Override
    public IFile<?> getNativeValue() throws ParseException {
        assert false : "getNativeValue() shouldn't be called on Hyperlink";
        return null;
    }

    @Override
    protected ContentPanel createEditor() {
        return contentPanel;
    }

    @Override
    protected ContentPanel createViewer() {
        return contentPanel;
    }

    class ContentPanel extends FlowPanel implements IWidget {

        private final Anchor fileNameAnchor;

        private final Anchor uploadAnchor;

        public ContentPanel() {
            fileNameAnchor = new Anchor("", new Command() {

                @Override
                public void execute() {
                    Window.open(getCComponent().getImageUrl(), "_blank", null);
                }
            });
            add(fileNameAnchor);

            uploadAnchor = new Anchor(i18n.tr("Upload File ..."), new Command() {

                @Override
                public void execute() {
                    showUploadFileDialog();
                }
            });
            add(uploadAnchor);

        }

        public void setNativeValue(IFile<?> value) {

            if (value == null || value.isNull()) {
                clearButton.setVisible(false);
                uploadAnchor.setVisible(true);
                fileNameAnchor.setVisible(false);
                fileNameAnchor.setText("");
            } else {
                clearButton.setVisible(true);
                uploadAnchor.setVisible(false);
                fileNameAnchor.setVisible(true);

                String text = "";
                CFile comp = getCComponent();
                if (value != null) {
                    if (comp.getFormat() != null) {
                        text = comp.getFormat().format(value);
                    } else {
                        text = value.toString();
                    }
                }
                fileNameAnchor.setText(text);
            }

        }

        @Override
        public void setEnabled(boolean enabled) {
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void setEditable(boolean editable) {
        }

        @Override
        public boolean isEditable() {
            return true;
        }

    }

}
