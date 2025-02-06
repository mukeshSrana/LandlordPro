// function enableEditing(cell) {
//   // Make sure only expense name and amount cells are editable
//   if (cell.tagName !== "TD") return; // Ensure it's a table cell
//
//   // Check if the cell is already editable
//   if (cell.isContentEditable) return;
//
//   // Set contentEditable to true for direct inline editing
//   cell.contentEditable = true;
//   cell.focus();
//
//   const validationPattern = /^[a-zA-Z0-9]+$/;
//
//   // Add an event listener to update hidden inputs when editing is complete
//   cell.addEventListener('blur', () => {
//     cell.contentEditable = false; // Disable editing
//
//     // Find the hidden input corresponding to this cell
//     const row = cell.closest('tr');
//     const apartmentShortNameHiddenInput = row.querySelector('input[name="apartmentShortName"]');
//     const addressLine1HiddenInput = row.querySelector('input[name="addressLine1"]');
//     const addressLine2HiddenInput = row.querySelector('input[name="addressLine2"]');
//     const pincodeHiddenInput = row.querySelector('input[name="pincode"]');
//
//     // Update the hidden input values based on the edited cell content
//     const newValue = cell.textContent.trim();
//     if (cell.cellIndex === 0 && apartmentShortNameHiddenInput) {
//       if (validationPattern.test(newValue)) {
//         apartmentShortNameHiddenInput.value = newValue;
//       } else {
//         alert("Invalid input for apartment short name. Only alphanumeric characters are allowed.");
//         cell.textContent = apartmentShortNameHiddenInput.value; // Revert to the original value
//       }
//     } else if (cell.cellIndex === 1 && addressLine1HiddenInput) {
//       addressLine1HiddenInput.value = cell.textContent.trim();
//     } else if (cell.cellIndex === 2 && addressLine2HiddenInput) {
//       addressLine2HiddenInput.value = cell.textContent.trim();
//     } else if (cell.cellIndex === 3 && pincodeHiddenInput) {
//       pincodeHiddenInput.value = cell.textContent.trim();
//     }
//   });
// }

function enableEditing(cell) {
  // Make sure it's a valid table cell
  if (cell.tagName !== "TD") return;

  // Prevent editing if it's already editable
  if (cell.isContentEditable) return;

  // Set contentEditable to true for inline editing
  cell.contentEditable = true;
  cell.focus();

  const validationPattern = /^[a-zA-Z0-9]+$/;

  // Add an event listener to handle blur (when editing is complete)
  cell.addEventListener('blur', async () => {
    cell.contentEditable = false; // Disable editing

    // Find the corresponding hidden input for the cell
    const row = cell.closest('tr');
    const apartmentShortNameHiddenInput = row.querySelector('input[name="apartmentShortName"]');
    const addressLine1HiddenInput = row.querySelector('input[name="addressLine1"]');
    const addressLine2HiddenInput = row.querySelector('input[name="addressLine2"]');
    const pincodeHiddenInput = row.querySelector('input[name="pincode"]');
    const cityHiddenInput = row.querySelector('input[name="city"]');

    // Update hidden inputs based on edited cell
    const newValue = cell.textContent.trim();
    if (cell.cellIndex === 0 && apartmentShortNameHiddenInput) {
      if (validationPattern.test(newValue)) {
        apartmentShortNameHiddenInput.value = newValue;
      } else {
        alert("Invalid input for apartment short name. Only alphanumeric characters are allowed.");
        cell.textContent = apartmentShortNameHiddenInput.value; // Revert to original value
      }
    } else if (cell.cellIndex === 1 && addressLine1HiddenInput) {
      addressLine1HiddenInput.value = newValue;
    } else if (cell.cellIndex === 2 && addressLine2HiddenInput) {
      addressLine2HiddenInput.value = newValue;
    } else if (cell.cellIndex === 3 && pincodeHiddenInput) {
      const pincode = newValue;
      pincodeHiddenInput.value = pincode; // Update hidden pincode input

      // Fetch the city based on the pincode
      if (pincode.length >= 4) {
        try {
          const response = await fetch(`/api/pincode/${pincode}`);
          const city = response.ok ? await response.text() : "City not found";
          cityHiddenInput.value = city; // Update hidden city input
          const cityCell = row.querySelector('td:nth-child(5)'); // Find the city cell
          cityCell.textContent = city; // Update the city cell content
        } catch (error) {
          console.error("Error fetching city:", error);
          cityHiddenInput.value = "Error fetching city";
          const cityCell = row.querySelector('td:nth-child(5)');
          cityCell.textContent = "Error fetching city"; // Show error in the city cell
        }
      } else {
        cityHiddenInput.value = "";
        const cityCell = row.querySelector('td:nth-child(5)');
        cityCell.textContent = ""; // Clear the city cell if pincode is invalid
      }
    }
  });
}

function validateForm(event) {
  const cityInput = document.querySelector('input[name="city"]');
  if (cityInput.value === "City not found" || cityInput.value === "Error fetching city" || cityInput.value === "") {
    event.preventDefault(); // Prevent form submission
    alert("Please provide a valid pincode to get a city.");
  }
}


