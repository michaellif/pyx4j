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
 * Created on 26-Sep-06
 * @author vlads
 */
package com.pyx4j.widgets.client.dialog;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.commons.css.CSSClass;
import com.pyx4j.gwt.commons.layout.ILayoutable;
import com.pyx4j.gwt.commons.layout.LayoutType;
import com.pyx4j.gwt.commons.ui.FlowPanel;
import com.pyx4j.gwt.commons.ui.HTML;
import com.pyx4j.gwt.commons.ui.SimplePanel;
import com.pyx4j.i18n.annotations.I18nComment;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Toolbar;

/**
 * Shared implementation for Modal Dialogs
 */
public class Dialog implements ProvidesResize, IsWidget {

    private static final Logger log = LoggerFactory.getLogger(Dialog.class);

    private static final I18n i18n = I18n.get(Dialog.class);

    private final PopupPanel popupPanel;

    private final FlowPanel container;

    private final CaptionPanel captionPanel;

    private FocusWidget firstFocusWidget;

    private Button defaultButton;

    private boolean enabledEnterKeyForDefaultButton = true;

    private boolean allowEnterKeyForDefaultButton;

    protected Widget buttonsPanel;

    private Button yesButton;

    private Button noButton;

    private Button okButton;

    private Button cancelButton;

    private Button closeButton;

    private Button custom1Button;

    private Button custom2Button;

    private Button custom3Button;

    private Button custom4Button;

    private DialogOptions options;

    private IsWidget body;

    private final ContentPanel content;

    private FocusHandler focusHandler;

    private BlurHandler blurHandler;

    // Handle focus for Stack of Dialogs, e.g. make proper focus on dialog bellow once the one above closed.
    private FocusWidget currentFocusWidget;

    private Element documentActiveElement;

    private HandlerRegistration closeHandlerRegistration;

    private static final List<Dialog> openDialogs = new Vector<Dialog>();

    private int dialogPixelWidth;

    private int dialogMaxHeight;

    public Dialog(String caption) {
        this(caption, null, null);
    }

