import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import { Provider } from 'react-redux';
import configureStore from 'redux-mock-store';
import { BrowserRouter as Router } from 'react-router-dom';
import $ from 'jquery';
import ProgrammingManage from './ProgrammingManage';

const mockStore = configureStore([]);

jest.mock('jquery', () => ({
    ajax: jest.fn(),
}));

describe('ProgrammingManage Component', () => {
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

    test('renders ProgrammingManage component', () => {
        render(
            <Provider store={store}>
                <Router>
                    <ProgrammingManage />
                </Router>
            </Provider>
        );

        expect(screen.getByText('Programming Problems')).toBeInTheDocument();
    });

    test('renders login prompt when not logged in', () => {
        store = mockStore({
            ...initialState,
            is_login: false,
        });

        render(
            <Provider store={store}>
                <Router>
                    <ProgrammingManage />
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
                    <ProgrammingManage />
                </Router>
            </Provider>
        );

        expect(screen.getByText('You do not have permission to access this page')).toBeInTheDocument();
    });

    test('renders programming problems table when logged in with sufficient permissions', async () => {
        $.ajax.mockImplementation(({ success }) => {
            success([
                {
                    programming_id: 1,
                    p_title: 'Title 1',
                    p_tag: 'Tag 1',
                    p_total_score: 10,
                    p_difficulty: 'Easy',
                    p_use_count: 5,
                    p_author_name: 'Author 1',
                },
            ]);
        });

        render(
            <Provider store={store}>
                <Router>
                    <ProgrammingManage />
                </Router>
            </Provider>
        );

        await waitFor(() => {
            expect(screen.getByText('Title 1')).toBeInTheDocument();
        });
    });

    test('renders loading spinner during data fetch', async () => {
        $.ajax.mockImplementation(() => {
            return new Promise((resolve) => setTimeout(resolve, 1000));
        });

        render(
            <Provider store={store}>
                <Router>
                    <ProgrammingManage />
                </Router>
            </Provider>
        );

        fireEvent.click(screen.getByText('Programming Problems'));

        expect(screen.getByText("Author")).toBeInTheDocument();
    });

    test('handles page navigation', async () => {
        $.ajax.mockImplementation(({ success }) => {
            success([
                {
                    programming_id: 1,
                    p_title: 'Title 1',
                    p_tag: 'Tag 1',
                    p_total_score: 10,
                    p_difficulty: 'Easy',
                    p_use_count: 5,
                    p_author_name: 'Author 1',
                },
                {
                    programming_id: 2,
                    p_title: 'Title 2',
                    p_tag: 'Tag 2',
                    p_total_score: 20,
                    p_difficulty: 'Medium',
                    p_use_count: 10,
                    p_author_name: 'Author 2',
                },
            ]);
        });

        render(
            <Provider store={store}>
                <Router>
                    <ProgrammingManage />
                </Router>
            </Provider>
        );

        await waitFor(() => {
            expect(screen.getByText('Title 1')).toBeInTheDocument();
            expect(screen.getByText('Title 2')).toBeInTheDocument();
        });

        fireEvent.click(screen.getByText('2'));

        await waitFor(() => {
            expect(screen.getByText('Title 2')).toBeInTheDocument();
        });
    });

    test('handles previous and next page buttons', async () => {
        $.ajax.mockImplementation(({ success }) => {
            success([
                {
                    programming_id: 1,
                    p_title: 'Title 1',
                    p_tag: 'Tag 1',
                    p_total_score: 10,
                    p_difficulty: 'Easy',
                    p_use_count: 5,
                    p_author_name: 'Author 1',
                },
                {
                    programming_id: 2,
                    p_title: 'Title 2',
                    p_tag: 'Tag 2',
                    p_total_score: 20,
                    p_difficulty: 'Medium',
                    p_use_count: 10,
                    p_author_name: 'Author 2',
                },
            ]);
        });

        render(
            <Provider store={store}>
                <Router>
                    <ProgrammingManage />
                </Router>
            </Provider>
        );

        await waitFor(() => {
            expect(screen.getByText('Title 1')).toBeInTheDocument();
            expect(screen.getByText('Title 2')).toBeInTheDocument();
        });

        fireEvent.click(screen.getByText('»'));

        await waitFor(() => {
            expect(screen.getByText('Title 2')).toBeInTheDocument();
        });

        fireEvent.click(screen.getByText('«'));

        await waitFor(() => {
            expect(screen.getByText('Title 1')).toBeInTheDocument();
        });
    });
});