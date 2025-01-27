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
    const nameHiddenInput = row.querySelector('input[name="name"]');
    const amountHiddenInput = row.querySelector('input[name="amount"]');
    const expenseLocationHiddenInput = row.querySelector('input[name="expenseLocation"]');

    // Update the hidden input values based on the edited cell content
    if (cell.cellIndex === 1 && nameHiddenInput) {
      nameHiddenInput.value = cell.textContent.trim();
    } else if (cell.cellIndex === 2 && amountHiddenInput) {
      amountHiddenInput.value = cell.textContent.trim();
    } else if (cell.cellIndex === 3 && expenseLocationHiddenInput) {
      expenseLocationHiddenInput.value = cell.textContent.trim();
    }
  });
}