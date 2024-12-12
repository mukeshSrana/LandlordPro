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

