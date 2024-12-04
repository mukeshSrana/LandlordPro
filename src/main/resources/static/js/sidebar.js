// Sidebar.js

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
    localStorage.setItem('activeDropdown', menuId);
  } else {
    // If it's already open, close it
    options.classList.add('hidden');

    // Remove the active dropdown from localStorage
    localStorage.removeItem('activeDropdown');
  }
}

// Highlight the selected sidebar option
function highlightSelectedOption(optionId) {
  // Remove the `selected` class from all sidebar links
  document.querySelectorAll('.sidebar a').forEach(link => {
    link.classList.remove('selected');
  });

  // Add the `selected` class to the selected option
  const selectedOption = document.getElementById(optionId);
  if (selectedOption) {
    selectedOption.classList.add('selected');
  }
}

// Clear specific localStorage keys only on application load/reload
function clearLocalStorageOnLoad() {
  localStorage.removeItem('activeDropdown');
  localStorage.removeItem('activeOption');
}

// Reset all dropdowns (default state)
function resetDropdowns() {
  const allMenus = document.querySelectorAll('.sidebar ul');
  allMenus.forEach(menu => menu.classList.add('hidden')); // Ensure all menus are hidden
}


// Restore the active dropdown and option state from localStorage
function restoreDropdownState() {
  const activeDropdown = localStorage.getItem('activeDropdown');
  const activeOption = localStorage.getItem('activeOption');

  if (activeDropdown) {
    const options = document.getElementById(activeDropdown);
    if (options) {
      options.classList.remove('hidden');
    }
  }

  if (activeOption) {
    highlightSelectedOption(activeOption);
  }
}

// Ensure actions are taken on page load
document.addEventListener('DOMContentLoaded', () => {
  clearLocalStorageOnLoad(); // Clear stored dropdown and option state
  resetDropdowns(); // Close all dropdowns initially
  restoreDropdownState(); // Open the saved dropdown and highlight the selected option if exists
});

// AJAX function to load page content dynamically
function loadPage(url, optionId) {
  // Send AJAX request
  fetch(url)
    .then(response => response.text())
    .then(html => {
      // Update the content area with the new page content
      document.querySelector('.content').innerHTML = html;

      // After loading the content, restore the sidebar dropdown state
      restoreDropdownState();

      // Highlight the selected option
      if (optionId) {
        localStorage.setItem('activeOption', optionId); // Save the selected option
        highlightSelectedOption(optionId);
      }
    })
    .catch(error => {
      console.log('Error loading page:', error);
    });
}

// Function to persist dropdown and view state after form submission
function persistStateAfterSubmission(menuId, activeOption) {
  localStorage.setItem('activeDropdown', menuId); // Keep the dropdown open
  localStorage.setItem('activeOption', activeOption); // Save the selected option
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
          // Persist dropdown state for "Expenses" and the active view
          persistStateAfterSubmission('expenses-menu', 'create-expense-option');

          // Reload the content (same page) after successful submission
          loadPage(window.location.pathname, 'create-expense-option'); // Reload current page content
        } else {
          console.error('Form submission failed.');
        }
      })
      .catch(error => {
        console.error('Error submitting form:', error);
      });
  });
});
