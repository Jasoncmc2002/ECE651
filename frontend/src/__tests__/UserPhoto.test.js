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

const returnFileSize = function (number) {
    if (number < 1024) {
        return `${number} bytes`;
    } else if (number >= 1024 && number < 1048576) {
        return `${(number / 1024).toFixed(1)} KB`;
    } else if (number >= 1048576) {
        return `${(number / 1048576).toFixed(1)} MB`;
    }
}

test("check file size", () => {
    expect(returnFileSize(1023)).toEqual("1023 bytes");
    expect(returnFileSize(1024)).toEqual("1.0 KB");
    expect(returnFileSize(1048576)).toEqual("1.0 MB");
})