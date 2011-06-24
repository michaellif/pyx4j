package com.propertyvista.portal.client.ui;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.commons.StringDebugId;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.widgets.client.style.IStyleSuffix;

public class TopRightActionsViewImpl extends FlowPanel implements TopRightActionsView {

    private static I18n i18n = I18nFactory.getI18n(TopRightActionsViewImpl.class);

//    private static final String GREETING_MESSAGE = i18n.tr("Welcome");

    private Presenter presenter;

    private final CHyperlink logout;

    private final Label greetings;

    public static String DEFAULT_STYLE_PREFIX = "TopRightActionsViewImpl";

    public static enum StyleSuffix implements IStyleSuffix {
        PhoneLabel, GreetingLabel
    }

    public TopRightActionsViewImpl() {
        setStyleName(DEFAULT_STYLE_PREFIX);
        setSize("100%", "100%");

        FlowPanel topLinksPanel = new FlowPanel();
        topLinksPanel.getElement().getStyle().setMargin(4, Unit.PX);
        add(topLinksPanel);

        greetings = new Label();
        greetings.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.GreetingLabel);
        greetings.getElement().getStyle().setMarginRight(2, Unit.EM);
        greetings.getElement().getStyle().setDisplay(Display.INLINE);
        topLinksPanel.add(greetings);

        logout = new CHyperlink(null, new Command() {
            @Override
            public void execute() {
                presenter.logout();
            }
        });
        logout.setDebugId(new StringDebugId("logout"));
        logout.setValue(i18n.tr("LogOut"));
        logout.setVisible(false);
        logout.asWidget().getElement().getStyle().setMarginRight(15, Unit.EM);
        logout.asWidget().getElement().getStyle().setDisplay(Display.INLINE);
        topLinksPanel.add(logout);

        Label phone = new Label("1-888-310-7000");
        phone.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.PhoneLabel);
        phone.getElement().getStyle().setDisplay(Display.INLINE);
        topLinksPanel.add(phone);

    }

    @Override
    public void setPresenter(final Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onLogedOut() {
        logout.setVisible(false);
        greetings.setText("");

    }

    @Override
    public void onLogedIn(String userName) {
        logout.setVisible(true);
        greetings.setText(userName);

    }
}
