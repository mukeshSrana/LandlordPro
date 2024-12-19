document.addEventListener('DOMContentLoaded', function() {
  // Check if the user is authenticated based on the data attribute
  const isAuthenticated = document.getElementById('authStatus').dataset.authenticated;

  // Show/Hide Login/Logout based on authentication
  if (isAuthenticated === 'true') {
    document.getElementById('loginLink').style.display = 'none';
    document.getElementById('logoutLink').style.display = 'inline-block';
  } else {
    document.getElementById('loginLink').style.display = 'inline-block';
    document.getElementById('logoutLink').style.display = 'none';
  }

  // Show/hide logout form based on authentication status
  const logoutLink = document.getElementById("logoutLink");
  const logoutForm = document.getElementById("logoutForm");

  logoutLink.addEventListener("click", function(event) {
    event.preventDefault();
    logoutForm.submit();  // Submit the form when the link is clicked
  });
});