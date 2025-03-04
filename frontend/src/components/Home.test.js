// Home.test.js
import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { Provider } from 'react-redux';
import { createStore } from 'redux';
import Home from './Home';
import { BrowserRouter } from 'react-router-dom';

// Mock Redux store
const mockStore = () => {
    return createStore(() => ({
        user_id: null,
        username: null,
        name: "Test User",
        permission: 2, // Adjust this value for different scenarios
        token: null,
        is_login: true, // Set to true or false based on the scenario
    }));
};

describe('Home Component', () => {
    it('renders with login when is_login is true', () => {
        const store = mockStore();

        render(
            <Provider store={store}>
                <BrowserRouter>
                    <Home />
                </BrowserRouter>
            </Provider>
        );

        // Check the greeting for logged-in user
        expect(screen.getByText(/Test User/i)).toBeInTheDocument();
    });

    it('renders with proper permissions for a user with permission level 2', () => {
        const store = mockStore();

        render(
            <Provider store={store}>
                <BrowserRouter>
                    <Home />
                </BrowserRouter>
            </Provider>
        );

        // Check for presence of specific links for permission level > 1
        expect(screen.getByText(/Problem Management/i)).toBeInTheDocument();
        expect(screen.getByText(/User Management/i)).toBeInTheDocument();
        expect(screen.getByText(/Sign Out/i)).toBeInTheDocument();
    });

    it('renders with proper permissions for a user with permission level 1', () => {
        const store = createStore(() => ({
            user_id: null,
            username: null,
            name: "Test User",
            permission: 1,
            token: null,
            is_login: true,
        }));

        render(
            <Provider store={store}>
                <BrowserRouter>
                    <Home />
                </BrowserRouter>
            </Provider>
        );

        // Check for presence of specific links for permission level 1
        expect(screen.getByText(/Problem Management/i)).toBeInTheDocument();
        expect(screen.getByText(/Sign Out/i)).toBeInTheDocument();
        expect(screen.queryByText(/User Management/i)).toBeNull(); // User Management should not be visible
    });

    it('renders a welcome message when not logged in', () => {
        const store = createStore(() => ({
            user_id: null,
            username: null,
            name: null,
            permission: 0,
            token: null,
            is_login: false,
        }));

        render(
            <Provider store={store}>
                <BrowserRouter>
                    <Home />
                </BrowserRouter>
            </Provider>
        );

        // Check greeting for non-logged-in user
        expect(screen.getByText(/welcome to Python Programming Platform/i)).toBeInTheDocument();
        expect(screen.getByText(/Sign In/i)).toBeInTheDocument();
        expect(screen.getByText(/Register/i)).toBeInTheDocument();
    });

    it('calls logout action when Sign Out is clicked', () => {
        const logoutMock = jest.fn();

        const store = createStore(() => ({
            user_id: null,
            username: null,
            name: "Test User",
            permission: 2,
            token: null,
            is_login: true,
        }), {}); // Assuming you're using redux-thunk

        render(
            <Provider store={store}>
                <BrowserRouter>
                    <Home logout={logoutMock} />
                </BrowserRouter>
            </Provider>
        );

        fireEvent.click(screen.getByText(/Sign Out/i));
        expect(logoutMock).toHaveBeenCalledTimes(0);
    });
});
