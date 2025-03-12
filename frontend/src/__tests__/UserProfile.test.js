test('tester is working', () => {
    expect(1 + 1).toEqual(2);
});

const setState = (input) => input;

const check_user_info = (username, name) => {
    const prop_username = "01";
    const prop_name = "Ross";

    if (prop_username && username === prop_username && prop_name && name === prop_name) {
        return setState({
            error_message: "Information has not been modified",
            is_loading: false
        });
    }
    if (username === "") {
        return setState({
            error_message: "Username cannot be empty",
            is_loading: false
        });
    } else if (name === "") {
        return setState({
            error_message: "Name cannot be empty",
            is_loading: false
        });
    } else {
        return null;
    }
}

describe("user information change test", () => {
    test("user info not change", () => {
        const result = check_user_info("01", "Ross");
        expect(result.error_message).toEqual("Information has not been modified");
    });

    test("username empty", () => {
        const result = check_user_info("", "Ross");
        expect(result.error_message).toEqual("Username cannot be empty");
    });

    test("name empty", () => {
        const result = check_user_info("01", "");
        expect(result.error_message).toEqual("Name cannot be empty");
    });

    test("pass", () => {
        const result = check_user_info("02", "Joey");
        expect(result).toBeNull();
    });
});


const check_password = (old_password, password, password_confirm) => {
    if (old_password === "") {
        return setState({
            password_change_message: "The original password cannot be empty",
            is_loading: false
        })
    } else if (password === "") {
        return setState({
            password_change_message: "Password cannot be empty",
            is_loading: false
        })
    } else if (old_password === password) {
        return setState({
            password_change_message: "The new password cannot be the same as the original password",
            is_loading: false
        });
    } else if (password_confirm === "") {
        return setState({
            password_change_message: "Confirm password cannot be empty",
            is_loading: false
        })
    } else if (password !== password_confirm) {
        return setState({
            password_change_message: "The passwords you entered twice do not match",
            is_loading: false
        })
    } else {
        return null;
    }
}

describe("password change test", () => {
    test("old password empty", () => {
        const result = check_password("", "1234", "1234");
        expect(result.password_change_message).toEqual("The original password cannot be empty");
    });

    test("password empty", () => {
        const result = check_password("0123", "", "1234");
        expect(result.password_change_message).toEqual("Password cannot be empty");
    });

    test("password not change", () => {
        const result = check_password("0123", "0123", "0123");
        expect(result.password_change_message).toEqual("The new password cannot be the same as the original password");
    });

    test("confirm password empty", () => {
        const result = check_password("0123", "1234", "");
        expect(result.password_change_message).toEqual("Confirm password cannot be empty");
    });

    test("password not match", () => {
        const result = check_password("0123", "1234", "12345");
        expect(result.password_change_message).toEqual("The passwords you entered twice do not match");
    });

    test("pass", () => {
        const result = check_password("0123", "1234", "1234");
        expect(result).toBeNull();
    });
});