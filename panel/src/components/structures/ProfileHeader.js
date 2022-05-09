import React, { useEffect } from 'react';
import HorizontalBox from '../atoms/HorizontalBox';
import Avatar from '../atoms/Avatar';
import Button from '../atoms/Button';
import Text from '../atoms/Text';
import { useAuth0 } from '@auth0/auth0-react';
import { Logout } from 'grommet-icons';

const ProfileHeader = () => {
    const { loginWithRedirect, logout, user, isAuthenticated, getAccessTokenSilently } = useAuth0();

    useEffect(() => {
        if (isAuthenticated) {
            getAccessTokenSilently().then(token =>
                localStorage.setItem('token', token)
            );
        } else {
            localStorage.removeItem('token');
        }
    }, [getAccessTokenSilently, isAuthenticated]);

    return isAuthenticated
        ? <AuthenticatedHeader user={user} logout={() => logout({ returnTo: window.location.origin })}/>
        : <UnauthenticatedHeader login={loginWithRedirect}/>;
};

const AuthenticatedHeader = ({ user, logout }) =>
    <HorizontalBox justify="between" align="center">
        <Avatar url={user.picture} size="medium"/>
        <Text>{user.name}</Text>
        <Logout onClick={logout} cursor="pointer"/>
    </HorizontalBox>;

const UnauthenticatedHeader = ({ login }) =>
    <HorizontalBox justify="end">
        <Button onClick={login} size="small" primary>Login</Button>
    </HorizontalBox>;

export default ProfileHeader;
