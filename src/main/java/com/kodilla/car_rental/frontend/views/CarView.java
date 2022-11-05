package com.kodilla.car_rental.frontend.views;

import com.kodilla.car_rental.frontend.client.CarClient;
import com.kodilla.car_rental.frontend.client.RentalClient;
import com.kodilla.car_rental.frontend.dto.CarDto;
import com.kodilla.car_rental.frontend.dto.RentalDto;
import com.kodilla.car_rental.frontend.dto.Status;
import com.kodilla.car_rental.frontend.dto.UserDto;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@UIScope
@Component
public class CarView extends  VerticalLayout{

    private final Grid<CarDto> carGrid = new Grid<>();
    private final CarClient carClient;
    private final RentalClient rentalClient;
    private final RentalView rentalsView;

    private final CarDto carDto = new CarDto();
    private final Dialog addCarDialog = new Dialog();
    private final Binder<CarDto> binderForSavingCar = new Binder<>();
    private final TextField vin = new TextField("Vin");
    private final TextField brand = new TextField("Brand");
    private final TextField model = new TextField("Model");
    private final IntegerField productionYear = new IntegerField("Production year");
    private final TextField fuelType = new TextField("Fuel type");
    private final IntegerField mileage = new IntegerField("Mileage");
    private final BigDecimalField dailyCost = new BigDecimalField("Cost / day");
    private final Dialog updateCarDialog = new Dialog();
    private final Binder<CarDto> binderForUpdatingCar = new Binder<>();
    private final TextField vinUpdate = new TextField("Vin");
    private final TextField brandUpdate = new TextField("Brand");
    private final TextField modelUpdate = new TextField("Model");
    private final IntegerField productionYearUpdate = new IntegerField("Production year");
    private final TextField fuelTypeUpdate = new TextField("Fuel type");
    private final IntegerField mileageUpdate = new IntegerField("Mileage");
    private final BigDecimalField dailyCostUpdate = new BigDecimalField("Cost / day");
    private final Dialog newRentalDialog = new Dialog();
    private final Binder<RentalDto> binderForRental = new Binder<>();
    private final DatePicker startRentDate = new DatePicker("Rented from");
    private final DatePicker endRentDate = new DatePicker("Rented to");
    Button addCarButton = new Button("Add new car");
    private UserDto loggedUserDto;
    private Long carId;

    @Autowired
    public CarView(CarClient carClient, RentalClient rentalClient, RentalView rentalView) {
        this.carClient = carClient;
        this.rentalClient = rentalClient;
        this.rentalsView = rentalView;

        bindFields();

        VerticalLayout newCarDialogLayout = new VerticalLayout();
        Button saveCarButton = createSaveCarButton();
        newCarDialogLayout.add(vin, brand, model, productionYear, fuelType, mileage,
                dailyCost, saveCarButton);
        addCarDialog.isCloseOnOutsideClick();
        addCarDialog.add(newCarDialogLayout);

        VerticalLayout updateCarDialogLayout = new VerticalLayout();
        Button confirmUpdateButton = createConfirmUpdateButton();
        updateCarDialogLayout.add(vinUpdate, brandUpdate, modelUpdate, productionYearUpdate, fuelTypeUpdate,
                mileageUpdate, dailyCostUpdate, confirmUpdateButton);
        updateCarDialog.isCloseOnOutsideClick();
        updateCarDialog.add(updateCarDialogLayout);

        VerticalLayout newRentalDialogLayout = new VerticalLayout();
        Button confirmRentButton = createConfirmRentalButton();
        newRentalDialogLayout.add(startRentDate, endRentDate, confirmRentButton);
        newRentalDialog.isCloseOnOutsideClick();
        newRentalDialog.add(newRentalDialogLayout);

        setColumns();

        carGrid.addComponentColumn(this::createRentalButton);
        carGrid.addComponentColumn(this::createUpdateButton);
        carGrid.addComponentColumn(this::createDeleteButton);

        addCarButton.addClickListener(e -> addCarDialog.open());

        add(addCarButton, carGrid, addCarDialog);
    }