    public Dialog(String caption, DialogOptions options, IsWidget body) {
        super();

        popupPanel = new PopupPanel(false, true);
        popupPanel.getElement().getStyle().setProperty("zIndex", "20");

        container = new FlowPanel();
        container.setStylePrimaryName(DialogTheme.StyleName.Dialog.name());
        container.getStyle().setProperty("cursor", "default");

        captionPanel = new CaptionPanel();
        container.add(captionPanel);

        popupPanel.setWidget(new SimplePanel(container));

        popupPanel.setGlassEnabled(true);
        setCaption(caption);

        content = new ContentPanel();

        container.add(content);

        setDialogOptions(options);

        setBody(body);

        Window.addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent event) {
                layout();
            }
        });

        setDialogPixelWidth(500);

    }

    protected int getActualDialogHeight() {
        return container.getOffsetHeight();
    }

    public void setCaption(String caption) {
        captionPanel.setHTML(caption);
    }

    public void setBody(IsWidget body) {
        this.body = body;
        if (body != null) {
            content.setBody(body);
        }
    }

    public void setDialogOptions(DialogOptions options) {
        this.options = options;
        buttonsPanel = createButtonsPanel();
        content.setOptions(buttonsPanel);

        this.addKeyDownHandler(new KeyDownHandler() {

            @Override
            public void onKeyDown(KeyDownEvent event) {
                if ((allowEnterKeyForDefaultButton) && enabledEnterKeyForDefaultButton && event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    // This does help for in Safari on Windows,
                    // or else other dialog shown after this would catch the keyCode be automatically closed.
                    event.preventDefault();
                    defaultButton.click();
                } else if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
                    event.preventDefault();
                    if (cancelButton != null) {
                        cancelButton.click();
                    } else if (closeButton != null) {
                        closeButton.click();
                    }
                }
            }
        });

        if (closeHandlerRegistration != null) {
            closeHandlerRegistration.removeHandler();
        }
        if (options instanceof CloseHandler) {
            closeHandlerRegistration = popupPanel.addCloseHandler((CloseHandler<PopupPanel>) options);
        }

    }

    private FlowPanel createButtonsPanel() {
        FlowPanel buttonsPanel = new FlowPanel();
        buttonsPanel.setStylePrimaryName(DialogTheme.StyleName.DialogButtonsPanel.name());

        Toolbar defaultButtonsToolbar = new Toolbar();
        defaultButtonsToolbar.setStylePrimaryName(DialogTheme.StyleName.DialogDefaultButtonsToolbar.name());
        buttonsPanel.add(defaultButtonsToolbar);

        Toolbar customButtonsToolbar = new Toolbar();
        customButtonsToolbar.setStylePrimaryName(DialogTheme.StyleName.DialogCustomButtonsToolbar.name());
        buttonsPanel.add(customButtonsToolbar);

        if (options instanceof Custom1Option) {
            custom1Button = createButton(((Custom1Option) options).custom1Text(), ((Custom1Option) options).getCustom1DebugID(), true, new Command() {
                @Override
                public void execute() {
                    if (((Custom1Option) options).onClickCustom1()) {
                        hide(false);
                    }
                }
            });
            customButtonsToolbar.addItem(custom1Button);
        }

        if (options instanceof Custom2Option) {
            custom2Button = createButton(((Custom2Option) options).custom2Text(), ((Custom2Option) options).getCustom2DebugID(), true, new Command() {
                @Override
                public void execute() {
                    if (((Custom2Option) options).onClickCustom2()) {
                        hide(false);
                    }
                }
            });
            customButtonsToolbar.addItem(custom2Button);
        }

        if (options instanceof Custom3Option) {
            custom3Button = createButton(((Custom3Option) options).custom3Text(), ((Custom3Option) options).getCustom3DebugID(), true, new Command() {
                @Override
                public void execute() {
                    if (((Custom3Option) options).onClickCustom3()) {
                        hide(false);
                    }
                }
            });
            customButtonsToolbar.addItem(custom3Button);
        }

        if (options instanceof Custom4Option) {
            custom4Button = createButton(((Custom4Option) options).custom4Text(), ((Custom4Option) options).getCustom4DebugID(), true, new Command() {
                @Override
                public void execute() {
                    if (((Custom4Option) options).onClickCustom4()) {
                        hide(false);
                    }
                }
            });
            customButtonsToolbar.addItem(custom4Button);
        }

        if (options instanceof YesOption) {
            yesButton = createButton(defaultYesText(), DialogDebugId.Dialog_Yes, true, new Command() {
                @Override
                public void execute() {
                    if (((YesOption) options).onClickYes()) {
                        hide(false);
                    }
                }
            });
            defaultButtonsToolbar.addItem(yesButton);
        }

        if (options instanceof NoOption) {
            noButton = createButton(defaultNoText(), DialogDebugId.Dialog_No, true, new Command() {
                @Override
                public void execute() {
                    if (((NoOption) options).onClickNo()) {
                        hide(false);
                    }
                }
            });
            defaultButtonsToolbar.addItem(noButton);
        }

        if (options instanceof OkOption) {
            okButton = createButton(optionTextOk(), DialogDebugId.Dialog_Ok, true, new Command() {
                @Override
                public void execute() {
                    if (((OkOption) options).onClickOk()) {
                        hide(false);
                    }
                }
            });
            defaultButtonsToolbar.addItem(okButton);
        }

        if (options instanceof CancelOption) {
            cancelButton = createButton(optionTextCancel(), DialogDebugId.Dialog_Cancel, true, new Command() {
                @Override
                public void execute() {
                    if (((CancelOption) options).onClickCancel()) {
                        hide(false);
                    }
                }
            });
            defaultButtonsToolbar.addItem(cancelButton);
        }

        if (options instanceof CloseOption) {
            closeButton = createButton(optionTextClose(), DialogDebugId.Dialog_Close, true, new Command() {
                @Override
                public void execute() {
                    if (((CloseOption) options).onClickClose()) {
                        hide(false);
                    }
                }
            });
            defaultButtonsToolbar.addItem(closeButton);
        }

        return buttonsPanel;
    }

    @I18nComment("As an answer to a question")
    private static final String defaultNoText() {
        return i18n.tr("No");
    }

    @I18nComment("As an answer to a question")
    private static final String defaultYesText() {
        return i18n.tr("Yes");
    }

    public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
        return popupPanel.addDomHandler(handler, KeyDownEvent.getType());
    }

    /**
     * This is alternative to using CustomOption. Override to change the text in dialog.
     *
     * @return text for 'OK' button
     */
    protected String optionTextOk() {
        if (options instanceof OkOptionText) {
            return ((OkOptionText) options).optionTextOk();
        } else {
            return i18n.tr("OK");
        }
    }

    /**
     * This is alternative to using CustomOption. Override to change the text in dialog.
     *
     * @return text for 'Cancel' button
     */
    protected String optionTextCancel() {
        if (options instanceof CancelOptionText) {
            return ((CancelOptionText) options).optionTextCancel();
        } else {
            return i18n.tr("Cancel");
        }
    }

    /**
     * This is alternative to using CustomOption. Override to change the text in dialog.
     *
     * @return text for 'Close' button
     */
    protected String optionTextClose() {
        return i18n.tr("Close");
    }

    private Button createButton(String text, IDebugId debugID, boolean canHaveFocus, Command buttonCommand) {
        Button button = new Button(text, buttonCommand);
        if (debugID == null) {
            button.ensureDebugId("Dialog." + text);
        } else {
            button.ensureDebugId(debugID.debugId());
        }
        button.getStyle().setMargin(3, Unit.PX);
        button.getStyle().setWhiteSpace(WhiteSpace.NOWRAP);
        if (canHaveFocus && defaultButton == null) {
            defaultButton = button;
        }
        return button;
    }

    @Override
    public boolean equals(Object other) {
        return (this == other);
    }

    public void setDialogPixelWidth(int width) {
        dialogPixelWidth = width;
        layout();
    }

    public int getContentMaxHeight() {
        return dialogMaxHeight - captionPanel.getOffsetHeight() - buttonsPanel.getOffsetHeight();
    }

    public void layout() {
        if (Window.getClientWidth() > dialogPixelWidth) {
            popupPanel.setWidth(dialogPixelWidth + "px");
        } else {
            popupPanel.setWidth(Window.getClientWidth() + "px");
        }

        dialogMaxHeight = (int) (Window.getClientHeight() * 0.9);
        popupPanel.getElement().getStyle().setPropertyPx("maxHeight", dialogMaxHeight);

        int left = Math.max(Window.getScrollLeft() + (Window.getClientWidth() - popupPanel.getOffsetWidth()) / 2, 0);
        popupPanel.getElement().getStyle().setPropertyPx("left", left);

        int top = Math.max(Window.getScrollTop() + (Window.getClientHeight() - popupPanel.getOffsetHeight()) / 2, 0);
        popupPanel.getElement().getStyle().setPropertyPx("top", top);

        onLayout(this, LayoutType.getLayoutType(dialogPixelWidth));
    }

    private static void onLayout(IsWidget widget, LayoutType layoutType) {
        if (widget instanceof ILayoutable) {
            ILayoutable component = (ILayoutable) widget;
            component.doLayout(layoutType);
        }
        if (widget instanceof HasWidgets) {
            for (Widget childWidget : (HasWidgets) widget) {
                onLayout(childWidget, layoutType);
            }
        }
    }

    public void show() {
        if (openDialogs.size() == 0) {
            documentActiveElement = getDocumentActiveElement();
        }

        if (!openDialogs.contains(this)) {
            openDialogs.add(this);
        }

        if (!popupPanel.isShowing()) {
            popupPanel.setVisible(false);
        }
        popupPanel.show();

        layout();

        popupPanel.setVisible(true);

        // The insides of Dialog may be CForm that is only initialized on show.
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                setupFocusManager();
                if (firstFocusWidget != null) {
                    firstFocusWidget.setFocus(true);
                    currentFocusWidget = firstFocusWidget;
                } else {
                    if (defaultButton != null) {
                        defaultButton.setFocus(true);
                    }
                }
            }
        });

        if (options instanceof OpenHandler) {
            ((OpenHandler) options).onOpen(new OpenEvent(this) {
            });
        }
    }

    public void hide(boolean autoClosed) {
        openDialogs.remove(this);
        popupPanel.hide(autoClosed);

        // Set proper focus in the Dialog blow just closed one.
        if (openDialogs.size() > 0) {
            final Dialog d = openDialogs.get(openDialogs.size() - 1);
            if (d.currentFocusWidget != null) {
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        if (d.currentFocusWidget != null) {
                            d.currentFocusWidget.setFocus(true);
                            log.trace("Focus requestd to {}", d.currentFocusWidget);
                        }
                    }
                });
            }
        } else {
            log.trace("Last dialog Closed");
            if (documentActiveElement != null) {
                try {
                    documentActiveElement.focus();
                } catch (Throwable ignore) {
                }
                documentActiveElement = null;
            }
        }
    }

    public final native Element getDocumentActiveElement() /*-{
		return $doc.activeElement;
    }-*/;

    public static void closeOpenDialogs() {
        for (int i = 0; i < openDialogs.size(); i++) {
            Dialog d = openDialogs.get(i);
            d.popupPanel.hide();
        }
        openDialogs.clear();
    }

    public static boolean isDialogOpen() {
        return openDialogs.size() > 0;
    }

    public void requestFocus(final FocusWidget focusWidget) {
        firstFocusWidget = focusWidget;
    }

    protected void setupFocusManager() {
        if (focusHandler == null) {
            focusHandler = new FocusHandler() {
                @Override
                public void onFocus(FocusEvent event) {
                    if (defaultButton != null) {
                        log.trace("Focus on {}", event.getSource().getClass());
                        boolean allowDefaultButton = ((event.getSource() instanceof TextBox) || (event.getSource() instanceof CheckBox))
                                || (event.getSource() instanceof ListBox);
                        allowEnterKeyForDefaultButton = allowDefaultButton;
                        if (allowDefaultButton) {
                            defaultButton.addStyleName(CSSClass.gwtButtonDefault.name());
                        } else {
                            defaultButton.removeStyleName(CSSClass.gwtButtonDefault.name());
                        }
                    }
                    if (event.getSource() instanceof FocusWidget) {
                        currentFocusWidget = (FocusWidget) event.getSource();
                    }
                }
            };
        }
        if (blurHandler == null) {
            blurHandler = new BlurHandler() {
                @Override
                public void onBlur(BlurEvent event) {
                    if (defaultButton != null) {
                        allowEnterKeyForDefaultButton = false;
                        defaultButton.removeStyleName(CSSClass.gwtButtonDefault.name());
                    }
                }

            };
        }
        attachFocusHandler(this.content.iterator());
    }

    private void attachFocusHandler(Iterator<Widget> iterator) {
        while (iterator.hasNext()) {
            Widget w = iterator.next();
            if (w instanceof FocusWidget) {
                ((FocusWidget) w).addFocusHandler(focusHandler);
                ((FocusWidget) w).addBlurHandler(blurHandler);
                if ((firstFocusWidget == null) && (!(w instanceof Button))) {
                    firstFocusWidget = (FocusWidget) w;
                }
            }
            if (w instanceof HasWidgets) {
                attachFocusHandler(((HasWidgets) w).iterator());
            }
        }
    }

    public boolean isEnterKeyForDefaultButton() {
        return enabledEnterKeyForDefaultButton;
    }

    public void setEnterKeyForDefaultButton(boolean allowEnterKeyForDefaultButton) {
        this.enabledEnterKeyForDefaultButton = allowEnterKeyForDefaultButton;
    }

    public Button getYesButton() {
        return yesButton;
    }

    public Button getNoButton() {
        return noButton;
    }

    public Button getOkButton() {
        return okButton;
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    public Button getCloseButton() {
        return closeButton;
    }

    public Button getCustom1Button() {
        return custom1Button;
    }

    public Button getCustom2Button() {
        return custom2Button;
    }

    public Button getCustom3Button() {
        return custom3Button;
    }

    public Button getCustom4Button() {
        return custom4Button;
    }

    class ContentPanel extends FlowPanel implements RequiresResize, ProvidesResize {

        private final SimplePanel bodyHolder;

        private final SimplePanel optionsHolder;

        public ContentPanel() {
            setStylePrimaryName(DialogTheme.StyleName.DialogContent.name());
            setSize("100%", "100%");

            bodyHolder = new SimplePanel();
            add(bodyHolder);

            optionsHolder = new SimplePanel();
            add(optionsHolder);
        }

        public void setBody(IsWidget body) {
            bodyHolder.setWidget(body);
        }

        public void setOptions(IsWidget options) {
            optionsHolder.setWidget(options);
        }

        @Override
        public void onResize() {
            for (Widget child : getChildren()) {
                if (child instanceof RequiresResize) {
                    ((RequiresResize) child).onResize();
                }
            }
        }

    }

    class CaptionPanel extends HTML {

        public CaptionPanel() {
            setWordWrap(false);
            setStylePrimaryName(DialogTheme.StyleName.DialogCaption.name());
            getElement().getStyle().setHeight(1.5, Unit.EM);
            getElement().getStyle().setLineHeight(1.5, Unit.EM);
        }

    }

    @Override
    public Widget asWidget() {
        return popupPanel;
    }

    public void addCloseHandler(CloseHandler<PopupPanel> handler) {
        popupPanel.addCloseHandler(handler);
    }

    public String getTitle() {
        return popupPanel.getTitle();
    }
}