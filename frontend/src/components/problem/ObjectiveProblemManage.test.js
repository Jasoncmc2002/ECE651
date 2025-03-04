import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import { Provider } from 'react-redux';
import configureStore from 'redux-mock-store';
import { BrowserRouter as Router } from 'react-router-dom';
import $ from 'jquery';
import ObjectiveProblemManage from './ObjectiveProblemManage';

const mockStore = configureStore([]);

jest.mock('jquery', () => ({
    ajax: jest.fn(),
}));

describe('ObjectiveProblemManage Component', () => {
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

    test('renders ObjectiveProblemManage component', () => {
        render(
            <Provider store={store}>
                <Router>
                    <ObjectiveProblemManage />
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
                    <ObjectiveProblemManage />
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
                    <ObjectiveProblemManage />
                </Router>
            </Provider>
        );

        expect(screen.getByText('You do not have permission to access this page')).toBeInTheDocument();
    });

    test('renders objective problems table when logged in with sufficient permissions', async () => {
        $.ajax.mockImplementation(({ success }) => {
            success([
                {
                    objective_problem_id: 1,
                    op_description: 'Description 1',
                    op_tag: 'Tag 1',
                    op_total_score: 10,
                    op_difficulty: 'Easy',
                    op_use_count: 5,
                    op_author_name: 'Author 1',
                },
            ]);
        });

        render(
            <Provider store={store}>
                <Router>
                    <ObjectiveProblemManage />
                </Router>
            </Provider>
        );

        await waitFor(() => {
            expect(screen.getByText('Description 1')).toBeInTheDocument();
        });
    });

    test('renders loading spinner during data fetch', async () => {
        $.ajax.mockImplementation(() => {
            return new Promise((resolve) => setTimeout(resolve, 1000));
        });

        render(
            <Provider store={store}>
                <Router>
                    <ObjectiveProblemManage />
                </Router>
            </Provider>
        );

        fireEvent.click(screen.getByText('Objective Problems'));

        expect(screen.getByRole('toolbar')).toBeInTheDocument();
    });

    test('handles page navigation', async () => {
        $.ajax.mockImplementation(({ success }) => {
            success([
                {
                    objective_problem_id: 1,
                    op_description: 'Description 1',
                    op_tag: 'Tag 1',
                    op_total_score: 10,
                    op_difficulty: 'Easy',
                    op_use_count: 5,
                    op_author_name: 'Author 1',
                },
                {
                    objective_problem_id: 2,
                    op_description: 'Description 2',
                    op_tag: 'Tag 2',
                    op_total_score: 20,
                    op_difficulty: 'Medium',
                    op_use_count: 10,
                    op_author_name: 'Author 2',
                },
            ]);
        });

        render(
            <Provider store={store}>
                <Router>
                    <ObjectiveProblemManage />
                </Router>
            </Provider>
        );

        await waitFor(() => {
            expect(screen.getByText('Description 1')).toBeInTheDocument();
            expect(screen.getByText('Description 2')).toBeInTheDocument();
        });

        fireEvent.click(screen.getByText('2'));

        await waitFor(() => {
            expect(screen.getByText('Description 2')).toBeInTheDocument();
        });
    });

    test('handles previous and next page buttons', async () => {
        $.ajax.mockImplementation(({ success }) => {
            success([
                {
                    objective_problem_id: 1,
                    op_description: 'Description 1',
                    op_tag: 'Tag 1',
                    op_total_score: 10,
                    op_difficulty: 'Easy',
                    op_use_count: 5,
                    op_author_name: 'Author 1',
                },
                {
                    objective_problem_id: 2,
                    op_description: 'Description 2',
                    op_tag: 'Tag 2',
                    op_total_score: 20,
                    op_difficulty: 'Medium',
                    op_use_count: 10,
                    op_author_name: 'Author 2',
                },
            ]);
        });

        render(
            <Provider store={store}>
                <Router>
                    <ObjectiveProblemManage />
                </Router>
            </Provider>
        );

        await waitFor(() => {
            expect(screen.getByText('Description 1')).toBeInTheDocument();
            expect(screen.getByText('Description 2')).toBeInTheDocument();
        });

        fireEvent.click(screen.getByText('»'));

        await waitFor(() => {
            expect(screen.getByText('Description 2')).toBeInTheDocument();
        });

        fireEvent.click(screen.getByText('«'));

        await waitFor(() => {
            expect(screen.getByText('Description 1')).toBeInTheDocument();
        });
    });
});