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
    const rolesHiddenInput = row.querySelector('input[name="roles"]');
    const mobileNumberHiddenInput = row.querySelector('input[name="mobileNumber"]');
    const enabledNumberHiddenInput = row.querySelector('input[name="enabled"]');

    // Update the hidden input values based on the edited cell content
    if (cell.cellIndex === 2 && rolesHiddenInput) {
      rolesHiddenInput.value = cell.textContent.trim();
    } else if (cell.cellIndex === 3 && mobileNumberHiddenInput) {
      mobileNumberHiddenInput.value = cell.textContent.trim();
    } else if (cell.cellIndex === 3 && enabledNumberHiddenInput) {
      enabledNumberHiddenInput.value = cell.textContent.trim();
    }
  });
}

function updateHiddenEnabledInput(selectElement) {
  const row = selectElement.closest('tr');
  const hiddenEnabledInput = row.querySelector('input[name="enabled"]');

  if (hiddenEnabledInput) {
    hiddenEnabledInput.value = selectElement.value; // Update hidden input value
  }
}

