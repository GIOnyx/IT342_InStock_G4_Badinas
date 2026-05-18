import { Navigate } from 'react-router-dom';

function PrivateRoute({ children, allowedRoles }) {
  const token = localStorage.getItem('token');
  const user = JSON.parse(localStorage.getItem('user') || '{}');

  if (!token) {
    return <Navigate to="/login" replace />;
  }

  if (allowedRoles?.length && !allowedRoles.includes(user.role)) {
    return (
      <Navigate
        to="/"
        replace
        state={{
          toastMessage: 'Access Denied: Unauthorized Area',
          toastType: 'error',
        }}
      />
    );
  }

  return children;
}

export default PrivateRoute;
