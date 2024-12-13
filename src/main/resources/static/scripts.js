function toggleDropdown(menuId) {
  // Close all dropdowns
  document.querySelectorAll('.dropdown-content').forEach(dropdown => {
    dropdown.style.display = 'none';
  });

  // Toggle the selected dropdown
  const menu = document.getElementById(menuId);
  if (menu.style.display === 'block') {
    menu.style.display = 'none';
  } else {
    menu.style.display = 'block';
  }
}
