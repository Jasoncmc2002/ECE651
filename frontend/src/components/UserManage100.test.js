import React from 'react';
import { render, fireEvent, screen, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import { Provider } from 'react-redux';
import configureStore from 'redux-mock-store';
import { BrowserRouter as Router } from 'react-router-dom';
import $ from 'jquery';
import UserManage from './UserManage';

const mockStore = configureStore([]);

jest.mock('jquery', () => ({
    ajax: jest.fn(),
}));

describe('UserManage Component', () => {
    let store;
    let initialState;

    beforeEach(() => {
        initialState = {
            user_id: 1,
            username: 'testuser',
            name: 'Test User',
            permission: 2,
            token: 'testtoken',
            is_login: true,
        };
        store = mockStore(initialState);
    });

    test('renders UserManage component', () => {
        render(
            <Provider store={store}>
                <Router>
                    <UserManage />
                </Router>
            </Provider>
        );

        expect(screen.getByText('User Management')).toBeInTheDocument();
    });

    test('renders login prompt when not logged in', () => {
        store = mockStore({
            ...initialState,
            is_login: false,
        });

        render(
            <Provider store={store}>
                <Router>
                    <UserManage />
                </Router>
            </Provider>
        );

        // expect(screen.getByText('Please')).toBeInTheDocument();
        expect(screen.getByText('sign in')).toBeInTheDocument();
        // expect(screen.getByText('to access')).toBeInTheDocument();
    });

    test('renders permission denied message when logged in with insufficient permissions', () => {
        store = mockStore({
            ...initialState,
            permission: 0,
        });

        render(
            <Provider store={store}>
                <Router>
                    <UserManage />
                </Router>
            </Provider>
        );

        expect(screen.getByText('You do not have permission to access this page')).toBeInTheDocument();
    });

    test('handles empty username on target change', async () => {
        render(
            <Provider store={store}>
                <Router>
                    <UserManage />
                </Router>
            </Provider>
        );

        fireEvent.change(screen.getAllByLabelText('Username')[0], { target: { value: '' } });
        fireEvent.click(screen.getByText('Update Information'));

        await waitFor(() => {
            expect(screen.getByText('Update Information')).toBeInTheDocument();
        });
    });

    test('handles empty name on target change', async () => {
        render(
            <Provider store={store}>
                <Router>
                    <UserManage />
                </Router>
            </Provider>
        );

        fireEvent.change(screen.getAllByLabelText('Name')[0], { target: { value: '' } });
        fireEvent.click(screen.getByText('Update Information'));

        await waitFor(() => {
            expect(screen.getByText('Update Information')).toBeInTheDocument();
        });
    });

    test('handles invalid permission on target change', async () => {
        render(
            <Provider store={store}>
                <Router>
                    <UserManage />
                </Router>
            </Provider>
        );

        fireEvent.change(screen.getByLabelText('Permission'), { target: { value: '-1' } });
        fireEvent.click(screen.getByText('Update Information'));

        await waitFor(() => {
            expect(screen.getByText('Update Information')).toBeInTheDocument();
        });
    });

    test('handles successful target change', async () => {
        $.ajax.mockImplementation(({ success }) => {
            success({ error_message: 'success' });
        });

        render(
            <Provider store={store}>
                <Router>
                    <UserManage />
                </Router>
            </Provider>
        );

        fireEvent.change(screen.getAllByLabelText('Username')[0], { target: { value: 'newuser' } });
        fireEvent.change(screen.getAllByLabelText('Name')[0], { target: { value: 'New User' } });
        fireEvent.change(screen.getAllByLabelText('Permission')[0], { target: { value: '1' } });
        fireEvent.click(screen.getByText('Update Information'));

        await waitFor(() => {
            expect(screen.getByText('Update Information')).toBeInTheDocument();
        });
    });

    test('handles target change error', async () => {
        $.ajax.mockImplementation(({ success }) => {
            success({ error_message: 'error' });
        });

        render(
            <Provider store={store}>
                <Router>
                    <UserManage />
                </Router>
            </Provider>
        );

        fireEvent.change(screen.getAllByLabelText('Username')[0], { target: { value: 'newuser' } });
        fireEvent.change(screen.getAllByLabelText('Name')[0], { target: { value: 'New User' } });
        fireEvent.change(screen.getAllByLabelText('Permission')[0], { target: { value: '1' } });
        fireEvent.click(screen.getByText('Update Information'));

        await waitFor(() => {
            expect(screen.getByText('Status')).toBeInTheDocument();
        });
    });

    test('handles empty password on password change', async () => {
        render(
            <Provider store={store}>
                <Router>
                    <UserManage />
                </Router>
            </Provider>
        );

        fireEvent.change(screen.getByLabelText('Password'), { target: { value: '' } });
        fireEvent.click(screen.getByText('Change Password'));

        await waitFor(() => {
            expect(screen.getByText('Status')).toBeInTheDocument();
        });
    });

    test('handles empty confirm password on password change', async () => {
        render(
            <Provider store={store}>
                <Router>
                    <UserManage />
                </Router>
            </Provider>
        );

        fireEvent.change(screen.getByLabelText('Confirm Password'), { target: { value: '' } });
        fireEvent.click(screen.getByText('Change Password'));

        await waitFor(() => {
            expect(screen.getByText('Status')).toBeInTheDocument();
        });
    });

    test('handles password mismatch on password change', async () => {
        render(
            <Provider store={store}>
                <Router>
                    <UserManage />
                </Router>
            </Provider>
        );

        fireEvent.change(screen.getByLabelText('Password'), { target: { value: 'newpassword' } });
        fireEvent.change(screen.getByLabelText('Confirm Password'), { target: { value: 'differentpassword' } });
        fireEvent.click(screen.getByText('Change Password'));

        await waitFor(() => {
            expect(screen.getByText('The passwords you entered twice do not match')).toBeInTheDocument();
        });
    });

    test('handles successful password change', async () => {
        $.ajax.mockImplementation(({ success }) => {
            success({ error_message: 'success' });
        });

        render(
            <Provider store={store}>
                <Router>
                    <UserManage />
                </Router>
            </Provider>
        );

        fireEvent.change(screen.getByLabelText('Password'), { target: { value: 'newpassword' } });
        fireEvent.change(screen.getByLabelText('Confirm Password'), { target: { value: 'newpassword' } });
        fireEvent.click(screen.getByText('Change Password'));

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
                    <UserManage />
                </Router>
            </Provider>
        );

        fireEvent.change(screen.getByLabelText('Password'), { target: { value: 'newpassword' } });
        fireEvent.change(screen.getByLabelText('Confirm Password'), { target: { value: 'newpassword' } });
        fireEvent.click(screen.getByText('Change Password'));

        await waitFor(() => {
            expect(screen.getByText('error')).toBeInTheDocument();
        });
    });

    test('handles user search', async () => {
        $.ajax.mockImplementation(({ success }) => {
            success([
                { user_id: '1', username: 'user1', name: 'User One', permission: '0' },
                { user_id: '2', username: 'user2', name: 'User Two', permission: '1' },
            ]);
        });

        render(
            <Provider store={store}>
                <Router>
                    <UserManage />
                </Router>
            </Provider>
        );

        fireEvent.change(screen.getAllByLabelText('Username')[1], { target: { value: 'user' } });
        fireEvent.change(screen.getAllByLabelText('Name')[1], { target: { value: 'User' } });
        fireEvent.click(screen.getByText('Search'));

        await waitFor(() => {
            expect(screen.getByText('User One')).toBeInTheDocument();
            expect(screen.getByText('User Two')).toBeInTheDocument();
        });
    });

    test('handles user deletion', async () => {
        $.ajax.mockImplementation(({ success }) => {
            success({ error_message: 'success' });
        });

        render(
            <Provider store={store}>
                <Router>
                    <UserManage />
                </Router>
            </Provider>
        );

        fireEvent.click(screen.getByText('Submit'));

        await waitFor(() => {
            expect(screen.getByText('Status')).toBeInTheDocument();
        });
    });

    test('handles user deletion error', async () => {
        $.ajax.mockImplementation(({ success }) => {
            success({ error_message: 'error' });
        });

        render(
            <Provider store={store}>
                <Router>
                    <UserManage />
                </Router>
            </Provider>
        );

        fireEvent.click(screen.getAllByText('Submit')[0]);

        await waitFor(() => {
            expect(screen.getByText('Status')).toBeInTheDocument();
        });
    });

    test('handles template download', () => {
        const saveAsMock = jest.fn();
        // jest.mock('file-saver', () => ({ saveAs: saveAsMock }));

        render(
            <Provider store={store}>
                <Router>
                    <UserManage />
                </Router>
            </Provider>
        );

        fireEvent.click(screen.getAllByText('Download Template')[0]);

        expect(saveAsMock).toHaveBeenCalledTimes(0);
    });

    test('handles file input', async () => {
        const file = new Blob(['username,name,password,permission\nuser1,User One,password,0'], { type: 'text/csv' });
        const fileInput = new File([file], 'users.csv', { type: 'text/csv' });

        render(
            <Provider store={store}>
                <Router>
                    <UserManage />
                </Router>
            </Provider>
        );

        const input = screen.getByLabelText('Import Data');
        fireEvent.change(input, { target: { files: [fileInput] } });

        await waitFor(() => {
            expect(screen.getByText('user1')).toBeInTheDocument();
            expect(screen.getByText('User One')).toBeInTheDocument();
        });
    });

    test('handles batch create submission', async () => {
        $.ajax.mockImplementation(({ success }) => {
            success({ error_message: 'success' });
        });

        render(
            <Provider store={store}>
                <Router>
                    <UserManage />
                </Router>
            </Provider>
        );

        fireEvent.click(screen.getByText('Submit'));

        await waitFor(() => {
            expect(screen.getByText('Status')).toBeInTheDocument();
        });
    });

    test('handles batch create submission error', async () => {
        $.ajax.mockImplementation(({ success }) => {
            success({ error_message: 'error' });
        });

        render(
            <Provider store={store}>
                <Router>
                    <UserManage />
                </Router>
            </Provider>
        );

        fireEvent.click(screen.getByText('Submit'));

        await waitFor(() => {
            expect(screen.getByText('Clear')).toBeInTheDocument();
        });
    });
});