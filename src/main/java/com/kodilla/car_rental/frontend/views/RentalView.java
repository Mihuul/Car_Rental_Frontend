package com.kodilla.car_rental.frontend.views;

import com.kodilla.car_rental.frontend.client.RentalClient;
import com.kodilla.car_rental.frontend.dto.FullRentalDto;
import com.kodilla.car_rental.frontend.dto.RentalDto;
import com.kodilla.car_rental.frontend.dto.UserDto;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
@UIScope
@Component
public class RentalView extends VerticalLayout {

    private final Grid<FullRentalDto> rentalGrid = new Grid<>();
    private final RentalClient rentalClient;

    private final Dialog extendRentalDialog = new Dialog();
    private final IntegerField extension = new IntegerField("Extend by (days)");
    private final Dialog modifyRentalDialog = new Dialog();
    private final Binder<RentalDto> binderForModifyRental = new Binder<>();
    private final DatePicker modifyStartDate = new DatePicker("Rented from");
    private final DatePicker modifyEndDate = new DatePicker("Rented to");

    private UserDto loggedUserDto;
    private Long rentalId;
    private Long carId;

    @Autowired
    public RentalView(RentalClient rentalClient) {
        this.rentalClient = rentalClient;

        bindFields();

        VerticalLayout extendRentalDialogLayout = new VerticalLayout();
        extendRentalDialog.isCloseOnOutsideClick();
        extendRentalDialog.add(extendRentalDialogLayout);

        VerticalLayout modifyRentalDialogLayout = new VerticalLayout();
        Button confirmModifyRentalButton = createConfirmModifyRentalButton();
        modifyRentalDialogLayout.add(modifyStartDate, modifyEndDate, confirmModifyRentalButton);
        modifyRentalDialog.isCloseOnOutsideClick();
        modifyRentalDialog.add(modifyRentalDialogLayout);

        setColumns();

        rentalGrid.addComponentColumn(this::createCloseRentalButton);
        rentalGrid.addComponentColumn(this::createModifyRentalButton);

        add(rentalGrid);
    }

    public void refreshRentalsForAdmin() {
        loggedUserDto = null;
        List<FullRentalDto> rentals = rentalClient.getAllRentals();
        rentalGrid.setItems(rentals);
    }

    public void refreshRentalsForUser(UserDto userDto) {
        loggedUserDto = userDto;
        List<FullRentalDto> rentals = rentalClient.getRentalByUserId(userDto.getId());
        rentalGrid.setItems(rentals);
    }

    private Button createCloseRentalButton(FullRentalDto fullRentalDto) {
        Dialog confirmCloseRentalDialog = createCloseRentalDialog(fullRentalDto);

        Button closeRentalButton = new Button("Close", event -> confirmCloseRentalDialog.open());

        closeRentalButton.setEnabled(loggedUserDto != null);
        return closeRentalButton;
    }

    private Dialog createCloseRentalDialog(FullRentalDto fullRentalDto) {
        Dialog confirmCloseRentalDialog = new Dialog();
        VerticalLayout confirmationLayout = new VerticalLayout();
        Button confirmCloseRentalButton = createConfirmCloseRentalButton(confirmCloseRentalDialog, fullRentalDto);
        Button cancelCloseRentalButton = createCancelConfirmationButton(confirmCloseRentalDialog);
        Label confirmationLabel = new Label("Are You sure about closing rental?");
        confirmationLayout.add(confirmationLabel, confirmCloseRentalButton, cancelCloseRentalButton);
        confirmCloseRentalDialog.add(confirmationLayout);
        return confirmCloseRentalDialog;
    }

    private Button createConfirmCloseRentalButton(Dialog dialog, FullRentalDto fullRentalDto) {
        return new Button("Confirm", event -> {
            rentalId = fullRentalDto.getId();
            closeRental(rentalId);
            dialog.close();
        });
    }

    private Button createCancelConfirmationButton(Dialog dialog) {
        return new Button("Cancel", event -> dialog.close());
    }

    private Button createModifyRentalButton(FullRentalDto fullRentalDto) {
        Button modifyRentalButton = new Button("Modify");
        modifyRentalButton.addClickListener(event -> {
            rentalId = fullRentalDto.getId();
            carId = fullRentalDto.getCarId();
            modifyRentalDialog.open();
        });
        modifyRentalButton.setEnabled(loggedUserDto != null);
        return modifyRentalButton;
    }

    private Button createConfirmModifyRentalButton() {
        return new Button("Confirm", event -> {
            RentalDto rentalDto = new RentalDto();
            binderForModifyRental.writeBeanIfValid(rentalDto);
            rentalDto.setId(rentalId);
            rentalDto.setCarId(carId);
            rentalDto.setUserId(loggedUserDto.getId());
            rentalClient.updateRental(rentalDto);
            refreshRentalsForUser(loggedUserDto);
            modifyRentalDialog.close();
        });
    }

    private void closeRental(Long rentalId) {
        rentalClient.closeRental(rentalId);
        refreshRentalsForUser(loggedUserDto);
    }

    private void setColumns() {
        rentalGrid.addColumn(FullRentalDto::getId).setHeader("Id");
        rentalGrid.addColumn(FullRentalDto::getRentedFrom).setHeader("Start");
        rentalGrid.addColumn(FullRentalDto::getRentedUntil).setHeader("End");
        rentalGrid.addColumn(FullRentalDto::getCost).setHeader("Cost");
        rentalGrid.addColumn(FullRentalDto::getCarBrand).setHeader("Brand");
        rentalGrid.addColumn(FullRentalDto::getCarModel).setHeader("Model");
        rentalGrid.addColumn(FullRentalDto::getUserName).setHeader("Name");
        rentalGrid.addColumn(FullRentalDto::getUserSurname).setHeader("Surname");
    }
    
    private void bindFields() {
        binderForModifyRental.forField(modifyStartDate)
                .bind(RentalDto::getRentedFrom, RentalDto::setRentedFrom);
        binderForModifyRental.forField(modifyEndDate)
                .bind(RentalDto::getRentedUntil, RentalDto::setRentedUntil);
    }
}
