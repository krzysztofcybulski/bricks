import React from 'react';
import { Button as GrommetButton } from 'grommet';

const Button = ({ children, ...props }) => <GrommetButton {...props} label={children} />;

export default Button;
