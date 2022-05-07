import React from 'react';
import { RangeInput } from 'grommet';

const Slider = ({ value, min = 0, max = 100, onChange = () => {} }) => <RangeInput
    value={value}
    min={min}
    max={max}
    onChange={event => {
        onChange(event.target.value);
    }}
/>

export default Slider;
