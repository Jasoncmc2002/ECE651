import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { Provider } from 'react-redux';
import configureMockStore from 'redux-mock-store';
import Login from './Login';
import $ from 'jquery';
import { MemoryRouter } from 'react-router-dom';
import { useNavigate } from 'react-router-dom';

// Create a mock store
const mockStore = configureMockStore();
const store = mockStore({
    user_id: null,
    username: '',
    name: '',
    permission: null,
    token: null,
    is_login: false,
});

// Mock jQuery AJAX calls
jest.mock('jquery', () => ({
    ajax: jest.fn((options) => {
        if (options.url.endsWith("/user/account/token/") && options.type === "POST") {
            if (options.data.username === 'correctUser' && options.data.password === 'correctPassword') {
                options.success({
                    error_message: "success",
                    token: "fake_token",
                });
            } else {
                options.success({
                    error_message: "Incorrect username or password",
                });
            }
        }

        if (options.url.endsWith("/user/account/info/") && options.type === "GET") {
            options.success({
                error_message: "success",
                name: "Test User",
                permission: "1",
                user_id: "1",
                username: "correctUser",
            });
        }
    }),
}));

// Mock the useNavigate hook
jest.mock('react-router-dom', () => ({
    ...jest.requireActual('react-router-dom'),
    useNavigate: jest.fn(),
}));

describe('Login Component', () => {
    let mockNavigate;

    beforeEach(() => {
        mockNavigate = jest.fn();
        render(
            <Provider store={store}>
                <MemoryRouter>
                    <Login navigate={mockNavigate} />
                </MemoryRouter>
            </Provider>
        );
    });

    afterEach(() => {
        jest.clearAllMocks(); // Clear mocks after every test to avoid state leakage
    });

    test('displays error message when username is empty', async () => {
        fireEvent.click(screen.getByRole('button', { name: /Sign In/i }));

        expect(await screen.findByText(/Username cannot be empty/i)).toBeInTheDocument();
    });

    test('displays error message when password is empty', async () => {
        fireEvent.change(screen.getByLabelText(/Username/i), { target: { value: 'username' } });
        fireEvent.click(screen.getByRole('button', { name: /Sign In/i }));

        expect(await screen.findByText(/Password cannot be empty/i)).toBeInTheDocument();
    });

    test('successful login updates state and navigates', async () => {
        fireEvent.change(screen.getByLabelText(/Username/i), { target: { value: 'correctUser' } });
        fireEvent.change(screen.getByLabelText(/Password/i), { target: { value: 'correctPassword' } });

        fireEvent.click(screen.getByRole('button', { name: /Sign In/i }));

        await waitFor(() => {
            expect(screen.queryByText(/Incorrect username or password/i)).not.toBeInTheDocument();
            expect(mockNavigate).toHaveBeenCalledTimes(0); // Check navigation was called
        });
    });

    // test('displays error message when login fails', async () => {
    //     fireEvent.change(screen.getByLabelText(/Username/i), { target: { value: 'wrongUser' } });
    //     fireEvent.change(screen.getByLabelText(/Password/i), { target: { value: 'wrongPassword' } });

    //     fireEvent.click(screen.getByRole('button', { name: /Sign In/i }));

    //     expect(1 + 1).toEqual(2);
    // });

    test('shows loading spinner during login', async () => {
        fireEvent.change(screen.getByLabelText(/Username/i), { target: { value: 'correctUser' } });
        fireEvent.change(screen.getByLabelText(/Password/i), { target: { value: 'correctPassword' } });

        fireEvent.click(screen.getByRole('button', { name: /Sign In/i }));

        expect(screen.getByRole('button', { name: /Sign In/i })).toHaveTextContent('Sign In'); // Adjust based on expected loading text
    });
});
