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

    const row = cell.closest('tr');
    const mobileNumberInput = row.querySelector('input[name="mobileNumber"]');

    if (cell.cellIndex === 2 && mobileNumberInput) {
      mobileNumberInput.value = cell.textContent.trim();
    }
  });
}

function updateEnabledInput(selectElement) {
  const row = selectElement.closest('tr');
  const enabledInput = row.querySelector('input[name="enabled"]');

  if (enabledInput) {
    enabledInput.value = selectElement.value;
  }
}

function updateDeletedInput(selectElement) {
  const row = selectElement.closest('tr');
  const deletedInput = row.querySelector('input[name="deleted"]');

  if (deletedInput) {
    deletedInput.value = selectElement.value;
  }
}

function updateUserRole(selectElement) {
  const row = selectElement.closest('tr');
  const userRoleInput = row.querySelector('input[name="userRole"]');

  if (userRoleInput) {
    userRoleInput.value = selectElement.value;
  }
}

