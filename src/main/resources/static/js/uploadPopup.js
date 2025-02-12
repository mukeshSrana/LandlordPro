function showUploadPopup(element) {
  const expenseId = element.getAttribute("data-id");
  document.getElementById('expenseId').value = expenseId;
  document.getElementById('uploadPopup').style.display = 'flex';
}

function closeUploadPopup() {
  document.getElementById('uploadPopup').style.display = 'none';
}

document.addEventListener("click", function(event) {
  const popup = document.getElementById("uploadPopup");
  const popupContent = document.querySelector(".upload-popup-content");

  if (event.target === popup) {
    closeUploadPopup();
  }
});
