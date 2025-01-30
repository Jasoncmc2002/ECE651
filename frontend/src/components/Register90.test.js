import React from 'react';
import { render, fireEvent, screen, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import { BrowserRouter as Router } from 'react-router-dom';
import $ from 'jquery';
import Register from './Register';

jest.mock('jquery', () => ({
    ajax: jest.fn(),
}));

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