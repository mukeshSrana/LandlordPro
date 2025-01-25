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
    const commentsHiddenInput = row.querySelector('input[name="comments"]');2

    // Update the hidden input values based on the edited cell content
    if (cell.cellIndex === 3 && dateHiddenInput) {
      dateHiddenInput.value = cell.textContent.trim();
    } else if (cell.cellIndex === 4 && amountHiddenInput) {
      amountHiddenInput.value = cell.textContent.trim();
    } else if (cell.cellIndex === 6 && commentsHiddenInput) {
      commentsHiddenInput.value = cell.textContent.trim();
    }
  });
}

// Add a change event listener for the dropdown
document.addEventListener('DOMContentLoaded', () => {
  const dropdowns = document.querySelectorAll('select[name="status"]');
  dropdowns.forEach((dropdown) => {
    dropdown.addEventListener('change', (event) => {
      const row = event.target.closest('tr');
      const statusHiddenInput = row.querySelector('input[name="status"]');
      if (statusHiddenInput) {
        statusHiddenInput.value = event.target.value;
      }
    });
  });
});
