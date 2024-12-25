function enableEditing(cell) {
  // Make sure only expense name and amount cells are editable
  if (cell.tagName !== "TD") return; // Ensure it's a table cell

  // Check if the cell is already editable
  if (cell.isContentEditable) return;

  // Set contentEditable to true for direct inline editing
  cell.contentEditable = true;
  cell.focus();

  // Add an event listener to update hidden inputs when editing is complete
  cell.addEventListener('blur', () => {
    cell.contentEditable = false; // Disable editing

    // Find the hidden input corresponding to this cell
    const row = cell.closest('tr');
    const apartmentShortNameHiddenInput = row.querySelector('input[name="apartmentShortName"]');
    const addressLine1HiddenInput = row.querySelector('input[name="addressLine1"]');
    const addressLine2HiddenInput = row.querySelector('input[name="addressLine2"]');
    const pincodeHiddenInput = row.querySelector('input[name="pincode"]');
    const cityHiddenInput = row.querySelector('input[name="city"]');
    const countryHiddenInput = row.querySelector('input[name="country"]');

    // Update the hidden input values based on the edited cell content
    if (cell.cellIndex === 0 && apartmentShortNameHiddenInput) {
      apartmentShortNameHiddenInput.value = cell.textContent.trim();
    } else if (cell.cellIndex === 1 && addressLine1HiddenInput) {
      addressLine1HiddenInput.value = cell.textContent.trim();
    } else if (cell.cellIndex === 2 && addressLine2HiddenInput) {
      addressLine2HiddenInput.value = cell.textContent.trim();
    } else if (cell.cellIndex === 3 && pincodeHiddenInput) {
      pincodeHiddenInput.value = cell.textContent.trim();
    } else if (cell.cellIndex === 4 && cityHiddenInput) {
      cityHiddenInput.value = cell.textContent.trim();
    } else if (cell.cellIndex === 5 && countryHiddenInput) {
      countryHiddenInput.value = cell.textContent.trim();
    }
  });
}


