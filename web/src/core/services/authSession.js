export function logoutFromAppAndGoogle() {
  localStorage.removeItem('token');
  localStorage.removeItem('user');

  const googleLogoutWindow = window.open(
    'https://accounts.google.com/Logout',
    '_blank',
    'noopener,noreferrer,width=520,height=640'
  );

  window.location.assign('/login');

  if (googleLogoutWindow) {
    googleLogoutWindow.focus();
  }
}