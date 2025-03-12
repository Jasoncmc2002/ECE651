import React from 'react';
import { render } from '@testing-library/react';
import NotFound from './NotFound';
import '@testing-library/jest-dom/extend-expect'; // for the additional matchers

jest.mock('./contents/ContentCard', () => (props) => <div>{props.children}</div>);
jest.mock('../images/icon-with-name.png', () => 'mocked-image');

describe('NotFound Component', () => {
    test('renders NotFound component correctly', () => {
        const { getByAltText, getByText } = render(<NotFound />);

        expect(getByAltText('Brand')).toBeInTheDocument();
        expect(getByText('404: Page does not exist')).toBeInTheDocument();
    });
});