import React from 'react';
import { Paragraph } from 'grommet';

const Text = ({ size = "small", children }) =>
    <Paragraph size={size}
               margin="none"
               style={{ whiteSpace: 'nowrap' }}>
        {children}
    </Paragraph>;

export default Text;
