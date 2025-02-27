test('tester is working', () => {
    expect(1 + 1).toEqual(2);
});

const setState = (input) => input;

function register_check(username, password, password_confirm) {
    if (username === "") {
        return setState({
            error_message: "Username cannot be empty",
            is_loading: false,
        })
    } else if (password === "") {
        return setState({
            error_message: "Password cannot be empty",
            is_loading: false,
        })
    } else if (password_confirm === "") {
        return setState({
            error_message: "Confirm password cannot be empty",
            is_loading: false,
        })
    } else if (password !== password_confirm) {
        return setState({
            error_message: "The passwords you entered twice do not match",
            is_loading: false,
        })
    } else {
        return null;
    }
}

test('check register empty username', () => {
    const result = register_check("", "123456", "123456");
    expect(result.error_message).toEqual("Username cannot be empty");
});

test('check register empty password', () => {
    const result = register_check("0123", "", "");
    expect(result.error_message).toEqual("Password cannot be empty");
});

test('check register empty confirm password', () => {
    const result = register_check("0123", "123", "");
    expect(result.error_message).toEqual("Confirm password cannot be empty");
});

test('check register not match password', () => {
    const result = register_check("0123", "123", "1234");
    expect(result.error_message).toEqual("The passwords you entered twice do not match");
});

test('check register correct input', () => {
    const result = register_check("0123", "01234", "01234");
    expect(result).toBeNull();
});