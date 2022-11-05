package com.kodilla.car_rental.frontend.views;

import com.kodilla.car_rental.frontend.client.UserClient;
import com.kodilla.car_rental.frontend.dto.UserDto;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Component;

@UIScope
@Component
public class UserAccountView extends  VerticalLayout{

    private final UserClient userClient;
    private final Binder<UserDto> binder = new Binder<>();
    private final TextField name = new TextField("Name");
    private final TextField surname = new TextField("Last name");
    private final TextField email = new TextField("Email address");
    private final IntegerField phoneNumber = new IntegerField("Phone number");
    private final TextField password = new TextField("Password");
    private UserDto loggedUserDto = new UserDto();
    private Long userId;

    public UserAccountView(UserClient userClient) {
        this.userClient = userClient;

        bindFields();

        VerticalLayout accountLayout = new VerticalLayout();
        HorizontalLayout horizontalLayout = new HorizontalLayout();

        Button deleteUserButton = createDeleteAccountButton();
        Button updateUserButton = createUpdateUserButton();
        horizontalLayout.add(updateUserButton, deleteUserButton);

        accountLayout.add(name, surname, email, phoneNumber, password, horizontalLayout);

        add(accountLayout);
        setHorizontalComponentAlignment(Alignment.CENTER, accountLayout);
    }

    public void refreshForUser(UserDto userDto) {
        loggedUserDto = userDto;
        userId = userDto.getId();
        binder.readBean(userDto);
    }

    private Button createDeleteAccountButton() {
        Dialog confirmDeleteAccountDialog = createDeleteAccountDialog();
        return new Button("Delete account", event -> confirmDeleteAccountDialog.open());
    }

    private Dialog createDeleteAccountDialog() {
        Dialog confirmDeleteAccountDialog = new Dialog();
        VerticalLayout deleteAccountLayout = new VerticalLayout();
        Button confirmDeleteAccountButton = createConfirmDeleteAccountButton(confirmDeleteAccountDialog);
        Button cancelDeleteAccountButton = createCancelConfirmationButton(confirmDeleteAccountDialog);
        Label confirmationLabel = new Label("Are You sure about deleting Your Account?" +
                "\nPlease check firstly if You have closed all rentals.");
        deleteAccountLayout.add(confirmationLabel, confirmDeleteAccountButton, cancelDeleteAccountButton);
        confirmDeleteAccountDialog.add(deleteAccountLayout);
        return confirmDeleteAccountDialog;
    }

    private Button createConfirmDeleteAccountButton(Dialog dialog) {
        return new Button("Delete", event -> {
            deleteUser(loggedUserDto);
            dialog.close();
        });
    }

    private Button createUpdateUserButton() {
        Dialog confirmUpdateUserDialog = createUpdateUserDialog();
        return new Button("Modify personal data", event -> confirmUpdateUserDialog.open());
    }

    private Dialog createUpdateUserDialog() {
        Dialog confirmUpdateUserDialog = new Dialog();
        VerticalLayout confirmationLayout = new VerticalLayout();
        Button confirmUpdateUserButton = createConfirmUpdateUserButton(confirmUpdateUserDialog);
        Button cancelUpdateUserButton = createCancelConfirmationButton(confirmUpdateUserDialog);
        Label confirmationLabel = new Label("Are You sure about modify Your data?");
        confirmationLayout.add(confirmationLabel, confirmUpdateUserButton, cancelUpdateUserButton);
        confirmUpdateUserDialog.add(confirmationLayout);
        return confirmUpdateUserDialog;
    }

    private Button createConfirmUpdateUserButton(Dialog dialog) {
        return new Button("Modify", event -> {
            if (areFieldsFilled()) {
                if (binder.writeBeanIfValid(loggedUserDto)) {
                    updateUser(loggedUserDto);
                }
                dialog.close();
            } else {
                Dialog alertDialog = createUpdateAlertDialog();
                alertDialog.open();
            }
        });
    }

    private Button createCancelConfirmationButton(Dialog dialog) {
        return new Button("Cancel", event -> dialog.close());
    }

    private void updateUser(UserDto userDto) {
        userDto.setId(userId);
        userClient.updateUser(userDto);
    }

    private void deleteUser(UserDto userDto) {
        if (userClient.doesUserExist(userDto.getEmail())) {
            userClient.deleteUser(userDto.getId());
            getUI().ifPresent(ui -> ui.navigate(""));
        } else {
            Dialog alertDialog = createDeleteAlertDialog();
            alertDialog.open();
        }
    }

    private Dialog createDeleteAlertDialog() {
        Dialog alertDialog = new Dialog();
        VerticalLayout alertLayout = new VerticalLayout();
        Button cancelAlertButton = new Button("Cancel", event -> alertDialog.close());
        Label alertLabel = new Label("You've got still opened rents. Deleting account is forbidden.");
        alertLayout.add(alertLabel, cancelAlertButton);
        alertDialog.add(alertLayout);
        return alertDialog;
    }

    private boolean areFieldsFilled() {
        return (!name.getValue().equals("") &&
                !surname.getValue().equals("") &&
                !email.getValue().equals("") &&
                !password.getValue().equals("") &&
                phoneNumber.getValue() != null);
    }

    private Dialog createUpdateAlertDialog() {
        Dialog alertDialog = new Dialog();
        VerticalLayout alertLayout = new VerticalLayout();
        Button cancelAlertButton = new Button("Cancel", event -> alertDialog.close());
        Label alertLabel = new Label("All fields must be filled!");
        alertLayout.add(alertLabel, cancelAlertButton);
        alertDialog.add(alertLayout);
        return alertDialog;
    }

    private void bindFields() {
        binder.forField(name)
                .bind(UserDto::getName, UserDto::setName);
        binder.forField(surname)
                .bind(UserDto::getSurname, UserDto::setSurname);
        binder.forField(email)
                .bind(UserDto::getEmail, UserDto::setEmail);
        binder.forField(phoneNumber)
                .bind(UserDto::getPhoneNumber, UserDto::setPhoneNumber);
        binder.forField(password)
                .bind(UserDto::getPassword, UserDto::setPassword);
    }
}
