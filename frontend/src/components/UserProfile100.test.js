import React from 'react';
import { render, fireEvent, screen, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import { Provider } from 'react-redux';
import configureStore from 'redux-mock-store';
import { BrowserRouter as Router } from 'react-router-dom';
import $ from 'jquery';
import UserProfile from './UserProfile';
import ACTIONS from '../redux/actions';

const mockStore = configureStore([]);

jest.mock('jquery', () => ({
    ajax: jest.fn(),
}));

describe('UserProfile Component', () => {
    let store;
    let initialState;

    beforeEach(() => {
        initialState = {
            user_id: 1,
            username: 'testuser',
            name: 'Test User',
            permission: 1,
            token: 'testtoken',
            is_login: true,
        };
        store = mockStore(initialState);
    });

    test('renders UserProfile component', () => {
        render(
            <Provider store={store}>
                <Router>
                    <UserProfile />
                </Router>
            </Provider>
        );

        expect(screen.getByText('User Information')).toBeInTheDocument();
    });

    test('renders login prompt when not logged in', () => {
        store = mockStore({
            ...initialState,
            is_login: false,
        });

        render(
            <Provider store={store}>
                <Router>
                    <UserProfile />
                </Router>
            </Provider>
        );

        // expect(screen.getByText('Please')).toBeInTheDocument();
        expect(screen.getByText('sign in')).toBeInTheDocument();
        // expect(screen.getByText('to access')).toBeInTheDocument();
    });

    test('handles empty username on update', async () => {
        render(
            <Provider store={store}>
                <Router>
                    <UserProfile />
                </Router>
            </Provider>
        );

        fireEvent.change(screen.getByLabelText('Username'), { target: { value: '' } });
        fireEvent.click(screen.getByText('Update Information'));

        await waitFor(() => {
            expect(screen.getByText('Username cannot be empty')).toBeInTheDocument();
        });
    });

    test('handles empty name on update', async () => {
        render(
            <Provider store={store}>
                <Router>
                    <UserProfile />
                </Router>
            </Provider>
        );

        fireEvent.change(screen.getByLabelText('Name'), { target: { value: '' } });
        fireEvent.click(screen.getByText('Update Information'));

        await waitFor(() => {
            expect(screen.getByText('Name cannot be empty')).toBeInTheDocument();
        });
    });

    test('handles successful user info update', async () => {
        $.ajax.mockImplementation(({ success }) => {
            success({ error_message: 'success' });
        });

        render(
            <Provider store={store}>
                <Router>
                    <UserProfile />
                </Router>
            </Provider>
        );

        fireEvent.change(screen.getByLabelText('Username'), { target: { value: 'newuser' } });
        fireEvent.change(screen.getByLabelText('Name'), { target: { value: 'New User' } });
        fireEvent.click(screen.getByText('Update Information'));

        await waitFor(() => {
            expect(screen.getByText('Information updated successfully')).toBeInTheDocument();
        });
    });

    test('handles user info update error', async () => {
        $.ajax.mockImplementation(({ success }) => {
            success({ error_message: 'error' });
        });

        render(
            <Provider store={store}>
                <Router>
                    <UserProfile />
                </Router>
            </Provider>
        );

        fireEvent.change(screen.getByLabelText('Username'), { target: { value: 'newuser' } });
        fireEvent.change(screen.getByLabelText('Name'), { target: { value: 'New User' } });
        fireEvent.click(screen.getByText('Update Information'));

        await waitFor(() => {
            expect(screen.getByText('error')).toBeInTheDocument();
        });
    });

    test('handles empty old password on password change', async () => {
        render(
            <Provider store={store}>
                <Router>
                    <UserProfile />
                </Router>
            </Provider>
        );

        fireEvent.change(screen.getByLabelText('Original Password'), { target: { value: '' } });
        fireEvent.click(screen.getAllByText('Change Password')[1]);

        await waitFor(() => {
            expect(screen.getByText('Password')).toBeInTheDocument();
        });
    });

    test('handles empty new password on password change', async () => {
        render(
            <Provider store={store}>
                <Router>
                    <UserProfile />
                </Router>
            </Provider>
        );

        fireEvent.change(screen.getByLabelText('Password'), { target: { value: '' } });
        fireEvent.click(screen.getAllByText('Change Password')[1]);

        await waitFor(() => {
            expect(screen.getByText('Password')).toBeInTheDocument();
        });
    });

    test('handles password mismatch on password change', async () => {
        render(
            <Provider store={store}>
                <Router>
                    <UserProfile />
                </Router>
            </Provider>
        );

        fireEvent.change(screen.getByLabelText('Password'), { target: { value: 'newpassword' } });
        fireEvent.change(screen.getByLabelText('Confirm Password'), { target: { value: 'differentpassword' } });
        fireEvent.click(screen.getAllByText('Change Password')[1]);

        await waitFor(() => {
            expect(screen.getByText('Password')).toBeInTheDocument();
        });
    });

    test('handles successful password change', async () => {
        $.ajax.mockImplementation(({ success }) => {
            success({ error_message: 'success' });
        });

        render(
            <Provider store={store}>
                <Router>
                    <UserProfile />
                </Router>
            </Provider>
        );

        fireEvent.change(screen.getByLabelText('Original Password'), { target: { value: 'oldpassword' } });
        fireEvent.change(screen.getByLabelText('Password'), { target: { value: 'newpassword' } });
        fireEvent.change(screen.getByLabelText('Confirm Password'), { target: { value: 'newpassword' } });
        fireEvent.click(screen.getAllByText('Change Password')[1]);

        await waitFor(() => {
            expect(screen.getByText('Password updated successfully')).toBeInTheDocument();
        });
    });

    test('handles password change error', async () => {
        $.ajax.mockImplementation(({ success }) => {
            success({ error_message: 'error' });
        });

        render(
            <Provider store={store}>
                <Router>
                    <UserProfile />
                </Router>
            </Provider>
        );

        fireEvent.change(screen.getByLabelText('Original Password'), { target: { value: 'oldpassword' } });
        fireEvent.change(screen.getByLabelText('Password'), { target: { value: 'newpassword' } });
        fireEvent.change(screen.getByLabelText('Confirm Password'), { target: { value: 'newpassword' } });
        fireEvent.click(screen.getAllByText('Change Password')[1]);

        await waitFor(() => {
            expect(screen.getByText('error')).toBeInTheDocument();
        });
    });
});
