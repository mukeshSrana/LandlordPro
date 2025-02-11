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
    const monthlyRentHiddenInput = row.querySelector('input[name="monthlyRent"]');
    const securityDepositHiddenInput = row.querySelector('input[name="securityDeposit"]');
    const securityDepositInstitutionNameHiddenInput = row.querySelector('input[name="securityDepositInstitutionName"]');

    // Update the hidden input values based on the edited cell content
    if (cell.cellIndex === 4 && monthlyRentHiddenInput) {
      monthlyRentHiddenInput.value = cell.textContent.trim();
    } else if (cell.cellIndex === 5 && securityDepositHiddenInput) {
      securityDepositHiddenInput.value = cell.textContent.trim();
    } else if (cell.cellIndex === 6 && securityDepositInstitutionNameHiddenInput) {
      securityDepositInstitutionNameHiddenInput.value = cell.textContent.trim();
    }
  });
}
function syncDateBeforeSubmit(form) {
  const row = form.closest("tr");
  const visibleLeaseStartDateInput = row.querySelector('input[name="visibleLeaseStartDate"]');
  const hiddenLeaseStartDateInput = form.querySelector('input[name="leaseStartDate"]');
  if (visibleLeaseStartDateInput && hiddenLeaseStartDateInput) {
    hiddenLeaseStartDateInput.value = visibleLeaseStartDateInput.value;
  }

  const visibleLeaseEndDateInput = row.querySelector('input[name="visibleLeaseEndDate"]');
  const hiddenLeaseEndDateInput = form.querySelector('input[name="leaseEndDate"]');
  if (visibleLeaseEndDateInput && hiddenLeaseEndDateInput) {
    hiddenLeaseEndDateInput.value = visibleLeaseEndDateInput.value;
  }
}


