import React, { useState } from 'react';
import Button from '../atoms/Button';
import { post } from '../../redux/api';
import Copy from '../molecules/Copy';

const ApiKeyGenerator = () => {
    const [apiKey, setApiKey] = useState();
    return apiKey
        ? <Copy>{apiKey}</Copy>
        : <Button onClick={() => post('keys').then(({ raw }) => setApiKey(raw))}>New API key</Button>;
};

export default ApiKeyGenerator;
