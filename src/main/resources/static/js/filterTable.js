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
    const dateHiddenInput = row.querySelector('input[name="date"]');
    const amountHiddenInput = row.querySelector('input[name="amount"]');
    const commentsHiddenInput = row.querySelector('input[name="comments"]');

    // Update the hidden input values based on the edited cell content
    if (cell.cellIndex === 1 && dateHiddenInput) {
      dateHiddenInput.value = cell.textContent.trim();
    } else if (cell.cellIndex === 2 && amountHiddenInput) {
      amountHiddenInput.value = cell.textContent.trim();
    } else if (cell.cellIndex === 4 && commentsHiddenInput) {
      commentsHiddenInput.value = cell.textContent.trim();
    }
  });
}
function syncDateBeforeSubmit(form) {
  const row = form.closest("tr");
  const visibleDateInput = row.querySelector('input[type="date"]');
  const hiddenDateInput = form.querySelector('input[name="date"]');
  if (visibleDateInput && hiddenDateInput) {
    hiddenDateInput.value = visibleDateInput.value;
  }
}
function updateStatus(selectElement) {
  const row = selectElement.closest('tr');
  const statusInput = row.querySelector('input[name="status"]');

  if (statusInput) {
    statusInput.value = selectElement.value;
  }
}

