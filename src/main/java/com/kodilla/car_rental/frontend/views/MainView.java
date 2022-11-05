package com.kodilla.car_rental.frontend.views;

import com.kodilla.car_rental.frontend.dto.UserDto;
import com.kodilla.car_rental.frontend.utils.PagedTabs;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Component;

@UIScope
@Component
@Route(value = "mainView")
public class MainView extends VerticalLayout{

    private final CarView carsView;
    private final UserView usersView;
    private final RentalView rentalsView;
    private final UserAccountView userAccountView;
    private final LogoutView logoutView;
    private final VinApiView vinApiView;

    private final PagedTabs tabs = new PagedTabs();
    private final Tab carsTab = new Tab("Cars");
    private final Tab usersTab = new Tab("Users");
    private final Tab rentalsTab = new Tab("Rentals");
    private final Tab userAccountTab = new Tab("User account");
    private final Tab hereApiTab = new Tab("Agency searcher");
    private final Tab vinDecoderTab = new Tab("Vin decoder");
    private final Tab logoutTab = new Tab();

    private UserDto loggedUserDto;

    public MainView(CarView carsView, UserView userView, RentalView rentalsView, UserAccountView userAccountView,
                    VinApiView vinApiView, LogoutView logoutView) {
        this.carsView = carsView;
        this.usersView = userView;
        this.rentalsView = rentalsView;
        this.userAccountView = userAccountView;
        this.vinApiView = vinApiView;
        this.logoutView = logoutView;

        tabs.add(carsView, carsTab);
        tabs.add(userView, usersTab);
        tabs.add(rentalsView, rentalsTab);
        tabs.add(userAccountView, userAccountTab);
        tabs.add(vinApiView, vinDecoderTab);
        tabs.add(logoutView, logoutTab);

        Button logoutButton = createLogoutButton();
        logoutTab.add(logoutButton);

        add(tabs);
    }

    public void adminViewSetup() {
        loggedUserDto = null;
        userAccountTab.setVisible(false);
        usersTab.setVisible(true);
        carsView.refreshCarsForAdmin();
        rentalsView.refreshRentalsForAdmin();
        usersView.refreshUsers();
        vinApiView.clearGrid();
    }

    public void userViewSetup(UserDto userDto) {
        loggedUserDto = userDto;
        userAccountTab.setVisible(true);
        usersTab.setVisible(false);
        carsView.refreshCarsForUser(userDto);
        rentalsView.refreshRentalsForUser(userDto);
        userAccountView.refreshForUser(userDto);
        vinApiView.clearGrid();
    }

    public void setBackStartingTab() {
        tabs.select(carsTab);
    }

    private Button createLogoutButton() {
        return new Button("Log out", event -> logoutView.displayLogOutDialog());
    }
}
