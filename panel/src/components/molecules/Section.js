import React from 'react';
import Text from '../atoms/Text';
import VerticalBox from '../atoms/VerticalBox';

const Section = ({ label, children }) =>
    <VerticalBox gap="small" flex={{ shrink: 0 }}>
        <Text>{label}</Text>
        {children}
    </VerticalBox>;

export default Section;
