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

  function deleteExpense(id, year, apartment) {
    const payload = {
      id: id,
      year: year,
      apartment: apartment
    };

    fetch('/expenses/delete', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(payload),
    })
      .then(response => {
        if (response.ok) {
          // Remove the row from the table
          const row = document.querySelector(
            `button[onclick="deleteExpense('${id}', '${year}', '${apartment}')"]`
          ).parentElement.parentElement;
          row.remove();
        } else {
          alert('Failed to delete the expense.');
        }
      })
      .catch(error => {
        console.error('Error:', error);
        alert('An error occurred while deleting the expense.');
      });
  }

