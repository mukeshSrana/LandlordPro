// sidebar.js

// Sidebar Toggle Functionality
function toggleDropdown(menuId, event) {
  event.preventDefault();

  // Close all dropdowns
  const allMenus = document.querySelectorAll('.sidebar ul');
  allMenus.forEach(menu => menu.classList.add('hidden'));

  // Open the clicked dropdown
  const options = document.getElementById(menuId);
  options.classList.toggle('hidden');
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
