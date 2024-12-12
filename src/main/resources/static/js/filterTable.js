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
    if (cell.isContentEditable) return;

    // Store the original value in case of cancel
    const originalValue = cell.innerText;
    const row = cell.closest('tr');
    const modifyForm = row.querySelector('form#modifyForm');

    // Make the cell content editable
    cell.contentEditable = true;
    cell.focus();

    // Show Modify Form and make the Modify button visible
    modifyForm.style.display = 'inline';  // Show the Modify button next to the Delete button

    // Event listener to save changes and submit
    modifyForm.addEventListener('submit', function (e) {
      e.preventDefault();

      const modifiedName = document.getElementById('modifiedName').value;
      const modifiedAmount = document.getElementById('modifiedAmount').value;

      // If changes are made, submit the modified values
      if (modifiedName !== originalValue || modifiedAmount !== originalValue) {
        this.submit();  // Submit the form
      } else {
        // Revert changes if nothing has changed
        cell.innerText = originalValue;
      }

      // Disable content-editable after submission
      cell.contentEditable = false;
    });
  }

