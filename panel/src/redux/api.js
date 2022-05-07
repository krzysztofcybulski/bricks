const address = process.env.REACT_APP_API ? `https://${process.env.REACT_APP_API}` : 'http://localhost:5050';
export const wsAddress = process.env.REACT_APP_API ? `wss://${process.env.REACT_APP_API}` : 'ws://localhost:5050';

export const get = async (path) => {
    const response = await fetch(`${address}/${path}`);
    return await response.json();
};

export const post = async (path, body = {}) => {
    const response = await fetch(`${address}/${path}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${localStorage.getItem('token')}`
        },
        body: JSON.stringify(body)
    });
    return await response.json();
};
