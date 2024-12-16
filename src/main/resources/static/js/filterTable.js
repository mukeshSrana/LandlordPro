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

    // Update the hidden input values based on the edited cell content
    if (cell.cellIndex === 2 && nameHiddenInput) {
      nameHiddenInput.value = cell.textContent.trim();
    } else if (cell.cellIndex === 3 && amountHiddenInput) {
      amountHiddenInput.value = cell.textContent.trim();
    }
  });
}

function submitUpdate(button) {
  // Find the row containing the button
  const row = button.closest('tr');
  const expenseId = row.querySelector('input[name="id"]').value;
  const year = row.querySelector('input[name="year"]').value;
  const apartmentName = row.querySelector('input[name="apartmentName"]').value;
  const expenseName = row.querySelector('td:nth-child(3)').innerText;
  const amount = row.querySelector('td:nth-child(4)').innerText;

  // Collect the updated data to send in the form
  const formData = new FormData();
  formData.append('id', expenseId);
  formData.append('year', year);
  formData.append('apartmentName', apartmentName);
  formData.append('name', expenseName);
  formData.append('amount', amount);

  // Perform the AJAX request
  fetch('/update', {
    method: 'POST',
    body: formData
  })
    .then(response => response.json())
    .then(data => {
      if (data.success) {
        // Update the row with the new data
        const updatedExpense = data.updatedExpense;
        row.querySelector('td:nth-child(1)').innerText = updatedExpense.year;
        row.querySelector('td:nth-child(2)').innerText = updatedExpense.apartmentName;
        row.querySelector('td:nth-child(3)').innerText = updatedExpense.name;
        row.querySelector('td:nth-child(4)').innerText = updatedExpense.amount;

        alert('Expense updated successfully.');
      } else {
        alert('Error updating the expense: ' + (data.message || 'Unknown error.'));
      }
    })
    .catch(error => {
      console.error('Error:', error);
      alert('An unexpected error occurred.');
    });
}
