import jwt_decode from 'jwt-decode';

export const hasPermission = (permission) => {
    const token = localStorage.getItem('token');
    if (!token) {
        return false;
    }
    const { permissions } = jwt_decode(localStorage.getItem('token'));
    return permissions.indexOf(permission) >= 0;
};
