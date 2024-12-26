function enableEditing(cell) {
  // Make sure only expense name and amount cells are editable
  if (cell.tagName !== "TD") return; // Ensure it's a table cell

  // Check if the cell is already editable
  if (cell.isContentEditable) return;

  // Set contentEditable to true for direct inline editing
  cell.contentEditable = true;
  cell.focus();

  const validationPattern = /^[a-zA-Z0-9]+$/;

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
    const newValue = cell.textContent.trim();
    if (cell.cellIndex === 0 && apartmentShortNameHiddenInput) {
      if (validationPattern.test(newValue)) {
        apartmentShortNameHiddenInput.value = newValue;
      } else {
        alert("Invalid input for apartment short name. Only alphanumeric characters are allowed.");
        cell.textContent = apartmentShortNameHiddenInput.value; // Revert to the original value
      }
    } else if (cell.cellIndex === 2 && addressLine1HiddenInput) {
      addressLine1HiddenInput.value = cell.textContent.trim();
    } else if (cell.cellIndex === 3 && addressLine2HiddenInput) {
      addressLine2HiddenInput.value = cell.textContent.trim();
    } else if (cell.cellIndex === 4 && pincodeHiddenInput) {
      pincodeHiddenInput.value = cell.textContent.trim();
    } else if (cell.cellIndex === 5 && cityHiddenInput) {
      cityHiddenInput.value = cell.textContent.trim();
    } else if (cell.cellIndex === 6 && countryHiddenInput) {
      countryHiddenInput.value = cell.textContent.trim();
    }
  });
}


