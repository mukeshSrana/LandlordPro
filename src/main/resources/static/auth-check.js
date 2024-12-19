document.addEventListener('DOMContentLoaded', function() {
  // Check if the user is authenticated based on the data attribute
  const isAuthenticated = document.getElementById('authStatus').dataset.authenticated;
  console.log('Authenticated:', isAuthenticated);  // Check if this prints true or false

  // Show/Hide Login/Logout based on authentication
  if (isAuthenticated === 'true') {
    document.getElementById('loginLink').style.display = 'none';
    document.getElementById('logoutLink').style.display = 'inline-block';
  } else {
    document.getElementById('loginLink').style.display = 'inline-block';
    document.getElementById('logoutLink').style.display = 'none';
  }
});
