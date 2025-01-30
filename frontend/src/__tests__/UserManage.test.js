test('tester is working', () => {
    expect(1 + 1).toEqual(2);
});

const test_case = {
    state: {},
    props: {},
    setState: function (new_state) { this.state = { ...this.state, ...new_state }; }
};

class TestCase {
    constructor(_state = {}, _props = {}, _check = undefined) {
        this.state = _state;
        this.props = _props;
        this.check = _check;
    };

    setState(new_state) {
        this.state = { ...this.state, ...new_state };
    };
}

function check_user_info_advanced() {
    if (this.state.target_username === "") {
        this.setState({
            error_message: "Username cannot be empty",
            is_loading: false,
        });
    } else if (this.state.target_name === "") {
        this.setState({
            error_message: "Name cannot be empty",
            is_loading: false,
        });
    } else if (parseInt(this.state.target_permission) < 0 || parseInt(this.state.target_permission) > 2) {
        this.setState({
            error_message: "Invalid permission value",
            is_loading: false,
        });
    } else {
        return null;
    }
};

describe("test information update", () => {
    test("empty username", () => {
        const state = {
            target_username: "",
            target_name: "123",
            target_permission: "0"
        };
        const tc_empty_username = new TestCase(state, {}, check_user_info_advanced);
        tc_empty_username.check();
        // console.log(tc_empty_username);
        expect(tc_empty_username.state.error_message).toEqual("Username cannot be empty");
    });

    test("empty name", () => {
        const state = {
            target_username: "03",
            target_name: "",
            target_permission: "0"
        };
        const tc_empty_name = new TestCase(state, {}, check_user_info_advanced);
        tc_empty_name.check();
        expect(tc_empty_name.state.error_message).toEqual("Name cannot be empty");
    });

    test("invalid permission", () => {
        const state = {
            target_username: "03",
            target_name: "1234",
            target_permission: "-1"
        };
        const tc_invalid_permission = new TestCase(state, {}, check_user_info_advanced);
        tc_invalid_permission.check();
        expect(tc_invalid_permission.state.error_message).toEqual("Invalid permission value");
    });

    test("pass", () => {
        const state = {
            target_username: "03",
            target_name: "1234",
            target_permission: "2"
        };
        const tc_invalid_permission = new TestCase(state, {}, check_user_info_advanced);
        tc_invalid_permission.check();
        expect(tc_invalid_permission.state.error_message).toBeUndefined();
    });
});

function check_user_password_advance() {
    if (this.state.target_password === "") {
        this.setState({
            error_message: "Password cannot be empty",
            is_loading: false,
        })
    } else if (this.state.target_password_confirm === "") {
        this.setState({
            error_message: "Confirm password cannot be empty",
            is_loading: false,
        })
    } else if (this.state.target_password !== this.state.target_password_confirm) {
        this.setState({
            error_message: "The passwords you entered twice do not match",
            is_loading: false,
        })
    } else {
        return null;
    }
}

describe("test password update", () => {
    test("empty password", () => {
        const state = {
            target_password: "",
            target_password_confirm: ""
        };
        const tc_empty_password = new TestCase(state, {}, check_user_password_advance);
        tc_empty_password.check();
        expect(tc_empty_password.state.error_message).toEqual("Password cannot be empty");
    });

    test("empty confirm password", () => {
        const state = {
            target_password: "123",
            target_password_confirm: ""
        };
        const tc_empty_password = new TestCase(state, {}, check_user_password_advance);
        tc_empty_password.check();
        expect(tc_empty_password.state.error_message).toEqual("Confirm password cannot be empty");
    });

    test("not match", () => {
        const state = {
            target_password: "123",
            target_password_confirm: "1234"
        };
        const tc_empty_password = new TestCase(state, {}, check_user_password_advance);
        tc_empty_password.check();
        expect(tc_empty_password.state.error_message).toEqual("The passwords you entered twice do not match");
    });

    test("pass", () => {
        const state = {
            target_password: "123",
            target_password_confirm: "123"
        };
        const tc_empty_password = new TestCase(state, {}, check_user_password_advance);
        tc_empty_password.check();
        expect(tc_empty_password.state.error_message).toBeUndefined();
    });
});