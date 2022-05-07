import { Auth0Provider } from '@auth0/auth0-react';

const AuthProvider = ({ children }) =>
    <Auth0Provider
        domain="bricks-game.eu.auth0.com"
        clientId="Q7rI3YVzRN6MW7D3xGmLFufNxxXCUIkn"
        audience="https://bricks.kcybulski.me/"
        redirectUri={window.location.origin}>
        {children}
    </Auth0Provider>;

export default AuthProvider;
