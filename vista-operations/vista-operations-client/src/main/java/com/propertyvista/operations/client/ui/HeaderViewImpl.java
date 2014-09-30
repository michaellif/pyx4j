package com.propertyvista.operations.client.ui;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.gwt.commons.layout.LayoutChangeEvent;
import com.pyx4j.gwt.commons.layout.LayoutChangeHandler;
import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent;
import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent.ChangeType;
import com.pyx4j.gwt.commons.layout.LayoutType;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Button.ButtonMenuBar;
import com.pyx4j.widgets.client.Button.SecureMenuItem;
import com.pyx4j.widgets.client.Toolbar;

import com.propertyvista.common.client.theme.SiteViewTheme;
import com.propertyvista.operations.client.resources.OperationsImages;
import com.propertyvista.operations.rpc.ac.UserSelfAccountAndSettings;

public class HeaderViewImpl extends FlowPanel implements HeaderView {

    public enum Theme {
        Gainsboro, VillageGreen, BlueCold, BrownWarm
    }

    private static final I18n i18n = I18n.get(HeaderViewImpl.class);

    private Presenter presenter;

    private Button userButton;

    private Button loginButton;

    private LayoutType layoutType;

    private Button sideMenuButton;

    private boolean loggedIn = false;

    public HeaderViewImpl() {
        setStyleName(SiteViewTheme.StyleName.SiteViewHeader.name());

        {//Left Toolbar
            Toolbar toolbar = new Toolbar();
            sideMenuButton = new Button(OperationsImages.INSTANCE.menu(), new Command() {
                @Override
                public void execute() {
                    AppSite.getEventBus().fireEvent(new LayoutChangeRequestEvent(ChangeType.toggleSideMenu));
                }
            });
            toolbar.addItem(sideMenuButton);
            toolbar.addItem(createLogoContainer());
            add(toolbar);
        }

        add(createActionsContainer());

        AppSite.getEventBus().addHandler(LayoutChangeEvent.TYPE, new LayoutChangeHandler() {

            @Override
            public void onLayoutChangeRerquest(LayoutChangeEvent event) {
                doLayout(event.getLayoutType());
            }

        });

        doLayout(LayoutType.getLayoutType(Window.getClientWidth()));
    }

    private Widget createLogoContainer() {
        SimplePanel logoContainer = new SimplePanel();
        logoContainer.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        logoContainer.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        logoContainer.getElement().getStyle().setMargin(5, Unit.PX);

        HTML logo = new HTML("Property Vista Software<br/>Operations");
        logo.getElement().getStyle().setCursor(Cursor.POINTER);

        logo.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.navigToLanding();
            }
        });
        logoContainer.setWidget(logo);
        return logoContainer;
    }

    private Widget createActionsContainer() {
        Toolbar toolbar = new Toolbar();
        toolbar.getElement().getStyle().setProperty("right", "0");
        toolbar.getElement().getStyle().setProperty("top", "0");

        toolbar.addStyleName(SiteViewTheme.StyleName.SiteViewAction.name());

        userButton = new Button("");

        ButtonMenuBar userButtonMenu = new ButtonMenuBar();
        userButton.setMenu(userButtonMenu);

        userButtonMenu.addItem(new SecureMenuItem(i18n.tr("Account"), new Command() {
            @Override
            public void execute() {
                presenter.showAccount();
            }
        }, UserSelfAccountAndSettings.class));

        userButtonMenu.addItem(new MenuItem(i18n.tr("LogOut"), new Command() {
            @Override
            public void execute() {
                presenter.logout();
            }
        }));

        loginButton = new Button(i18n.tr("Log In"), new Command() {
            @Override
            public void execute() {
                presenter.login();
            }
        });

        toolbar.addItem(userButton);
        toolbar.addItem(loginButton);

        return toolbar.asWidget();
    }

    @Override
    public void setPresenter(final Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onLogedOut() {
        this.loggedIn = false;

        userButton.setVisible(false);
        userButton.setTextLabel("");

        loginButton.setVisible(true);
    }

    @Override
    public void onLogedIn(String userName) {
        this.loggedIn = true;

        loginButton.setVisible(false);

        userButton.setVisible(true);
        userButton.setTextLabel(userName);

    }

    private void doLayout(LayoutType layoutType) {
        this.layoutType = layoutType;
        calculateActionsState();
    }

    private void calculateActionsState() {
        switch (layoutType) {
        case phonePortrait:
        case phoneLandscape:
        case tabletPortrait:
            sideMenuButton.setVisible(loggedIn);
            break;
        default:
            sideMenuButton.setVisible(false);
            break;
        }
    }
}
