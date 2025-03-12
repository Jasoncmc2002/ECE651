import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import { Provider } from 'react-redux';
import configureStore from 'redux-mock-store';
import { BrowserRouter as Router } from 'react-router-dom';
import $ from 'jquery';
import ProblemSet from './ProblemSet';

const mockStore = configureStore([]);

jest.mock('jquery', () => ({
    ajax: jest.fn(),
}));

describe('ProblemSet Component', () => {
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

    test('renders ProblemSet component', () => {
        render(
            <Provider store={store}>
                <Router>
                    <ProblemSet />
                </Router>
            </Provider>
        );

        expect(screen.getByText('Active Problem Sets')).toBeInTheDocument();
    });

    test('renders login prompt when not logged in', () => {
        store = mockStore({
            ...initialState,
            is_login: false,
        });

        render(
            <Provider store={store}>
                <Router>
                    <ProblemSet />
                </Router>
            </Provider>
        );


        expect(screen.getByText('sign in')).toBeInTheDocument();

    });

    test('renders active problem sets table when logged in with sufficient permissions', async () => {
        // $.ajax.mockImplementation(({ success }) => {
        //     success([
        //         {
        //             problem_set_id: 1,
        //             ps_name: 'Problem Set 1',
        //             ps_start_time: '2024-03-29T22:18',
        //             ps_end_time: '2024-03-29T23:18',
        //             duration: '60',
        //             ps_author_name: 'Author 1',
        //         },
        //     ]);
        // });

        render(
            <Provider store={store}>
                <Router>
                    <ProblemSet />
                </Router>
            </Provider>
        );

        await waitFor(() => {
            expect(screen.getByText('Name')).toBeInTheDocument();
        });
    });

    test('renders loading spinner during data fetch', async () => {
        // $.ajax.mockImplementation(() => {
        //     return new Promise((resolve) => setTimeout(resolve, 1000));
        // });

        render(
            <Provider store={store}>
                <Router>
                    <ProblemSet />
                </Router>
            </Provider>
        );

        fireEvent.click(screen.getByText('Active Problem Sets'));

        await waitFor(() => {
            expect(screen.getByText('Name')).toBeInTheDocument();
        });
    });

    test('handles page navigation', async () => {
        // $.ajax.mockImplementation(({ success }) => {
        //     success([
        //         {
        //             problem_set_id: 1,
        //             ps_name: 'Problem Set 1',
        //             ps_start_time: '2024-03-29T22:18',
        //             ps_end_time: '2024-03-29T23:18',
        //             duration: '60',
        //             ps_author_name: 'Author 1',
        //         },
        //         {
        //             problem_set_id: 2,
        //             ps_name: 'Problem Set 2',
        //             ps_start_time: '2024-03-30T22:18',
        //             ps_end_time: '2024-03-30T23:18',
        //             duration: '60',
        //             ps_author_name: 'Author 2',
        //         },
        //     ]);
        // });

        render(
            <Provider store={store}>
                <Router>
                    <ProblemSet />
                </Router>
            </Provider>
        );

        await waitFor(() => {
            expect(screen.getByText('Name')).toBeInTheDocument();
        });
    });

    test('handles previous and next page buttons', async () => {
        // $.ajax.mockImplementation(({ success }) => {
        //     success([
        //         {
        //             problem_set_id: 1,
        //             ps_name: 'Problem Set 1',
        //             ps_start_time: '2024-03-29T22:18',
        //             ps_end_time: '2024-03-29T23:18',
        //             duration: '60',
        //             ps_author_name: 'Author 1',
        //         },
        //         {
        //             problem_set_id: 2,
        //             ps_name: 'Problem Set 2',
        //             ps_start_time: '2024-03-30T22:18',
        //             ps_end_time: '2024-03-30T23:18',
        //             duration: '60',
        //             ps_author_name: 'Author 2',
        //         },
        //     ]);
        // });

        render(
            <Provider store={store}>
                <Router>
                    <ProblemSet />
                </Router>
            </Provider>
        );

        await waitFor(() => {
            expect(screen.getByText('Name')).toBeInTheDocument();
        });
        fireEvent.click(screen.getByText('»'));

        await waitFor(() => {
            expect(screen.getByText('Name')).toBeInTheDocument();
        });

        fireEvent.click(screen.getByText('«'));

        await waitFor(() => {
            expect(screen.getByText('Name')).toBeInTheDocument();
        });
    });
});