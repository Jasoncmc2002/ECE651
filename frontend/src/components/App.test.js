import { jwtDecode } from "jwt-decode";

test('tester is working', () => {
    expect(1 + 1).toEqual(2);
});

const jwt_token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJmZjNhMDYxZDUzMDg0N2NhYjQxNGUzMDAwNDIwNWYyYiIsInN1YiI6IjEiLCJpc3MiOiJzZyIsImlhdCI6MTczODAxMTYwMCwiZXhwIjoxNzM5MjIxMjAwfQ.G7iVezp-3l8DocMZJsCjftodM74n2l6wfCZ6TCzUrSY";

const decoded = jwtDecode(jwt_token);

test('jwt token decode correctly', () => {
    expect(decoded.exp).toEqual(1739221200);
    expect(decoded.sub).toEqual("1");
});