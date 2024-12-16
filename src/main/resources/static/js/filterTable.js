function enableEditing(cell) {
  // Make sure only expense name and amount cells are editable
  if (cell.tagName !== "TD") return; // Ensure it's a table cell

  // Check if the cell is already editable
  if (cell.isContentEditable) return;

  // Set contentEditable to true for direct inline editing
  cell.contentEditable = true;
  cell.focus();

  // Display the Modify button for this row
  const row = cell.closest('tr');
  const updateButton = row.querySelector('.update-btn');
  //updateButton.style.display = 'inline';
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
