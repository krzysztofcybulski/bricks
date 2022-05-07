import React from 'react';
import HorizontalBox from '../atoms/HorizontalBox';
import Text from '../atoms/Text';

const Properties = ({ properties, size }) => {
    const texts = properties.filter(prop => prop !== undefined);
    return <HorizontalBox gap="xsmall">
        {texts
            .map((prop, index) =>
                <HorizontalBox gap="xsmall" pad="none" key={prop}>
                    <Text size={size}>{prop}</Text>
                    {index < texts.length - 1 && <Text>·</Text>}
                </HorizontalBox>
            )}
    </HorizontalBox>;
};

export default Properties;
