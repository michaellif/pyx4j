package com.propertyvista.portal.client.ui;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;

import com.pyx4j.commons.StringDebugId;
import com.pyx4j.forms.client.ui.CHyperlink;

public class TopRightActionsViewImpl extends FlowPanel implements TopRightActionsView {

    private static I18n i18n = I18nFactory.getI18n(TopRightActionsViewImpl.class);

    private static final String GREETING_MESSAGE = i18n.tr("Welcome");

    private final HorizontalPanel topLinksPanel;

    private Presenter presenter;

    private final CHyperlink logout;

    private final CHyperlink login;

    private final HTML greetings;

    public TopRightActionsViewImpl() {
        setStyleName(PortalView.DEFAULT_STYLE_PREFIX + PortalView.StyleSuffix.Header);
        setSize("100%", "100%");
        getElement().getStyle().setFontSize(0.9, Unit.EM);

        topLinksPanel = new HorizontalPanel();
        topLinksPanel.getElement().getStyle().setMargin(4, Unit.PX);
        add(topLinksPanel);

        greetings = new HTML(GREETING_MESSAGE);
        greetings.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        greetings.getElement().getStyle().setMarginRight(2, Unit.EM);
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
        logout.asWidget().getElement().getStyle().setMarginRight(2, Unit.EM);
        topLinksPanel.add(logout);
        //TODO login is probably not needed here because the login screen is available only under residents
        login = new CHyperlink(null, new Command() {
            @Override
            public void execute() {
                presenter.login();
            }
        });
        login.setDebugId(new StringDebugId("login"));
        login.setValue(i18n.tr("LogIn"));
        login.asWidget().getElement().getStyle().setMarginRight(2, Unit.EM);
        //TODO
        //   topLinksPanel.add(login);

    }

    @Override
    public void setPresenter(final Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onLogedOut() {
        logout.setVisible(false);
        login.setVisible(true);
        greetings.setHTML("");

    }

    @Override
    public void onLogedIn(String userName) {
        logout.setVisible(true);
        login.setVisible(false);

        greetings.setHTML(GREETING_MESSAGE + "&nbsp;" + userName);

    }
}
