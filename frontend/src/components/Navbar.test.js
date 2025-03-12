import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import { Provider } from 'react-redux';
import configureStore from 'redux-mock-store';
import { BrowserRouter as Router } from 'react-router-dom';
import Navbar from './Navbar';
import ACTIONS from '../redux/actions';

const mockStore = configureStore([]);

describe('Navbar Component', () => {
    let store;
    let initialState;

    beforeEach(() => {
        initialState = {
            user_id: null,
            username: '',
            name: '',
            permission: 0,
            token: '',
            is_login: false,
        };
        store = mockStore(initialState);
    });

    test('renders Navbar component when not logged in', () => {
        render(
            <Provider store={store}>
                <Router>
                    <Navbar />
                </Router>
            </Provider>
        );

        expect(screen.getByText('Python Programming Platform')).toBeInTheDocument();
        expect(screen.queryByText('Sign Out')).not.toBeInTheDocument();
    });

    test('renders Navbar component when logged in', () => {
        store = mockStore({
            ...initialState,
            is_login: true,
            name: 'Test User',
        });

        render(
            <Provider store={store}>
                <Router>
                    <Navbar />
                </Router>
            </Provider>
        );

        expect(screen.getByText('Python Programming Platform')).toBeInTheDocument();
        expect(screen.getByText('Test User')).toBeInTheDocument();
        expect(screen.getByText('Sign Out')).toBeInTheDocument();
        expect(screen.queryByText('Sign In')).not.toBeInTheDocument();
        expect(screen.queryByText('Register')).not.toBeInTheDocument();
    });

    test('handles logout', () => {
        store = mockStore({
            ...initialState,
            is_login: true,
            name: 'Test User',
        });

        render(
            <Provider store={store}>
                <Router>
                    <Navbar />
                </Router>
            </Provider>
        );

        fireEvent.click(screen.getByText('Sign Out'));

        const actions = store.getActions();
        expect(actions).toEqual([{ type: ACTIONS.LOGOUT }]);
    });

    test('renders permission-based links for permission level 1', () => {
        store = mockStore({
            ...initialState,
            is_login: true,
            permission: 1,
        });

        render(
            <Provider store={store}>
                <Router>
                    <Navbar />
                </Router>
            </Provider>
        );

        expect(screen.getByText('Problem Management')).toBeInTheDocument();
        expect(screen.getByText('Problem Set Management')).toBeInTheDocument();
    });

    test('renders permission-based links for permission level greater than 1', () => {
        store = mockStore({
            ...initialState,
            is_login: true,
            permission: 2,
        });

        render(
            <Provider store={store}>
                <Router>
                    <Navbar />
                </Router>
            </Provider>
        );

        expect(screen.getByText('Problem Management')).toBeInTheDocument();
        expect(screen.getByText('Problem Set Management')).toBeInTheDocument();
        expect(screen.getByText('User Management')).toBeInTheDocument();
        expect(screen.getByText('Developer')).toBeInTheDocument();
    });

    test('renders developer links for permission level greater than 1', () => {
        store = mockStore({
            ...initialState,
            is_login: true,
            permission: 2,
        });

        render(
            <Provider store={store}>
                <Router>
                    <Navbar />
                </Router>
            </Provider>
        );

        fireEvent.click(screen.getByText('Developer'));

        expect(screen.getByText('404')).toBeInTheDocument();
        expect(screen.getByText('User Profile')).toBeInTheDocument();
        expect(screen.getByText('Editor Demo')).toBeInTheDocument();
        expect(screen.getByText('Programming Editor Demo')).toBeInTheDocument();
        expect(screen.getByText('Markdown Editor Demo')).toBeInTheDocument();
    });
});