    public void refreshCarsForAdmin() {
        loggedUserDto = null;
        addCarButton.setEnabled(true);
        List<CarDto> cars = carClient.getCars();
        carGrid.setItems(cars);
    }

    public void refreshCarsForUser(UserDto userDto) {
        loggedUserDto = userDto;
        addCarButton.setEnabled(false);
        List<CarDto> cars = carClient.getCars();
        carGrid.setItems(cars);
    }

    private void saveCar(CarDto carDto) {
        carClient.saveCar(carDto);
        refreshCarsForAdmin();
        addCarDialog.close();
        clearFields();
    }

    private void clearFields() {
        vin.clear();
        brand.clear();
        model.clear();
        productionYear.clear();
        fuelType.clear();
        mileage.clear();
        dailyCost.clear();
    }

    private Button createSaveCarButton() {
        return new Button("Save car", event -> {
            if (areCreateCarFieldsFilled()) {
                binderForSavingCar.writeBeanIfValid(carDto);
                saveCar(carDto);
            } else {
                Dialog alertDialog = createAlertDialog();
                alertDialog.open();
            }
        });
    }

    private Button createUpdateButton(CarDto carDto) {
        Button updateButton = new Button("Update");
        updateButton.addClickListener(e -> {
            carId = carDto.getId();
            binderForUpdatingCar.readBean(carDto);
            updateCarDialog.open();
        });
        if (loggedUserDto == null) {
            updateButton.setEnabled(!carDto.getStatus().equals(Status.RENTED));
        } else {
            updateButton.setEnabled(false);
        }
        return updateButton;
    }

    private Button createConfirmUpdateButton() {
        return new Button("Confirm", event -> {
            if (areUpdatedCarFieldsFilled()) {
                binderForUpdatingCar.writeBeanIfValid(carDto);
                carDto.setId(carId);
                carClient.updateCar(carDto);
                refreshCarsForAdmin();
                updateCarDialog.close();
            } else {
                Dialog alertDialog = createAlertDialog();
                alertDialog.open();
            }
        });
    }

    private Button createRentalButton(CarDto carDto) {
        Button rentalButton = new Button("Rent");
        rentalButton.addClickListener(e -> {
            carId = carDto.getId();
            newRentalDialog.open();
        });
        if (loggedUserDto == null) {
            rentalButton.setEnabled(false);
        } else {
            rentalButton.setEnabled(!carDto.getStatus().equals(Status.RENTED));
        }
        return rentalButton;
    }

    private Button createConfirmRentalButton() {
        return new Button("Confirm rental", event -> {
            RentalDto rentalDto = new RentalDto();
            binderForRental.writeBeanIfValid(rentalDto);
            rentalDto.setCarId(carId);
            rentalDto.setUserId(loggedUserDto.getId());
            rentalClient.createRental(rentalDto);
            rentalsView.refreshRentalsForUser(loggedUserDto);
            refreshCarsForUser(loggedUserDto);
            newRentalDialog.close();
        });
    }

    private Button createDeleteButton(CarDto carDto) {
        Dialog confirmDeleteCarDialog = createDeleteCarDialog(carDto);
        Button deleteButton = new Button("Delete", event -> confirmDeleteCarDialog.open());
        if (loggedUserDto == null) {
            deleteButton.setEnabled(!carDto.getStatus().equals(Status.RENTED));
        } else {
            deleteButton.setEnabled(false);
        }
        return deleteButton;
    }

    private Dialog createDeleteCarDialog(CarDto carDto) {
        Dialog confirmDeleteCarDialog = new Dialog();
        VerticalLayout confirmationLayout = new VerticalLayout();
        Button confirmDeleteCarButton = createConfirmDeleteCarButton(confirmDeleteCarDialog, carDto);
        Button cancelDeleteCarButton = createCancelDeleteCarButton(confirmDeleteCarDialog);
        Label confirmationLabel = new Label("Are You sure about deleting car from database?");
        confirmationLayout.add(confirmationLabel, confirmDeleteCarButton, cancelDeleteCarButton);
        confirmDeleteCarDialog.add(confirmationLayout);
        return confirmDeleteCarDialog;
    }

