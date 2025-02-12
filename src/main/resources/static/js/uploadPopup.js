function showUploadPopup(element) {
  const expenseId = element.getAttribute("expenseId");
  const apartmentId = element.getAttribute("apartmentId");
  const year = element.getAttribute("year");
  document.getElementById('expenseId').value = expenseId;
  document.getElementById('apartmentId').value = apartmentId;
  document.getElementById('year').value = year;
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
