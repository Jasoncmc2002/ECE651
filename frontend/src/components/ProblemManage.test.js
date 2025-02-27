import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import { Provider } from 'react-redux';
import configureStore from 'redux-mock-store';
import { BrowserRouter as Router } from 'react-router-dom';
import ProblemManage from './ProblemManage';

const mockStore = configureStore([]);

describe('ProblemManage Component', () => {
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

    test('renders ProblemManage component', () => {
        render(
            <Provider store={store}>
                <Router>
                    <ProblemManage />
                </Router>
            </Provider>
        );

        expect(screen.getByText('Objective Problems')).toBeInTheDocument();
    });

    test('renders login prompt when not logged in', () => {
        store = mockStore({
            ...initialState,
            is_login: false,
        });

        render(
            <Provider store={store}>
                <Router>
                    <ProblemManage />
                </Router>
            </Provider>
        );

        expect(screen.getByText('sign in')).toBeInTheDocument();
    });

    test('renders permission denied message when logged in with insufficient permissions', () => {
        store = mockStore({
            ...initialState,
            permission: 0,
        });

        render(
            <Provider store={store}>
                <Router>
                    <ProblemManage />
                </Router>
            </Provider>
        );

        expect(screen.getByText('You do not have permission to access this page')).toBeInTheDocument();
    });

    test('renders objective and programming management links when logged in with sufficient permissions', () => {
        render(
            <Provider store={store}>
                <Router>
                    <ProblemManage />
                </Router>
            </Provider>
        );

        expect(screen.getByText('Objective Problems')).toBeInTheDocument();
        expect(screen.getByText('Programming Problems')).toBeInTheDocument();
    });

    test('renders greeting based on time of day', () => {
        const mockDate = new Date(2021, 1, 1, 10); // 10 AM
        jest.spyOn(global, 'Date').mockImplementation(() => mockDate);

        render(
            <Provider store={store}>
                <Router>
                    <ProblemManage />
                </Router>
            </Provider>
        );

        expect(screen.getByText('Good morning, Test User')).toBeInTheDocument();

        jest.spyOn(global, 'Date').mockRestore();
    });

    test('renders different greeting based on time of day', () => {
        const mockDate = new Date(2021, 1, 1, 19); // 7 PM
        jest.spyOn(global, 'Date').mockImplementation(() => mockDate);

        render(
            <Provider store={store}>
                <Router>
                    <ProblemManage />
                </Router>
            </Provider>
        );

        expect(screen.getByText('Good evening, Test User')).toBeInTheDocument();

        jest.spyOn(global, 'Date').mockRestore();
    });
});