import React from 'react';
import { Box } from 'grommet';

const HorizontalBox = ({ children, style, ...props }) =>
    <Box direction="row" {...props} style={style}>
        {children}
    </Box>;

export default HorizontalBox;
