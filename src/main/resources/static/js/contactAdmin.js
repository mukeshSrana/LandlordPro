function updateResolvedInput(selectElement) {
  const row = selectElement.closest('tr');
  const resolvedInput = row.querySelector('input[name="resolved"]');

  if (resolvedInput) {
    resolvedInput.value = selectElement.value;
  }
}

function updateDeletedInput(selectElement) {
  const row = selectElement.closest('tr');
  const deletedInput = row.querySelector('input[name="deleted"]');

  if (deletedInput) {
    deletedInput.value = selectElement.value;
  }
}