    private Button createConfirmDeleteCarButton(Dialog dialog, CarDto carDto) {
        return new Button("Delete", event -> {
            carClient.deleteCar(carDto.getId());
            refreshCarsForAdmin();
            dialog.close();
        });
    }

    private Button createCancelDeleteCarButton(Dialog dialog) {
        return new Button("Cancel", event -> dialog.close());
    }

    private boolean areCreateCarFieldsFilled() {
        return (!vin.getValue().equals("") &&
                !brand.getValue().equals("") &&
                !model.getValue().equals("") &&
                productionYear.getValue() != null &&
                !fuelType.getValue().equals("") &&
                mileage.getValue() != null &&
                dailyCost.getValue() != null);
    }

    private boolean areUpdatedCarFieldsFilled() {
        return (!vinUpdate.getValue().equals("") &&
                !brandUpdate.getValue().equals("") &&
                !modelUpdate.getValue().equals("") &&
                productionYearUpdate.getValue() != null &&
                !fuelTypeUpdate.getValue().equals("") &&
                mileageUpdate.getValue() != null &&
                dailyCostUpdate.getValue() != null);
    }

    private Dialog createAlertDialog() {
        Dialog alertDialog = new Dialog();
        VerticalLayout alertLayout = new VerticalLayout();
        Button cancelAlertButton = new Button("Cancel", event -> alertDialog.close());
        Label alertLabel = new Label("All fields must be filled!");
        alertLayout.add(alertLabel, cancelAlertButton);
        alertDialog.add(alertLayout);
        return alertDialog;
    }

    private void setColumns() {
        carGrid.addColumn(CarDto::getId).setHeader("Id");
        carGrid.addColumn(CarDto::getBrand).setHeader("Brand");
        carGrid.addColumn(CarDto::getModel).setHeader("Model");
        carGrid.addColumn(CarDto::getProductionYear).setHeader("Year");
        carGrid.addColumn(CarDto::getMileage).setHeader("Mileage");
        carGrid.addColumn(CarDto::getDailyCost).setHeader("Cost/day");
        carGrid.addColumn(CarDto::getStatus).setHeader("Status");
    }

    private void bindFields() {
        binderForSavingCar.forField(vin)
                .bind(CarDto::getVin, CarDto::setVin);
        binderForSavingCar.forField(brand)
                .bind(CarDto::getBrand, CarDto::setBrand);
        binderForSavingCar.forField(model)
                .bind(CarDto::getModel, CarDto::setModel);
        binderForSavingCar.forField(productionYear)
                .bind(CarDto::getProductionYear, CarDto::setProductionYear);
        binderForSavingCar.forField(fuelType)
                .bind(CarDto::getFuel, CarDto::setFuel);
        binderForSavingCar.forField(mileage)
                .bind(CarDto::getMileage, CarDto::setMileage);
        binderForSavingCar.forField(dailyCost)
                .bind(CarDto::getDailyCost, CarDto::setDailyCost);

        binderForUpdatingCar.forField(vinUpdate)
                .bind(CarDto::getVin, CarDto::setVin);
        binderForUpdatingCar.forField(brandUpdate)
                .bind(CarDto::getBrand, CarDto::setBrand);
        binderForUpdatingCar.forField(modelUpdate)
                .bind(CarDto::getModel, CarDto::setModel);
        binderForUpdatingCar.forField(productionYearUpdate)
                .bind(CarDto::getProductionYear, CarDto::setProductionYear);
        binderForUpdatingCar.forField(fuelTypeUpdate)
                .bind(CarDto::getFuel, CarDto::setFuel);
        binderForUpdatingCar.forField(mileageUpdate)
                .bind(CarDto::getMileage, CarDto::setMileage);
        binderForUpdatingCar.forField(dailyCostUpdate)
                .bind(CarDto::getDailyCost, CarDto::setDailyCost);
        binderForRental.forField(startRentDate)
                .bind(RentalDto::getRentedFrom, RentalDto::setRentedFrom);
        binderForRental.forField(endRentDate)
                .bind(RentalDto::getRentedUntil, RentalDto::setRentedUntil);
    }
}
