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
  } else {
    // If it's already open, close it
    options.classList.add('hidden');
  }
}

// Reset all dropdowns (default state)
function resetDropdowns() {
  const allMenus = document.querySelectorAll('.sidebar ul');
  allMenus.forEach(menu => menu.classList.add('hidden'));
}

// Ensure all dropdowns are closed on page load
document.addEventListener('DOMContentLoaded', () => {
  resetDropdowns();
});
