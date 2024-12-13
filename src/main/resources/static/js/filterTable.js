
// JavaScript function to filter table rows
function filterTable() {
  const yearFilter = document.getElementById('year').value.trim();
  const apartmentFilter = document.getElementById('apartment').value.trim();
  const rows = document.querySelectorAll('table tbody tr');

  rows.forEach(row => {
    const year = row.querySelector('td:nth-child(1)').textContent.trim();
    const apartment = row.querySelector('td:nth-child(2)').textContent.trim();

    const matchesYear = !yearFilter || year === yearFilter;
    const matchesApartment = !apartmentFilter || apartment === apartmentFilter;

    row.style.display = (matchesYear && matchesApartment) ? '' : 'none';
  });
}

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

  // Perform the AJAX request (or submit the form)
  fetch('/expenses/update', {
    method: 'POST',
    body: formData
  })
    .then(response => response.json())
    .then(data => {
      // If successful, hide the Update button again
      if (data.success) {
        // button.style.display = 'none';
        alert('Updated the expense.');
      } else {
        alert('Error updating the expense.');
      }
    })
    .catch(error => {
      console.error('Error:', error);
    });
}