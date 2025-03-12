test('tester is working', () => {
    expect(1 + 1).toEqual(2);
});

const setState = (input) => input;

function login_check(username, password) {
    if (username === "") {
        return setState({
            error_message: "Username cannot be empty",
            is_loading: false
        });
    } else if (password === "") {
        return setState({
            error_message: "Password cannot be empty",
            is_loading: false
        });
    } else {
        return null;
    }
}

test('check login empty username', () => {
    const result = login_check("", "123456");
    expect(result.error_message).toEqual("Username cannot be empty");
});

test('check login empty password', () => {
    const result = login_check("0123", "");
    expect(result.error_message).toEqual("Password cannot be empty");
});

test('check login correct input', () => {
    const result = login_check("0123", "0.123");
    expect(result).toBeNull();
});