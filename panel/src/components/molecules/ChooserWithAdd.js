import React, { useState } from 'react';
import HorizontalBox from '../atoms/HorizontalBox';
import { Select } from 'grommet';
import Button from '../atoms/Button';

const ChooserWithAdd = ({ options, add, label = 'Add' }) => {
    const [selected, setSelected] = useState(options[0].value);
    return <HorizontalBox gap="small" pad="small">
        <Select
            options={options.map(b => b.name)}
            value={selected}
            onChange={({ option }) => setSelected(option.value)}
            style={{ flexGrow: 1 }}
        />
        <Button onClick={() => add(selected)}
                plain
                style={{ flexShrink: 1 }}>
            {label}
        </Button>
    </HorizontalBox>;
};

export default ChooserWithAdd;
