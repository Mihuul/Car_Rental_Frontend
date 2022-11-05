package com.kodilla.car_rental.frontend.views;

import com.kodilla.car_rental.frontend.client.UserClient;
import com.kodilla.car_rental.frontend.dto.UserDto;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Component;

@UIScope
@Component
@Route(value = "")
public class RegisterView extends VerticalLayout {

    private final UserClient userClient;
    private final UserDto userDto = new UserDto();

    private final Binder<UserDto> binder = new Binder<>();
    private final TextField name = new TextField("Name");
    private final TextField lastName = new TextField("Last name");
    private final EmailField email = new EmailField("E-mail address");
    private final PasswordField password = new PasswordField("Password");
    private final IntegerField phoneNumber = new IntegerField("Phone number");

    public RegisterView(UserClient userClient) {
        this.userClient = userClient;

        bindFields();

        Span welcomeTitle = new Span("Welcome in Car Rental Service!");
        Span spaceSpan = new Span(" ");
        Span registrationTitle = new Span("Create new account below or log in.");
        Button loginButton = createLoginButton();
        Button registerButton = createRegisterButton();

        add(welcomeTitle, spaceSpan, registrationTitle, name, lastName, email, password, phoneNumber, registerButton,
                loginButton);
        setAlignItems(Alignment.CENTER);
    }

    private void save(UserDto userDto) {
        if (!userClient.doesUserExist(userDto.getEmail())) {
            userClient.createUser(userDto);
            getUI().ifPresent(ui -> ui.navigate("loginView"));
            clearFields();
        } else {
            Dialog alertDialog = createUserAlreadyRegisteredDialog();
            alertDialog.open();
        }
    }

    private Button createLoginButton() {
        return new Button("I already have account. Log me in.", event ->
                getUI().ifPresent(ui -> ui.navigate("loginView")));
    }

    private Button createRegisterButton() {
        return new Button("Create account", event -> {
            if (areFieldsFilled()) {
                binder.writeBeanIfValid(userDto);
                save(userDto);
            } else {
                Dialog alertDialog = createEmptyFieldsDialog();
                alertDialog.open();
            }
        });
    }

    private boolean areFieldsFilled() {
        return (!name.getValue().equals("") &&
                !lastName.getValue().equals("") &&
                !email.getValue().equals("") &&
                !password.getValue().equals("") &&
                phoneNumber.getValue() != null);
    }

    private Dialog createEmptyFieldsDialog() {
        Dialog alertDialog = new Dialog();
        VerticalLayout alertLayout = new VerticalLayout();
        Button cancelAlertButton = new Button("Cancel", event -> alertDialog.close());
        Label alertLabel = new Label("All fields must be filled!");
        alertLayout.add(alertLabel, cancelAlertButton);
        alertDialog.add(alertLayout);
        return alertDialog;
    }

    private Dialog createUserAlreadyRegisteredDialog() {
        Dialog alertDialog = new Dialog();
        VerticalLayout alertLayout = new VerticalLayout();
        Button cancelAlertButton = new Button("Cancel", event -> alertDialog.close());
        Label alertLabel = new Label("User is already registered!");
        alertLayout.add(alertLabel, cancelAlertButton);
        alertDialog.add(alertLayout);
        return alertDialog;
    }

    private void clearFields() {
        name.clear();
        lastName.clear();
        email.clear();
        phoneNumber.clear();
        password.clear();
    }

    private void bindFields() {
        binder.forField(name)
                .bind(UserDto::getName, UserDto::setName);
        binder.forField(lastName)
                .bind(UserDto::getSurname, UserDto::setSurname);
        binder.forField(email)
                .bind(UserDto::getEmail, UserDto::setEmail);
        binder.forField(password)
                .bind(UserDto::getPassword, UserDto::setPassword);
        binder.forField(phoneNumber)
                .bind(UserDto::getPhoneNumber, UserDto::setPhoneNumber);
    }
}
