function showUploadPopup(element) {
  const expenseId = element.getAttribute("data-id");
  document.getElementById('expenseId').value = expenseId;
  document.getElementById('uploadPopup').style.display = 'flex';
}

function closeUploadPopup() {
  document.getElementById('uploadPopup').style.display = 'none';
}
