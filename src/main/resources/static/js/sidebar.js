// sidebar.js

// Sidebar Toggle Functionality
function toggleDropdown(menuId, event) {
  event.preventDefault();

  // Get the clicked dropdown options
  const options = document.getElementById(menuId);

  // Check if the dropdown is already open
  if (options.classList.contains('hidden')) {
    // If it's hidden, close all dropdowns first
    const allMenus = document.querySelectorAll('.sidebar ul');
    allMenus.forEach(menu => menu.classList.add('hidden'));

    // Then open the clicked dropdown
    options.classList.remove('hidden');

    // Save the active dropdown to localStorage
    localStorage.setItem('activeDropdown', menuId); // Set activeDropdown here
  } else {
    // If it's already open, close it
    options.classList.add('hidden');

    // Remove the active dropdown from localStorage
    localStorage.removeItem('activeDropdown');
  }
}

// Reset all dropdowns (default state)
function resetDropdowns() {
  const allMenus = document.querySelectorAll('.sidebar ul');
  allMenus.forEach(menu => menu.classList.add('hidden'));
}

// Restore the active dropdown state from localStorage
function restoreDropdownState() {
  const activeDropdown = localStorage.getItem('activeDropdown');
  if (activeDropdown) {
    const options = document.getElementById(activeDropdown);
    if (options) {
      options.classList.remove('hidden');
    }
  }
}

// Ensure all dropdowns are closed on page load, except the saved one
document.addEventListener('DOMContentLoaded', () => {
  resetDropdowns(); // Close all dropdowns initially
  restoreDropdownState(); // Open the saved dropdown if exists
});

// AJAX function to load page content dynamically
function loadPage(url) {
  // Send AJAX request
  fetch(url)
    .then(response => response.text())
    .then(html => {
      // Update the content area with the new page content
      document.querySelector('.content').innerHTML = html;

      // After loading the content, restore the sidebar dropdown state
      restoreDropdownState();
    })
    .catch(error => {
      console.log('Error loading page:', error);
    });
}

// Handle form submission and prevent page reload (for staying on the same view)
document.querySelectorAll('form').forEach(form => {
  form.addEventListener('submit', event => {
    event.preventDefault(); // Prevent default form submission

    const formData = new FormData(form);
    fetch(form.action, {
      method: form.method,
      body: formData,
    })
      .then(response => {
        if (response.ok) {
          // Reload the content (same page) after successful submission
          loadPage(window.location.pathname); // Reload current page content
        } else {
          console.error('Form submission failed.');
        }
      })
      .catch(error => {
        console.error('Error submitting form:', error);
      });
  });
});
