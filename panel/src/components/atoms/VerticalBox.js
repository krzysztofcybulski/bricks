import React from 'react';
import { Box } from 'grommet';

const VerticalBox = ({ children, style, ...props }) =>
    <Box direction="column" {...props} style={style}>
        {children}
    </Box>;

export default VerticalBox;
