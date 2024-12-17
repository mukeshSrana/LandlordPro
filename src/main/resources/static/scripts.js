// Function to toggle the visibility of the dropdown
function toggleDropdown(menuId) {
  // Close all dropdowns except the one being clicked
  document.querySelectorAll('.dropdown-content').forEach(dropdown => {
    if (dropdown.id !== menuId) {
      dropdown.style.display = 'none'; // Close other dropdowns
    }
  });

  // Toggle the selected dropdown (open if closed, close if open)
  const menu = document.getElementById(menuId);
  if (menu.style.display === 'block') {
    menu.style.display = 'none'; // Close it if it's open
  } else {
    menu.style.display = 'block'; // Open it if it's closed
  }
}

// Function to handle selection (without closing the dropdown)
function handleSelection(menuId) {
  // No need to close the dropdown here; it stays open
  // Add active class to the selected link (optional)
  document.querySelectorAll(`#${menuId} a`).forEach(link => {
    link.classList.add('active');
  });
  const menu = document.getElementById(menuId);
  menu.style.display = 'none';
}

document.addEventListener('DOMContentLoaded', function() {
  // Check the 'currentPage' variable (injected from Thymeleaf) and open the corresponding dropdown
  if (currentPage === 'registerExpense' || currentPage === 'handleExpense') {
    toggleDropdown('expenses-options');
  }
  if (currentPage === 'registerApartment' || currentPage === 'handleApartment') {
    toggleDropdown('apartment-options');
  }
  if (currentPage === 'registerIncome' || currentPage === 'handleIncome') {
    toggleDropdown('income-options');
  }
  if (currentPage === 'report-1' || currentPage === 'report-2') {
    toggleDropdown('report-options');
  }
});


