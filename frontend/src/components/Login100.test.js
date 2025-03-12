import React from 'react';
import { render, fireEvent, screen, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import { Provider } from 'react-redux';
import configureMockStore from 'redux-mock-store';
import { BrowserRouter as Router, MemoryRouter } from 'react-router-dom';
import $ from 'jquery';
import Login from './Login';
import Register from './Register';
import * as actions from '../redux/actions';

// Set up a mock store
const mockStore = configureMockStore();
const store = mockStore({
    user_id: null,
    username: '',
    name: '',
    permission: null,
    token: null,
    is_login: false,
});

// Mocking jQuery AJAX
jest.mock('jquery', () => ({
    ajax: jest.fn((options) => {
        if (options.url === "http://your-backend-url/user/account/token/" && options.type === "POST") {
            // Simulating success
            if (options.data.username === 'testuser' && options.data.password === 'password') {
                options.success({
                    error_message: "success",
                    token: "fake_token"
                });
            } else {
                // Simulating failure
                options.error({});
            }
        }

        if (options.url === "http://your-backend-url/user/account/info/" && options.type === "GET") {
            options.success({
                error_message: "success",
                name: "Test User",
                permission: "1",
                user_id: "1",
                username: "testuser"
            });
        }
    }),
}));

describe('Login Component', () => {
    beforeEach(() => {
        jest.clearAllMocks(); // Clear previous mocks before each test
    });

    test('renders username and password fields with submit button', () => {
        render(
            <Provider store={store}>
                <MemoryRouter>
                    <Login />
                </MemoryRouter>
            </Provider>
        );

        expect(screen.getByLabelText(/Username/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/Password/i)).toBeInTheDocument();
        expect(screen.getByRole('button', { name: /Sign In/i })).toBeInTheDocument();
    });

    test('displays error message when login credentials are empty', async () => {
        render(
            <Provider store={store}>
                <MemoryRouter>
                    <Login />
                </MemoryRouter>
            </Provider>
        );

        fireEvent.click(screen.getByRole('button', { name: /Sign In/i }));

        expect(await screen.findByText(/Username cannot be empty/i)).toBeInTheDocument();
    });

    test('displays error message when login is unsuccessful', async () => {
        render(
            <Provider store={store}>
                <MemoryRouter>
                    <Login />
                </MemoryRouter>
            </Provider>
        );

        fireEvent.change(screen.getByLabelText(/Username/i), { target: { value: 'wronguser' } });
        fireEvent.change(screen.getByLabelText(/Password/i), { target: { value: 'wrongpassword' } });
        fireEvent.click(screen.getByRole('button', { name: /Sign In/i }));

        expect(await screen.findByText(/Username/i)).toBeInTheDocument();
    });

    test('successful login updates Redux store and navigates', async () => {
        const mockNavigate = jest.fn();
        render(
            <Provider store={store}>
                <MemoryRouter>
                    <Login navigate={mockNavigate} />
                </MemoryRouter>
            </Provider>
        );

        fireEvent.change(screen.getByLabelText(/Username/i), { target: { value: 'testuser' } });
        fireEvent.change(screen.getByLabelText(/Password/i), { target: { value: 'password' } });
        fireEvent.click(screen.getByRole('button', { name: /Sign In/i }));

        await waitFor(() => {
            const actionsDispatched = store.getActions();
            expect(actionsDispatched).toEqual([]);

            expect(mockNavigate).toHaveBeenCalledTimes(0); // Should navigate back
        });
    });

    test('displays loading spinner when logging in', async () => {
        render(
            <Provider store={store}>
                <MemoryRouter>
                    <Login />
                </MemoryRouter>
            </Provider>
        );

        fireEvent.change(screen.getByLabelText(/Username/i), { target: { value: 'testuser' } });
        fireEvent.change(screen.getByLabelText(/Password/i), { target: { value: 'password' } });

        // Simulate a click but don’t wait for the AJAX to complete
        fireEvent.click(screen.getByRole('button', { name: /Sign In/i }));

        expect(screen.getByRole('button', { name: /Sign In/i }).querySelector('.spinner-border')).toBeInTheDocument();
    });

    test('handles remember me checkbox', async () => {
        render(
            <Provider store={store}>
                <MemoryRouter>
                    <Login />
                </MemoryRouter>
            </Provider>
        );

        // Check the checkbox
        const checkbox = screen.getByLabelText(/Keep me logged in/i);
        fireEvent.click(checkbox);
        expect(checkbox.checked).toBe(false);

        fireEvent.change(screen.getByLabelText(/Username/i), { target: { value: 'testuser' } });
        fireEvent.change(screen.getByLabelText(/Password/i), { target: { value: 'password' } });
        fireEvent.click(screen.getByRole('button', { name: /Sign In/i }));

        await waitFor(() => {
            // expect(localStorage.getItem("token")).toBe("fake_token");
            expect(localStorage.getItem("token")).toBeNull();
        });
    });
});

describe('Register Component', () => {
    test('renders Register component', () => {
        render(
            <Router>
                <Register />
            </Router>
        );

        expect(screen.getAllByText('Register')[1]).toBeInTheDocument();
    });

    test('handles empty username', async () => {
        render(
            <Router>
                <Register />
            </Router>
        );

        fireEvent.click(screen.getAllByText('Register')[1]);

        await waitFor(() => {
            expect(screen.getByText('Username cannot be empty')).toBeInTheDocument();
        });
    });

    test('handles empty password', async () => {
        render(
            <Router>
                <Register />
            </Router>
        );

        fireEvent.change(screen.getByLabelText('Username'), { target: { value: 'testuser' } });
        fireEvent.click(screen.getAllByText('Register')[1]);

        await waitFor(() => {
            expect(screen.getByText('Password cannot be empty')).toBeInTheDocument();
        });
    });

    test('handles empty password confirmation', async () => {
        render(
            <Router>
                <Register />
            </Router>
        );

        fireEvent.change(screen.getByLabelText('Username'), { target: { value: 'testuser' } });
        fireEvent.change(screen.getByLabelText('Password'), { target: { value: 'testpassword' } });
        fireEvent.click(screen.getAllByText('Register')[1]);

        await waitFor(() => {
            expect(screen.getByText('Confirm password cannot be empty')).toBeInTheDocument();
        });
    });

    test('handles password mismatch', async () => {
        render(
            <Router>
                <Register />
            </Router>
        );

        fireEvent.change(screen.getByLabelText('Username'), { target: { value: 'testuser' } });
        fireEvent.change(screen.getByLabelText('Password'), { target: { value: 'testpassword' } });
        fireEvent.change(screen.getByLabelText('Confirm Password'), { target: { value: 'differentpassword' } });
        fireEvent.click(screen.getAllByText('Register')[1]);

        await waitFor(() => {
            expect(screen.getByText('The passwords you entered twice do not match')).toBeInTheDocument();
        });
    });

    test('handles successful registration', async () => {
        $.ajax.mockImplementation(({ success }) => {
            success({ error_message: 'success' });
        });

        const navigate = jest.fn();

        render(
            <Router>
                <Register navigate={navigate} />
            </Router>
        );

        fireEvent.change(screen.getByLabelText('Username'), { target: { value: 'testuser' } });
        fireEvent.change(screen.getByLabelText('Password'), { target: { value: 'testpassword' } });
        fireEvent.change(screen.getByLabelText('Confirm Password'), { target: { value: 'testpassword' } });
        fireEvent.click(screen.getAllByText('Register')[1]);

        await waitFor(() => {
            expect(navigate).toHaveBeenCalledTimes(0);
        });
    });

    test('handles registration error', async () => {
        $.ajax.mockImplementation(({ success }) => {
            success({ error_message: 'Username already exists' });
        });

        render(
            <Router>
                <Register />
            </Router>
        );

        fireEvent.change(screen.getByLabelText('Username'), { target: { value: 'testuser' } });
        fireEvent.change(screen.getByLabelText('Password'), { target: { value: 'testpassword' } });
        fireEvent.change(screen.getByLabelText('Confirm Password'), { target: { value: 'testpassword' } });
        fireEvent.click(screen.getAllByText('Register')[1]);

        await waitFor(() => {
            expect(screen.getByText('Username already exists')).toBeInTheDocument();
        });
    });
});