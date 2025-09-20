import { render, screen } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import App from '../../../app/App';

test('renders the navbar and home lessonPage by default', () => {
  render(
    <BrowserRouter>
      <App />
    </BrowserRouter>
  );

  // Check for Navbar brand
  const brandElement = screen.getByText(/Language Learning Tools/i);
  expect(brandElement).toBeInTheDocument();

  // Check for Home lessonPage content
  const homeHeading = screen.getByText(/Welcome!/i);
  expect(homeHeading).toBeInTheDocument();
});
