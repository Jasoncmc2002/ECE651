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

function check_user_info_advanced() { // make sure this is a function
    // console.log(this);
    const prop_username = this.props.username;
    const prop_name = this.props.name;
    if (prop_username && this.state.username === prop_username && prop_name && this.state.name === prop_name) {
        this.setState({
            error_message: "Information has not been modified",
            is_loading: false
        });
        // console.log("information unchanged");
        return;
    }
    if (this.state.username === "") {
        this.setState({
            error_message: "Username cannot be empty",
            is_loading: false
        });
    } else if (this.state.name === "") {
        this.setState({
            error_message: "Name cannot be empty",
            is_loading: false
        });
    } else {
        return null;
    }
}

test("check state change class", () => {
    let tc_not_change = new TestCase;
    tc_not_change.props = {
        username: "01",
        name: "Ross"
    };
    tc_not_change.state = {
        username: "01",
        name: "Ross",
    };
    tc_not_change.setState({ username: "02", message: "000" });
    expect(tc_not_change.state.username).toEqual("02");
    expect(tc_not_change.state.message).toEqual("000");
    expect(tc_not_change.state.name).toEqual("Ross");
});

test("check state change object", () => {
    const test_case = {
        state: {
            username: "01",
            name: "Ross",
        },
        setState: function (new_state) {
            // console.log(this);
            this.state = { ...this.state, ...new_state };
        }
    };
    test_case.setState({ username: "02", message: "000" });
    expect(test_case.state.username).toEqual("02");
    expect(test_case.state.message).toEqual("000");
});


test("check not change object", () => {
    let test_case = {
        state: {
            username: "01",
            name: "Ross",
        },
        props: {
            username: "01",
            name: "Ross",
        },
        setState: function (new_state) { this.state = { ...this.state, ...new_state }; }
    };
    test_case.check = check_user_info_advanced;
    test_case.check();
    // console.log(test_case);
    expect(test_case.state.error_message).toEqual("Information has not been modified");
});

test("check not change class", () => {
    let tc_not_change = new TestCase;
    tc_not_change.props = {
        username: "01",
        name: "Ross"
    };
    tc_not_change.state = {
        username: "01",
        name: "Ross",
    };
    tc_not_change.check = check_user_info_advanced;
    tc_not_change.check();
    // console.log(tc_not_change);
    // console.log(typeof tc_not_change);  // this is an object so that's why you can add a function to it
    // console.log(typeof test_case);
    // console.log(typeof check_user_info_advanced);
    expect(tc_not_change.state.error_message).toEqual("Information has not been modified");
});


test("check not change class with constructor", () => {
    const state = {
        username: "01",
        name: "Ross"
    };
    const props = {
        username: "01",
        name: "Ross"
    };
    let tc_not_change = new TestCase(state, props, check_user_info_advanced);
    // console.log(tc_not_change);
    // console.log(typeof tc_not_change);  // this is an object so that's why you can add a function to it
    // console.log(typeof test_case);
    // console.log(typeof check_user_info_advanced);
    tc_not_change.check();
    expect(tc_not_change.state.error_message).toEqual("Information has not been modified");
});