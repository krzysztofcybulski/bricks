import React from 'react';
import { Avatar as GrommetAvatar } from 'grommet';

const Avatar = ({ url, size = "small" }) => <GrommetAvatar size={size} src={url} style={{ flexShrink: 0 }}/>;

export default Avatar;
